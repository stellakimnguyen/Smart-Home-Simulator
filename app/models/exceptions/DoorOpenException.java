package models.exceptions;

import models.devices.Device;

public class DoorOpenException extends DeviceException {
  public DoorOpenException(Device cause) {
    super(cause.toString() + ": This door is open.", cause);
  }
  public DoorOpenException(String message, Device cause) {
    super(message, cause);
  }
}
