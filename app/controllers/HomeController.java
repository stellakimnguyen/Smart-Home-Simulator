package controllers;

import models.*;
import models.Observable;
import models.devices.*;
import models.exceptions.*;
import models.modules.*;
import models.permissions.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Files.TemporaryFile;
import play.mvc.*;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Contains all actions that handle HTTP requests from the Users and the instance of [[models.modules.SHS SHS]].
 * ===Attributes===
 * `formFactory (private final FormFactory):` Helper to create HTML forms.
 *
 * `shs (private final [[models.modules.SHS SHS]]):` Singleton instance of SHS.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class HomeController extends Controller {
  private final FormFactory formFactory;
  public final SHS shs = SHS.getInstance();
  public final SHC shc = SHC.getInstance();
  public final SHP shp = SHP.getInstance();
  public final Logger logger = SHC.logger;

  @Inject
  public HomeController(FormFactory formFactory) {
    this.formFactory = formFactory;
    initialize();
  }

  private void initialize() {
    PermissionLocation local = PermissionLocation.local;
    PermissionLocation home = PermissionLocation.home;
    PermissionLocation always = PermissionLocation.always;
    User.UserType childAdult = User.UserType.Child_Adult;
    User.UserType childTeenager = User.UserType.Child_Teenager;
    User.UserType childUnderage = User.UserType.Child_Underage;
    User.UserType guest = User.UserType.Guest;
    PermitDoorOpenClose.authorize(childAdult,local);
    PermitDoorOpenClose.authorize(childTeenager,local);
    PermitDoorOpenClose.authorize(childUnderage,local);
    PermitDoorOpenClose.authorize(guest,local);
    PermitWindowOpenClose.authorize(childAdult,local);
    PermitWindowOpenClose.authorize(childTeenager,local);
    PermitWindowOpenClose.authorize(childAdult,home);

    logger.log(shs, "System initialized successfully.", Logger.MessageType.normal);
  }

  /**
   * Helper method used to parse a [[java.lang.String String]] containing a 2 digits precision decimal.
   * @param toParse the string to parse. It must contain a number, followed by a dot and then 1 or 2 digits.
   * @return the int resulting from multiplying the parsed decimal by 100.
   * @throws NumberFormatException if the [[java.lang.String String]] being parsed is not a 2 digits precision decimal.
   */
  private int parseTemperature(String toParse) throws NumberFormatException{
    String[] temperatureSections = toParse.split("[.]");
    int temperature;
    if (temperatureSections.length != 2) {
      throw new NumberFormatException();
    }
    temperature = Integer.parseInt(temperatureSections[0]) * 100;
    switch (temperatureSections[1].length()) {
      case 1:
        temperature += Integer.parseInt(temperatureSections[1]) * 10;
        break;
      case 2:
        temperature += Integer.parseInt(temperatureSections[1]);
        break;
      default:
        throw new NumberFormatException();
    }
    return temperature;
  }

  /**
   * Reads a house layout formatted text file and parses the information into a [[java.util.Map Map]] of [[models.Location Locations]]. The [[java.util.Map Map]] is then set as the [[models.modules.SHS SHS]] `home` value.
   * File content must respect the following format for success:
   * {{{
   * Locations {
   * <Location Name>,<Indoor/Outdoor/Outside>
   * ...
   * }
   * Devices {
   * <Device Class Type>,<Device Name>,<Device Location Name>,<Device Subclass Properties>
   * ...
   * }
   * }}}
   * Location names must be unique. Device names must be unique for that location. No location may have devices of the same name.
   * For each [[models.devices.Device Device]] subclass, extra properties may need to be specified. refer to the list below for each subclass format:
   *  - `[[models.devices.Light Light]],<Light Name>,<Light Location Name>`
   *  - `[[models.devices.Connection Connection]],<Connection Name>,<Connection Location Name>,<Connection secondLocation Name>`
   *  - `[[models.devices.Door Door]],<Door Name>,<Door Location Name>,<Door secondLocation Name>`
   *  - `[[models.devices.Window Window]],<Window Name>,<Window Location Name>,<Window secondLocation Name>`
   *
   * @example {{{
   *     Locations {
   *     Kitchen,Indoor
   *     LivingRoom,Indoor
   *     Bedroom,Indoor
   *     Patio,Outdoor
   *     }
   *     Devices {
   *     Light,KitchenLights,Kitchen
   *     Window,KitchenWindow,Kitchen
   *     Door,KitchenPatioDoor,Kitchen,Outside
   *     Connection,KitchenLivingRoomConnection,Kitchen,LivingRoom
   *     Light,LivingRoomLights,LivingRoom
   *     Door,EntryLivingRoomDoor,LivingRoom,Outside
   *     Door,LivingRoomBedroomDoor,LivingRoom,Bedroom
   *     Window,BedroomWindow,Bedroom
   *     Light,BedroomLights,Bedroom
   *     }
   * }}}
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successfully loading the file data into the SHS or a redirection to another method if there was an error while reading the file.
   */
  public Result loadHouseFromFile(Http.Request request, String tab){
    //Reset all Users to 'Outside', in case you're loading a new Layout.
    for(User user : shs.getUserMap().values()) {
      user.setLocation(SHS.getOutside());
    }
    //Reset invasion on the SHP, while keeping the Away Mode
    shp.setUnderInvasion(false);

    Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
    Http.MultipartFormData.FilePart<TemporaryFile> tempFile = body.getFile("layoutFile");
    TemporaryFile file = tempFile.getRef();
    if (file == null) {
      return redirect(routes.HomeController.main(tab));
    }
    File toRead = file.path().toFile();

    try(Scanner in = new Scanner(toRead)){  //safely auto-close scanner

      boolean foundMatrixDimensions = false;
      int section = -1;
      int[][] locationMatrix = new int[10][10];
      int rowIndex = 0;
      List<Location> locationList = new ArrayList<>();
      locationList.add(SHS.getOutside());
      String fileLine;
      String[] lineStringArray;
      Map<String, Location> newHouseMap = new HashMap<>();
      int doorwayCount = 1;

      while (in.hasNextLine()) {
        fileLine = in.nextLine(); //store line temporarily in string for manipulation

        switch(fileLine){ //determine if line has
          case "Locations {":
            section = 0;
            break;

          case "Devices {":
            section = 1;
            break;

          case "Map {":
            section = 2;
            break;

          case "}":
            break;

          default:
            //split file line according to ',' delimiter
            lineStringArray = fileLine.split(",");

            if(section == 0){ //create a location instance
              Location newLocation = new Location(lineStringArray[0], Location.LocationType.valueOf(lineStringArray[1]));
              (new Light(newLocation.getName() + " Light")).setLocation(newLocation);
              (new MovementDetector(" Movement Detector")).setLocation(newLocation);
              newHouseMap.put(newLocation.getName(), newLocation); //split into two steps for readability
              locationList.add(newLocation);
            } else  if (section == 1) {  //create Device instance
              Location newDeviceLocation = newHouseMap.get(lineStringArray[2]);

              switch(lineStringArray[0]){ //determine device subclass
                case "Connection":
                  Connection newConnection = new Connection(lineStringArray[1]);
                  newConnection.setLocation(newDeviceLocation);
                  newConnection.setSecondLocation(lineStringArray[3].equals("Outside")?SHS.getOutside():newHouseMap.get(lineStringArray[3]));
                  break;
                case "Door":
                  Door newDoor = new Door(lineStringArray[1]);
                  newDoor.setLocation(newDeviceLocation);
                  if (lineStringArray[3].equals("Outside")) {
                    String bufferName = "Doorway " + doorwayCount;
                    while (newHouseMap.containsKey(bufferName)) {
                      bufferName = "Doorway " + ++doorwayCount;
                    }
                    Location buffer = new Location(bufferName, Location.LocationType.Outside);
                    newHouseMap.put(bufferName, buffer);
                    locationList.add(buffer);
                    Light newLight =new Light(buffer.getName() + " Light");
                    newLight.setLocation(buffer);
                    System.out.println("newLight.getLocation().getName() = " + newLight.getLocation().getName());
                    (new MovementDetector(" Movement Detector")).setLocation(buffer);
                    newDoor.setSecondLocation(buffer);
                  } else {
                    newDoor.setSecondLocation(newHouseMap.get(lineStringArray[3]));
                  }
                  break;
                case "Window":
                  Window newWindow = new Window(lineStringArray[1]);
                  newWindow.setLocation(newDeviceLocation);
                  break;
              }
            } else if (section == 2) {
              if (!foundMatrixDimensions) {
                lineStringArray = fileLine.split("x");
                locationMatrix = new int[Integer.parseInt(lineStringArray[0])][Integer.parseInt(lineStringArray[1])];
                foundMatrixDimensions = true;
              } else {
                for (int i = 0; i < lineStringArray.length; i++) {
                  locationMatrix[rowIndex][i] = Integer.parseInt(lineStringArray[i]);
                }
                rowIndex++;
              }
            }
        }
      }
      //If function gets here, file is properly formatted and there were no issues generating locations and devices
      shs.setHome(newHouseMap);
      HomeMapper.mapHome(locationMatrix, locationList);
      //Register new layout for Away Mode
      shp.setRegistrationForAll("MovementDetector", shp.isAway());
      //Register new layout for Auto Light Mode
      shc.setRegistrationForAll("MovementDetector", shc.isAutoLights());


    } catch (FileNotFoundException e) {
      logger.log("House Layout could not be loaded. File could not be found.", Logger.MessageType.normal);
      return redirect(routes.HomeController.main(tab));
    } catch (Exception e) {
      logger.log("House Layout could not be loaded. File selected is invalid.", Logger.MessageType.danger);
      e.printStackTrace();
      return redirect(routes.HomeController.main(tab));
    }

    logger.log("House Layout loaded successfully.", Logger.MessageType.success);
    return redirect(routes.HomeController.main(tab));
  }

  /**
   * Reads a user profile formatted text file and parses the information into a [[java.util.Map Map]] of [[models.User Users]]. The [[java.util.Map Map]] is then set as the [[models.modules.SHS SHS]] `userMap` value.
   * The file also updates [[models.permissions.Permission Permissions]].
   * File content must respect the following format for success:
   * {{{
   * Users {
   * <User Name>,<Parent/Child_Adult/Child_Teenager/Child_Underage/Guest/Stranger>(,Active)
   * ...
   * }
   * Permissions {
   * <Permission Class Type>,always,(Comma Separated list of [[models.User User.UserTypes]]),home,(Comma Separated list of [[models.User User.UserTypes]]),local,(Comma Separated list of [[models.User User.UserTypes]])
   * ...
   * }
   * }}}
   * User names must be unique.
   * Only one [[models.User User]] can be set to "Active". If multiple are set, only the last one will be considered.
   *
   * @example {{{
   *     Users {
   *     Father,Parent,Active
   *     Mother,Parent
   *     Youngest Daughter,Child_Underage
   *     }
   *     Permissions {
   *     PermitWindowOpenClose,local,Parent,home,always
   *     }
   * }}}
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successfully loading the file data into the SHS or a redirection to another method if there was an error while reading the file.
   */
  public Result loadUsersFromFile(Http.Request request, String tab) {
    Http.MultipartFormData<TemporaryFile> body = request.body().asMultipartFormData();
    Http.MultipartFormData.FilePart<TemporaryFile> tempFile = body.getFile("userFile");
    TemporaryFile file = tempFile.getRef();
    if (file == null) {
      return redirect(routes.HomeController.main(tab));
    }
    File toRead = file.path().toFile();
    try(Scanner in = new Scanner(toRead)){  //safely auto-close scanner
      boolean isUser = false;
      String fileLine;
      String[] lineStringArray;
      Map<String, User> newUserMap = new HashMap<>();
      shs.setUserMap(newUserMap);

      while (in.hasNextLine()) {
        fileLine = in.nextLine(); //store line temporarily in string for manipulation

        switch(fileLine){ //determine if line has
          case "Users {":
            isUser = true;
            break;
          case "Permissions {":
            isUser = false;
            break;
          case "}":
            break;
          default:
            //split file line according to ',' delimiter
            lineStringArray = fileLine.split(",");

            if (isUser){ //create a User instance
              User newUser = new User(lineStringArray[0],User.UserType.valueOf(lineStringArray[1]));
              newUserMap.put(newUser.getName(), newUser); //split into two steps for readability
              if (lineStringArray.length>2) {
                shs.setActiveUser(newUser.getName());
              }
            } else { //update Permissions
              Permission permission;
              switch (lineStringArray[0]) {
                case "PermitDoorLockUnlock":
                  permission = PermitDoorLockUnlock.getPermission();
                  break;
                case "PermitDoorOpenClose":
                  permission = PermitDoorOpenClose.getPermission();
                  break;
                case "PermitLightOnOff":
                  permission = PermitLightOnOff.getPermission();
                  break;
                case "PermitWindowOpenClose":
                  permission = PermitWindowOpenClose.getPermission();
                  break;
                default:
                  throw new Exception();
              }

              PermissionLocation permissionLocation = PermissionLocation.never;
              for (int i = 1; i < lineStringArray.length; i++) {
                switch (lineStringArray[i]) {
                  case "never":
                    permissionLocation = PermissionLocation.never;
                    break;
                  case "local":
                    permissionLocation = PermissionLocation.local;
                    break;
                  case "home":
                    permissionLocation = PermissionLocation.home;
                    break;
                  case "always":
                    permissionLocation = PermissionLocation.always;
                    break;
                  default:
                    permission.authorize(User.UserType.valueOf(lineStringArray[i]), permissionLocation);
                }
              }
            }
        }
      }
      //If function gets here, file is properly formatted and there were no issues generating locations and devices

    } catch (FileNotFoundException e) {
      logger.log("User Profiles could not be loaded. File could not be found.", Logger.MessageType.normal);
      return redirect(routes.HomeController.main(tab));
    } catch (Exception e) {
      logger.log("User Profiles could not be loaded. File selected is invalid.", Logger.MessageType.danger);
      return redirect(routes.HomeController.main(tab));
    }
    //TODO Future Deliver: add thorough exception handling

    logger.log("User Profiles loaded successfully", Logger.MessageType.success);
    return redirect(routes.HomeController.main(tab));
  }

  /**
   * Saves the [[models.modules.SHS SHS.userMap]] and [[models.permissions Permission]] configurations to a file that the user can download.
   * The file follows the same format as [[controllers.HomeController.loadUsersFromFile loadUsersFromFile()]].
   * @return a file to be downloaded by the user.
   */
  public Result saveUsersToFile() {
    FileWriter writer = null;
    try {
      File saveFile = new File("target/userProfiles.txt");
      writer = new FileWriter(saveFile,false);
      writer.write("Users {\n");
      for (User user : shs.getUserMap().values()) {
        writer.write(user.getName() + "," + user.getType());
        if (user == shs.getActiveUser()) {
          writer.write(",Active");
        }
        writer.write("\n");
      }
      writer.write("}\nPermissions {\n");
      Permission permission;
      for (int i = 0; i < 4; i++) {
        switch (i) {
          case 0:
            permission = PermitDoorLockUnlock.getPermission();
            writer.write("PermitDoorLockUnlock");
            break;
          case 1:
            permission = PermitDoorOpenClose.getPermission();
            writer.write("PermitDoorOpenClose");
            break;
          case 2:
            permission = PermitLightOnOff.getPermission();
            writer.write("PermitLightOnOff");
            break;
          default:
            permission = PermitWindowOpenClose.getPermission();
            writer.write("PermitWindowOpenClose");
            break;
        }

        Map<User.UserType, PermissionLocation> permissionLocationMap = permission.getPermissions();
        for (User.UserType userType : User.UserType.values()) {
          writer.write("," + permissionLocationMap.get(userType) + "," + userType);
        }
        writer.write("\n");
      }
      writer.write("}");
      writer.close();
      writer = null;
      return ok(saveFile, false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ok(); //TODO
  }

  /**
   * Saves the [[models.modules.Logger console log]] to a file that the user can download.
   * The file displays displays the same contents as the console log at the time of download.
   * @return a file to be downloaded by the user.
   */
  public Result snapshotConsole() {
    //make file-friendly time string
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMMMdd_H'h'mm'm'ss's'-SSS");
    //return log with new name
    return ok(new File("target/logFile.txt"), false, Optional.of("consoleSnapshot_" + LocalDateTime.now().format(formatter) + ".txt"));
  }

  /**
   * An action that renders an HTML page with a welcome message.
   * The configuration in the <code>routes</code> file means that
   * this method will be called when the application receives a
   * <code>GET</code> request with a path of <code>/</code>.
   */
  public Result index(Http.Request request) {
    return main(request,"none");
  }
  /**
   * An action that renders an HTML page with a welcome message.
   * The configuration in the <code>routes</code> file means that
   * this method will be called when the application receives a
   * <code>GET</code> request with a path of <code>/</code>.
   */
  public Result main(Http.Request request, String tab) {
    return ok(views.html.index.render(tab, shs, formFactory.form(), request));
  }
  /**
   * Creates a [[models.User User]] instance from the information contained in the [[play.mvc.Http.Request Request]] and then adds it to the [[models.modules.SHS `userMap`]].
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful addition of a [[models.User User]] to the [[models.modules.SHS SHS]] or a redirection to another method upon failure.
   */
  public Result createUser(Http.Request request, String tab) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    String name, typeString, locationString;
    name = dynamicForm.get("name");
    typeString = dynamicForm.get("type");
    locationString = dynamicForm.get("location");
    // 2nd error check. Web page should already have stopped these errors from occurring
    if (name == null || name.trim().equals("")) {
      // to pass to the webpage: dynamicForm.withError("name","The value must not be empty");
      return badRequest(views.html.index.render(tab, shs, formFactory.form(), request));//TODO insert webpage that handles user creation
    }

    if (!User.isTypeStringValid(typeString)) {
      // to pass to the webpage: dynamicForm.withError("type","The value entered is invalid.");
      return badRequest(views.html.index.render(tab, shs, formFactory.form(), request));//TODO insert webpage that handles user creation
    }
    User toCreate = new User(name, User.UserType.valueOf(typeString));
    toCreate.setLocation(shs.getHome().get(locationString));
    if (toCreate.getType() == User.UserType.Parent) {
      if (shs.getParentAmount() >= 2 ) {
        return badRequest().flashing("error","You can only have a maximum of 2 parents per home.");//TODO insert webpage that the user will see on failure
      }
    }
    shs.getUserMap().put(name,toCreate);
    return redirect(routes.HomeController.main(tab));
  }

  /**
   * Removes an existing [[models.User User]] instance from the [[models.modules.SHS `userMap`]].
   * @param request the http header from the user.
   * @param name the [[models.User `name`]] of the [[models.User User]] to be deleted.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful removal of a [[models.User User]] from the [[models.modules.SHS SHS]] or a redirection to another method upon failure.
   */
  public Result deleteUser(Http.Request request, String tab, String name) {
    User toDelete = shs.getUserMap().get(name);
    String errorMessage = null;
    if (toDelete == null) {
      errorMessage = "The user you're trying to delete does not exist.";
    } else if (shs.getActiveUser().equals(toDelete)) {
      errorMessage = "You can not delete the active user. Please switch user accounts first.";
    }
    if (errorMessage != null) {
      return badRequest().flashing("error",errorMessage);//TODO insert webpage that the user will see on failure
    }

    shs.getUserMap().remove(name);
    return redirect(routes.HomeController.main(tab));
  }

  /**
   * Modifies the information of an existing [[models.User User]] instance and if necessary updates the [[models.modules.SHS `userMap`]].
   * @param request the http header from the user.
   * @param name the [[models.User `name`]] of the [[models.User User]] to be modified.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful modification of a [[models.User User]] from the [[models.modules.SHS SHS]] or a redirection to another method upon failure.
   */
  public Result editUser(Http.Request request, String tab, String name) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    String newName, newTypeString, locationString;
    newName = dynamicForm.get("name");
    newTypeString = dynamicForm.get("type");
    locationString = dynamicForm.get("location");
    User toEdit = shs.getUserMap().get(name);
    if (toEdit == null) {
      return badRequest().flashing("error","The user you're trying to edit does not exist.");//TODO insert webpage that the user will see on failure
    }

    if (User.isTypeStringValid(newTypeString)) {
      User.UserType newType = User.UserType.valueOf(newTypeString);
      if ((newType == User.UserType.Parent) && (toEdit.getType() != User.UserType.Parent)) {
        if (shs.getParentAmount() >= 2 ) {
          return badRequest().flashing("error","You can only have a maximum of 2 parents per home.");//TODO insert webpage that the user will see on failure
        }
      }
      toEdit.setType(newType);
    } else {
      // to pass to the webpage: dynamicForm.withError("type","The value entered is invalid.");
      return badRequest(views.html.index.render(tab, shs, formFactory.form(), request));
    }

    Location location = shs.getHome().get(locationString);
    String errorString = null;
    if (location == null) {
      return badRequest().flashing("error", "The location you have selected does not exist");//TODO insert webpage that handles user placement
    }
    toEdit.setLocation(location);


    if (name == null || name.trim().equals("")) {
      // to pass to the webpage: dynamicForm.withError("name","The value must not be empty");
      return badRequest(views.html.index.render(tab, shs, formFactory.form(), request));//TODO insert webpage that handles user edition
    } else if (!newName.equals(name)) {
      toEdit.setName(newName);
      shs.getUserMap().remove(name);
      shs.getUserMap().put(newName, toEdit);
    }
    return redirect(routes.HomeController.main(tab));//TODO insert webpage that the user will see after a successful user edition
  }

  /**
   * Makes an existing [[models.User User]] instance from the [[models.modules.SHS `userMap`]] into the [[models.modules.SHS `activeUser`]].
   * @param request the http header from the user.
   * @param name the [[models.User `name`]] of the [[models.User User]] to be set as active.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful change or a redirection to another method upon failure.
   */
  public Result setActiveUser(Http.Request request, String tab, String name) {
    User user = shs.getUserMap().get(name);
    if (user == null) {
      return badRequest().flashing("error", "The user you have selected does not exist");//TODO insert webpage that handles user placement
    }
    shs.setActiveUser(name);
    logger.log('\'' + shs.getActiveUser().getName() + "' is now the current user", Logger.MessageType.normal);
    return redirect(routes.HomeController.main(tab));
  }

  /**
   * Modifies the [[models.modules.SHS `currentTime`]] simulation attribute and [[models.Location `temperature`]] of [[models.Location Outdoor]] and [[models.Location Outside]] instances in the [[models.modules.SHS `home`]].
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful modification of all attributes or a redirection to another method upon failure.
   */
  public Result editSimulationParameters(Http.Request request, String tab) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    String newTemperatureString = dynamicForm.get("temperature");
    String dateString = dynamicForm.get("date");
    String timeString = dynamicForm.get("time");
    String timeMultiplierString = dynamicForm.get("timeMultiplier");
    int timeMultiplier;
    try {
      timeMultiplier = Integer.parseInt(timeMultiplierString);
      if (shs.getTimeMultiplier() != timeMultiplier) {
        shs.setTimeMultiplier(timeMultiplier);
        logger.log("Simulation Time Multiplier changed to " + shs.getTimeMultiplier() + '.', Logger.MessageType.success);
      }
    } catch (NumberFormatException e) {
      logger.log("Attempted to change Simulation Time Multiplier to an invalid value.", Logger.MessageType.warning);
      return badRequest(views.html.index.render(tab, shs, formFactory.form(), request));
    }
    int newTemperature;
    try {
      newTemperature = parseTemperature(newTemperatureString);
      if (SHS.getOutside().getTemperature() != newTemperature) {
        shs.setOutsideTemperature(newTemperature);
        logger.log("Outside and Outdoor temperature changed to " + SHS.getOutside().getTemperatureString() + '.', Logger.MessageType.success);
      }
    } catch (NumberFormatException e) {
      logger.log("Attempted to change Outside and Outdoor temperature to an invalid value.", Logger.MessageType.warning);
      return badRequest(views.html.index.render(tab, shs, formFactory.form(), request));
    }
    LocalDate newDate;
    LocalTime newTime;
    try {
      newDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      newTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
      LocalDateTime newCurrentTime = LocalDateTime.of(newDate, newTime);
      if (!shs.getSimulationTime().equals(newCurrentTime)) {
        shs.setSimulationTime(newCurrentTime);
        logger.log("Simulation Current Time changed to " + shs.getSimulationTime() + '.', Logger.MessageType.success);
      }
    } catch (Exception e) {
      //Do nothing. It happens when the simulation is running.
    }
    return redirect(routes.HomeController.main(tab));
  }

  private Result unauthorizedAction(String action, Device device, String tab) {
    logger.log(shs.getActiveUser(), "You don't have the permissions necessary to '" + action +"' the '" + device.toString() + '\'', Logger.MessageType.warning);
    return redirect(routes.HomeController.main(tab));
  }
  private Result failedAction(String action, Device device, String tab) {
    logger.log(shs.getActiveUser(), "It is impossible to '" + action +"' the '" + device.toString() + '\'', Logger.MessageType.danger);
    return redirect(routes.HomeController.main(tab));
  }
  private Result succeededAction(String action, Device device, String tab) {
    logger.log(shs.getActiveUser(), "You have succeeded to '" + action +"' the '" + device.toString() + '\'', Logger.MessageType.success);
    return redirect(routes.HomeController.main(tab));
  }

  /**
   * Performs the specified action on the [[models.devices.Device Device]].
   * @param request the http header from the user.
   * @param locationString the [[models.Location.name name]] of the [[models.Location Location]] where the device is.
   * @param name the [[models.devices.Device `name`]] of the [[models.devices.Device Device]] that will perform the action.
   * @param action a [[java.lang.String String]] representation of the action to be performed.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful execution of the specified action or a redirection to another method upon failure.
   */
  public Result performDeviceAction(Http.Request request, String tab, String locationString, String name, String action) {
    Location location = shs.getHome().get(locationString);
    if (location == null) {
      logger.log("Attempted to access a Location that does not exist.", Logger.MessageType.warning);
      return redirect(routes.HomeController.main(tab));
    }
    Device device = location.getDeviceMap().get(name);
    if (device == null) {
      logger.log("Attempted to access a Device that does not exist.", Logger.MessageType.warning);
      return redirect(routes.HomeController.main(tab));
    }
    User user = shs.getActiveUser();

    try {
      switch (action) {
        case Device.actionOpen:
        case Device.actionClose:
          if (device instanceof Window) {
            if (PermitWindowOpenClose.isAuthorized(user, device)) {
              if (device.doAction(action)) {
                return succeededAction(action, device, tab);
              } else {
                return failedAction(action, device, tab);
              }
            } else {
              return unauthorizedAction(action, device, tab);
            }
          }
          if (device instanceof Door) {
            if (PermitDoorOpenClose.isAuthorized(user, device)) {
              if (((Door)device).doAction(action)) {
                return succeededAction(action, device, tab);
              } else {
                return failedAction(action, device, tab);
              }
            } else {
              return unauthorizedAction(action, device, tab);
            }
          }
          // User is attempting to "open" or "close" something other than a Door or Window
          if (device.doAction(action)) {
            return succeededAction(action, device, tab);
          } else {
            return failedAction(action, device, tab);
          }
        case Device.actionOn:
        case Device.actionOff:
          if (device instanceof Light) {
            if (PermitLightOnOff.isAuthorized(user, device)) {
              if (device.doAction(action)) {
                return succeededAction(action, device, tab);
              } else {
                return failedAction(action, device, tab);
              }
            } else {
              return unauthorizedAction(action, device, tab);
            }
          }
          // User is attempting to "turn on" or "turn off" something other than a Light
          if (device.doAction(action)) {
            return succeededAction(action, device, tab);
          } else {
            return failedAction(action, device, tab);
          }
        case Door.actionLock:
        case Door.actionUnlock:
          if (device instanceof Door) {
            if (PermitDoorLockUnlock.isAuthorized(user, device)) {
              if (device.doAction(action)) {
                return succeededAction(action, device, tab);
              } else {
                return failedAction(action, device, tab);
              }
            } else {
              return unauthorizedAction(action, device, tab);
            }
          }
          // User is attempting to "lock" or "unlock" something other than a Door
          if (device.doAction(action)) {
            return succeededAction(action, device, tab);
          } else {
            return failedAction(action, device, tab);
          }
      }
      // User is attempting to do another action
      if (device.doAction(action)) {
        return succeededAction(action, device, tab);
      } else {
        return failedAction(action, device, tab);
      }
    } catch (WindowBlockedException e) {
      logger.log(shs.getActiveUser(), "It is impossible to '" + action +"' the '" + device.toString() + "'. Something is blocking the window.", Logger.MessageType.danger);
      return redirect(routes.HomeController.main(tab));
    } catch (DoorLockedException e) {
      logger.log(shs.getActiveUser(), "It is impossible to '" + action +"' the '" + device.toString() + "'. This door is locked.", Logger.MessageType.danger);
      return redirect(routes.HomeController.main(tab));
    } catch (SameStatusException e) {
      logger.log(shs.getActiveUser(), "It is impossible to '" + action +"' the '" + device.toString() + "', since it's already '" + e.getMessage() + "'.", Logger.MessageType.danger);
      return redirect(routes.HomeController.main(tab));
    }  catch (DoorOpenException e) {
      logger.log(shs.getActiveUser(), "It is impossible to '" + action +"' the '" + device.toString() + "', since it is '" + Device.actionOpen + '\'', Logger.MessageType.danger);
      return redirect(routes.HomeController.main(tab));
    } catch (DeviceException e) {
      logger.log(shs.getActiveUser(), "It is impossible to '" + action +"' the '" + device.toString() + "'.", Logger.MessageType.danger);
      return redirect(routes.HomeController.main(tab));
    }
  }

  /**
   * Helper method to give dynamic access to the overriden performDeviceAction method.
   */
  public Result deviceActionHelper(Http.Request request, String tab){
    return ok();
  }

  /**
   * Starts or stops the simulation if the pre-requisites are satisfied.
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successfully starting/stopping the simulation or a redirection to another method if the pre-requisites are not satisfied.
   */
  public Result toggleSimulationStatus(Http.Request request) {
    if (shs.getActiveUser() == null) {
      logger.log("You need to log in before starting the simulation.", Logger.MessageType.warning);
      return redirect(routes.HomeController.index());
    }
    if (shs.getHome().size() <= 1) {
      logger.log("You need to load a house layout before starting the simulation.", Logger.MessageType.warning);
      return redirect(routes.HomeController.index());
    }
    boolean isRunning = shs.isRunning();
    if (isRunning) {
      shs.stopClock();
    } else {
      shs.startClock();
    }
    shs.setRunning(!isRunning);
    return redirect(routes.HomeController.index());
  }

  /**
   * Starts or stops the [[models.modules.SHC SHC]]'s Auto Light mode.
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successfully starting/stopping the simulation or a redirection to another method if the pre-requisites are not satisfied.
   */
  public Result toggleAutoLight(Http.Request request) {
    shc.toggleAutoLights();
    logger.log("Auto light mode has been turned " + (shc.isAutoLights()?"on":"off"), Logger.MessageType.success);
    return redirect(routes.HomeController.main("SHC0"));
  }

  /**
   * Starts or stops the [[models.modules.SHP SHP]]'s Away mode.
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successfully starting/stopping the simulation or a redirection to another method if the pre-requisites are not satisfied.
   */
  public Result toggleAwayMode(Http.Request request) {
    shp.toggleAway();
    return redirect(routes.HomeController.main("SHP"));
  }

  /**
   * Starts or stops the [[models.modules.SHP SHP]]'s Away mode control over a given [[models.devices.Light Light]].
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successfully starting/stopping the simulation or a redirection to another method if the pre-requisites are not satisfied.
   */
  public Result toggleAwayModeLight(Http.Request request, String locationString, String name, boolean register) {
    Location location = shs.getHome().get(locationString);
    if (location == null) {
      logger.log("The location specified does not exist.", Logger.MessageType.warning);
      return redirect(routes.HomeController.main("SHP"));
    }
    Device device = location.getDeviceMap().get(name);
    if (!(device instanceof Light)) {
      logger.log("The device specified is not a Light.", Logger.MessageType.warning);
      return redirect(routes.HomeController.main("SHP"));
    }
    if (register) {
      shp.registerLight((Light)device);
    } else {
      shp.unregisterLight((Light)device);
    }
    return redirect(routes.HomeController.main("SHP"));
  }

  public Result editAwayMode(Http.Request request) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    String awayLightStartString = dynamicForm.get("awayLightStart");
    String awayLightEndString = dynamicForm.get("awayLightEnd");
    String timeDelayString = dynamicForm.get("timeDelay");

    try {
      LocalTime awayLightStart = LocalTime.parse(awayLightStartString, DateTimeFormatter.ofPattern("HH:mm:ss"));
      LocalTime awayLightEnd = LocalTime.parse(awayLightEndString, DateTimeFormatter.ofPattern("HH:mm:ss"));
      if (!shp.getAwayLightStart().equals(awayLightStart)) {
        shp.setAwayLightStart(awayLightStart);
        logger.log("Time to turn on Lights in Away Mode changed to " + shp.getAwayLightStartString() + ".", Logger.MessageType.success);
      }
      if (!shp.getAwayLightEnd().equals(awayLightEnd)) {
        shp.setAwayLightEnd(awayLightEnd);
        logger.log("Time to turn off Lights in Away Mode changed to " + shp.getAwayLightEndString() + ".", Logger.MessageType.success);
      }
    } catch (Exception e) {
      //Do nothing.
    }
    try {
      int timeDelay = Integer.parseInt(timeDelayString);
      if (shp.getTimeBeforeAuthorities() != timeDelay) {
        shp.setTimeBeforeAuthorities(timeDelay);
        logger.log("Time before contacting authorities in Away Mode changed to " + shp.getTimeBeforeAuthorities() + " seconds.", Logger.MessageType.success);
      }
    } catch (NumberFormatException e) {
      logger.log("Attempted to change the time before contacting authorities in Away Mode to an invalid value.", Logger.MessageType.warning);
      return badRequest(views.html.index.render("SHP", shs, formFactory.form(), request));
    }
    for (Location location : shs.getHome().values()) {
      for (Device device : location.getDeviceMap().values()) {
        if (device instanceof Light) {
          Light light = (Light)device;
          String status = dynamicForm.get(light.toString());
          if (status != null) {
            if (!shp.isLightRegistered(light)) {
              shp.registerLight(light);
              logger.log(light + " will now be controlled by the SHP while Away Mode is on.", Logger.MessageType.success);
            }
          } else {
            if (shp.isLightRegistered(light)) {
              shp.registerLight(light);
              logger.log(light + " will not be controlled anymore by the SHP while Away Mode is on.", Logger.MessageType.success);
            }
          }
        }
      }
    }
    return redirect(routes.HomeController.main("SHP"));
  }

  public Result editPermissions(Http.Request request) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    for (User.UserType userType : User.UserType.values()) {
      if ((userType != User.UserType.Parent) && (userType != User.UserType.Stranger)) {
        String permissionValue;
        String permissionName = "PermitAutoLightMode";


        permissionName = "PermitAwayMode";
        permissionValue = dynamicForm.get(permissionName + userType);
        System.out.println("permissionValue = " + permissionValue);
        if (permissionValue != null) {
          PermitAwayMode.authorize(userType, PermissionLocation.always);
        } else {
          PermitAwayMode.authorize(userType, PermissionLocation.never);
        }
        permissionName = "PermitWindowOpenClose";
        PermitWindowOpenClose.authorize(userType,PermissionLocation.valueOf(dynamicForm.get(permissionName + userType)));
        permissionName = "PermitDoorOpenClose";
        PermitDoorOpenClose.authorize(userType,PermissionLocation.valueOf(dynamicForm.get(permissionName + userType)));
        permissionName = "PermitDoorLockUnlock";
        PermitDoorLockUnlock.authorize(userType,PermissionLocation.valueOf(dynamicForm.get(permissionName + userType)));
        permissionName = "PermitLightOnOff";
        PermitLightOnOff.authorize(userType,PermissionLocation.valueOf(dynamicForm.get(permissionName + userType)));
        permissionName = "PermitAutoLightMode";
        PermitAutoLightMode.authorize(userType,PermissionLocation.valueOf(dynamicForm.get(permissionName + userType)));




      }
    }
    return redirect(routes.HomeController.main("user"));
  }

  public Result loadSideBar(Http.Request request, String name) {
    DynamicForm dynamicForm = formFactory.form();
    switch (name) {
      case "user":
        return ok(views.html.userSidebar.render(shs.getActiveUser(), shs, dynamicForm, request));
      case "house":
        return ok(views.html.houseLayout.render(formFactory.form(), request));
      case "device":
        return ok(views.html.contextSidebar.render(shs, dynamicForm, request));
      case "parameters":
        return ok(views.html.parameters.render(shs, dynamicForm, request));
      case "SHC0":
        return ok(views.html.SHCSidebar.render(0, shs, dynamicForm, request));
      case "SHC1":
        return ok(views.html.SHCSidebar.render(1, shs, dynamicForm, request));
      case "SHC2":
        return ok(views.html.SHCSidebar.render(2, shs, dynamicForm, request));
      case "SHP":
        return ok(views.html.SHPSidebar.render(shs, dynamicForm, request));
    }
    return ok();
  }
  public Result loadMetrics() {
    //return ok(views.html.constantMetrics.render(shs));
    return ok(views.html.house.render(shs));
  }
  public Result loadConsole() {
    return ok(views.html.console.render(shs));
  }
}
