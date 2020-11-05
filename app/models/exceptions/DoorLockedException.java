package models.exceptions;

import models.devices.Device;

public class DoorLockedException extends DeviceException {
  public DoorLockedException(Device cause) {
    super(cause.toString() + ": This door is locked", cause);
  }
  public DoorLockedException(String message, Device cause) {
    super(message, cause);
  }
}
