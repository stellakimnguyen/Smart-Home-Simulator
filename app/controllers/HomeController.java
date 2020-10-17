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
import java.time.format.DateTimeParseException;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
  private final FormFactory formFactory;
  private final SHS shs = SHS.getInstance();

  @Inject
  public HomeController(FormFactory formFactory) {
    this.formFactory = formFactory;
  }

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

    User toCreate = new User(name, User.userType.valueOf(typeString));
    shs.getUserMap().put(name,toCreate);
    return ok();//TODO insert webpage that the user will see after a successful user creation
  }

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
      toEdit.setType(User.userType.valueOf(newTypeString));
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
      newTime = LocalTime.parse(dateString, DateTimeFormatter.ofPattern("HH:mm:ss"));
    } catch (Exception e) {
      // to pass to the webpage: dynamicForm.withError("time","The value entered is invalid");
      return badRequest(views.html.index.render());//TODO insert webpage that handles simulation parameter edition
    }
    LocalDateTime newCurrentTime = LocalDateTime.of(newDate, newTime);


    shs.setOutsideTemperature(newTemperature);
    shs.setCurrentTime(newCurrentTime);
    return ok();//TODO insert webpage that the user will see after a successful simulation parameter edition
  }

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


}
