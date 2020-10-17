package controllers;

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
//    locationString = dynamicForm.get("location");
    User.userType type;
    // 2nd error check. Web page should already have stopped these errors from occurring
    if (name == null || name.trim().equals("")) {
      // to pass to the wepage: dynamicForm.withError("name","The value must not be empty");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user creation
    }

    if (typeString == null || typeString.trim().equals("")) {
      // to pass to the wepage: dynamicForm.withError("type","The value must not be empty");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user creation
    }
    try {
      type = User.userType.valueOf(typeString);
    } catch (IllegalArgumentException e) {
      // to pass to the wepage: dynamicForm.withError("type","The value selected does not exist");
      return badRequest(views.html.index.render());//TODO insert webpage that handles user creation
    }

//    if (locationString == null || locationString.trim().equals("") || !shs.getHome().containsKey(locationString)) {
//      // to pass to the wepage: dynamicForm.withError("location","The value must not be empty");
//      return badRequest(views.html.index.render());//TODO insert webpage that handles user creation
//    }

    User toCreate = new User(name, type);
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


}
