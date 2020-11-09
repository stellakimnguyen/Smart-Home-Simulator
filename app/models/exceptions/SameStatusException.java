package models.exceptions;

import models.devices.Device;

public class SameStatusException extends DeviceException {
  public SameStatusException(Device cause) {
    super(cause.toString() + ": This device is already " + cause.getFullStatus(), cause);
  }
  public SameStatusException(String message, Device cause) {
    super(message, cause);
  }
}
