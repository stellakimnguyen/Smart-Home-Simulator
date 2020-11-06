package models.exceptions;

import models.devices.Device;

public class InvalidActionException extends DeviceException {
  public InvalidActionException(Device cause) {
    super(cause.toString() + ": This Device can't perform that action", cause);
  }
  public InvalidActionException(String message, Device cause) {
    super(message, cause);
  }
}
