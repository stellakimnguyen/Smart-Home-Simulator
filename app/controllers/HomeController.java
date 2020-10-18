package controllers;

import models.Device;
import models.Location;
import models.SHS;
import models.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains all actions that handle HTTP requests from the Users and the instance of [[models.SHS SHS]].
 * ===Attributes===
 * `formFactory (private final FormFactory):` Helper to create HTML forms.
 *
 * `shs (private final [[models.SHS SHS]]):` Singleton instance of SHS.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class HomeController extends Controller {
  private final FormFactory formFactory;
  public final SHS shs = SHS.getInstance();

  @Inject
  public HomeController(FormFactory formFactory) {
    this.formFactory = formFactory;
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
   * An action that renders an HTML page with a welcome message.
   * The configuration in the <code>routes</code> file means that
   * this method will be called when the application receives a
   * <code>GET</code> request with a path of <code>/</code>.
   */
  public Result index() {
    return ok(views.html.index.render());
  }

  /**
   * Creates a [[models.User User]] instance from the information contained in the [[play.mvc.Http.Request Request]] and then adds it to the [[models.SHS `userMap`]].
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful addition of a [[models.User User]] to the [[models.SHS SHS]] or a redirection to another method upon failure.
   */
  public Result createUser(Http.Request request) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    String name, typeString, locationString;
    name = dynamicForm.get("name");
    typeString = dynamicForm.get("type");
    // 2nd error check. Web page should already have stopped these errors from occurring
    if (name == null || name.trim().equals("")) {
      // to pass to the webpage: dynamicForm.withError("name","The value must not be empty");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user creation
    }

    if (!User.isTypeStringValid(typeString)) {
      // to pass to the webpage: dynamicForm.withError("type","The value entered is invalid.");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user creation
    }
    User toCreate = new User(name, User.UserType.valueOf(typeString));
    if (toCreate.getType() == User.UserType.Parent) {
      if (shs.getParentAmount() >= 2 ) {
        return badRequest().flashing("error","You can only have a maximum of 2 parents per home.");//TODO insert webpage that the user will see on failure
      }
    }
    shs.getUserMap().put(name,toCreate);
    return ok();//TODO insert webpage that the user will see after a successful user creation
  }
  /**
   * Removes an existing [[models.User User]] instance from the [[models.SHS `userMap`]].
   * @param request the http header from the user.
   * @param name the [[models.User `name`]] of the [[models.User User]] to be deleted.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful removal of a [[models.User User]] from the [[models.SHS SHS]] or a redirection to another method upon failure.
   */
  public Result deleteUser(Http.Request request, String name) {
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
    return ok();//TODO insert webpage that the user will see after a successful user deletion
  }

  /**
   * Modifies the information of an existing [[models.User User]] instance and if necessary updates the [[models.SHS `userMap`]].
   * @param request the http header from the user.
   * @param name the [[models.User `name`]] of the [[models.User User]] to be modified.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful modification of a [[models.User User]] from the [[models.SHS SHS]] or a redirection to another method upon failure.
   */
  public Result editUser(Http.Request request, String name) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    String newName, newTypeString;
    newName = dynamicForm.get("name");
    newTypeString = dynamicForm.get("type");
    User toEdit = shs.getUserMap().get(name);
    if (toEdit == null) {
      return badRequest().flashing("error","The user you're trying to edit does not exist.");//TODO insert webpage that the user will see on failure
    }

    if (User.isTypeStringValid(newTypeString)) {
      User.UserType newType = User.UserType.valueOf(newTypeString);
      if (newType == User.UserType.Parent) {
        if (shs.getParentAmount() >= 2 ) {
          return badRequest().flashing("error","You can only have a maximum of 2 parents per home.");//TODO insert webpage that the user will see on failure
        }
      }
      toEdit.setType(newType);
    } else {
      // to pass to the webpage: dynamicForm.withError("type","The value entered is invalid.");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user edition
    }

    if (name == null || name.trim().equals("")) {
      // to pass to the webpage: dynamicForm.withError("name","The value must not be empty");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user edition
    } else if (!newName.equals(name)) {
      toEdit.setName(newName);
      shs.getUserMap().remove(name);
      shs.getUserMap().put(newName, toEdit);
    }
    return ok();//TODO insert webpage that the user will see after a successful user edition
  }

  /**
   * Modifies the [[models.User `location`]] property of a [[models.User User]] instance within the [[models.SHS `userMap`]].
   * @param request the http header from the user.
   * @param name the [[models.User `name`]] of the [[models.User User]] to be modified.
   * @param locationString the [[models.Location `name`]] of the new [[models.Location Location]].
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful modification of a [[models.User User]] from the [[models.SHS SHS]] or a redirection to another method upon failure.
   */
  public Result placeUser(Http.Request request, String name, String locationString) {
    User toPlace = shs.getUserMap().get(name);
    Location location = shs.getHome().get(locationString);
    String errorString = null;
    if (toPlace == null) {
      errorString = "The user you have selected does not exist";
    } else if (location == null) {
      errorString = "The location you have selected does not exist";
    }
    if (errorString != null) {
      return badRequest().flashing("error", errorString);//TODO insert webpage that handles user placement
    }

    toPlace.setLocation(location);
    return ok();//TODO insert webpage that the user will see after a successful user placement
  }

  /**
   * Modifies the [[models.SHS `currentTime`]] simulation attribute and [[models.Location `temperature`]] of [[models.Location Outdoor]] and [[models.Location Outside]] instances in the [[models.SHS `home`]].
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful modification of all attributes or a redirection to another method upon failure.
   */
  public Result editSimulationParameters(Http.Request request) {
    DynamicForm dynamicForm = formFactory.form().bindFromRequest(request);
    String newTemperatureString = dynamicForm.get("temperature");
    String dateString = dynamicForm.get("date");
    String timeString = dynamicForm.get("time");
    int newTemperature;
    try {
      newTemperature = parseTemperature(newTemperatureString);
    } catch (NumberFormatException e) {
      // to pass to the webpage: dynamicForm.withError("temperature","The value entered is invalid");
      return badRequest(views.html.index.render());//TODO insert webpage that handles simulation parameter edition
    }
    LocalDate newDate;
    try {
      newDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    } catch (Exception e) {
      // to pass to the webpage: dynamicForm.withError("date","The value entered is invalid");
      return badRequest(views.html.index.render());//TODO insert webpage that handles simulation parameter edition
    }
    LocalTime newTime;
    try {
      newTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
    } catch (Exception e) {
      // to pass to the webpage: dynamicForm.withError("time","The value entered is invalid");
      return badRequest(views.html.index.render());//TODO insert webpage that handles simulation parameter edition
    }
    LocalDateTime newCurrentTime = LocalDateTime.of(newDate, newTime);


    shs.setOutsideTemperature(newTemperature);
    shs.setCurrentTime(newCurrentTime);
    return ok();//TODO insert webpage that the user will see after a successful simulation parameter edition
  }

  /**
   * Performs the specified action on the [[models.Device Device]].
   * @param request the http header from the user.
   * @param locationString the [[models.Location.name name]] of the [[models.Location Location]] where the device is.
   * @param name the [[models.Device `name`]] of the [[models.Device Device]] that will perform the action.
   * @param action a [[java.lang.String String]] representation of the action to be performed.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successful execution of the specified action or a redirection to another method upon failure.
   */
  public Result performDeviceAction(Http.Request request, String locationString, String name, String action) {
    Location location = shs.getHome().get(locationString);
    if (location == null) {
      return badRequest().flashing("error","The location for that device does not exist");//TODO insert webpage that the user will see on failure
    }
    Device device = location.getDeviceMap().get(name);
    if (device == null) {
      return badRequest().flashing("error","That device does not exist in the specified location");//TODO insert webpage that the user will see on failure
    }
    boolean result = device.doAction(action);
    if (result) {
      return ok();//TODO insert webpage that the user will see after the action was performed successfully
    } else {
      return badRequest().flashing("error","That action could not be performed");//TODO insert webpage that the user will see on failure
    }

  }

  /**
   * Starts or stops the simulation if the pre-requisites are satisfied.
   * @param request the http header from the user.
   * @return a [[play.mvc.Result Result]]. It contains the webpage the user will see upon successfully starting/stopping the simulation or a redirection to another method if the pre-requisites are not satisfied.
   */
  public Result toggleSimulationStatus(Http.Request request) {
    if (shs.getActiveUser() == null) {
      return badRequest().flashing("error","You need to log in to perform this action");//TODO insert webpage that the user will see on failure
    }
    if (shs.getHome().size() <= 1) {
      return badRequest().flashing("error","You need to load a house layout to perform this action");//TODO insert webpage that the user will see on failure
    }
    shs.setRunning(!shs.isRunning());
    return ok();//TODO insert webpage that the user will see after the action was performed successfully
  }
}
