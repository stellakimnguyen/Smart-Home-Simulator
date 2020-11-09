package models.exceptions;

import models.devices.Device;

public class WindowBlockedException extends DeviceException {
  public WindowBlockedException(Device cause) {
    super(cause.toString() + ": This window is blocked", cause);
  }
  public WindowBlockedException(String message, Device cause) {
    super(message, cause);
  }
}
