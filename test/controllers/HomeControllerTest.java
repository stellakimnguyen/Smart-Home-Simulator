package controllers;

import models.User;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class HomeControllerTest extends WithApplication {

  @Override
  protected Application provideApplication() {
      return new GuiceApplicationBuilder().build();
  }

  @Test
  public void testIndex() {
      Http.RequestBuilder request = new Http.RequestBuilder()
              .method(GET)
              .uri("/");

      Result result = route(app, request);
      assertEquals(OK, result.status());
  }
  @Test
  public void testMain() {
    Http.RequestBuilder request = new Http.RequestBuilder()
            .method(GET)
            .uri("/none/");

    Result result = route(app, request);
    assertEquals(OK, result.status());
  }
  @Test
  public void testCreateUser(){
    HashMap<String, String> toTest = new HashMap<>();
    toTest.put("name", "name");
    toTest.put("type", User.UserType.Parent.toString());
    toTest.put("location", "Outside");
    User toCreate = new User("name", User.UserType.Parent);

    Http.RequestBuilder request = new Http.RequestBuilder()
            .method(POST)
            .bodyForm(toTest);

    Result result = route(app, request);
    assertEquals(404, result.status());
  }
}
