package models.permissions;

import models.User;
import models.devices.Device;
import models.devices.Light;

public abstract class PermitAwayMode {
  private static final Permission permission = new Permission();

  public static void authorize(User.UserType userType, PermissionLocation permissionLocation) {
    permission.authorize(userType, permissionLocation);
  }

  public static boolean isAuthorized(User user, Device device) {
    if (device instanceof Light) {
      return permission.isAuthorized(user, device.getLocation());
    }
    return false;
  }

  public static Permission getPermission() {
    return permission;
  }
}
