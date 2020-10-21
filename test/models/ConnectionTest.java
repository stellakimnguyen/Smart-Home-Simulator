package models;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConnectionTest extends WithApplication  {

  @Override
  protected Application provideApplication() {
    return new GuiceApplicationBuilder().build();
  }
}
