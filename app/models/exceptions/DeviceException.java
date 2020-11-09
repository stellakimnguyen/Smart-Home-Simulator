package models.exceptions;

import models.devices.Device;

public class DeviceException extends Exception {
  private final Device cause;

  public DeviceException(String message, Device cause) {
    super(message);
    this.cause = cause;
  }

  public DeviceException(Device cause) {
    super("An error has occurred on the Device " + cause.toString());
    this.cause = cause;
  }
}
