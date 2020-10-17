package controllers;

import models.Location;
import models.SHS;
import models.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;

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
      // to pass to the wepage: dynamicForm.withError("name","The value must not be empty");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user creation
    }

    if (!User.isTypeStringValid(typeString)) {
      // to pass to the wepage: dynamicForm.withError("type","The value entered is invalid.");
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
      // to pass to the wepage: dynamicForm.withError("type","The value entered is invalid.");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user edition
    }

    if (name == null || name.trim().equals("")) {
      // to pass to the wepage: dynamicForm.withError("name","The value must not be empty");
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
}
