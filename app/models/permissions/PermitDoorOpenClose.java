package models.permissions;

import models.devices.Device;
import models.devices.Door;
import models.User;

public abstract class PermitDoorOpenClose {
  private static final Permission permission = new Permission();

  public static void authorize(User.UserType userType, PermissionLocation permissionLocation) {
    permission.authorize(userType, permissionLocation);
  }

  public static boolean isAuthorized(User user, Device device) {
    if (device instanceof Door) {
      return permission.isAuthorized(user, device.getLocation()) || permission.isAuthorized(user, ((Door) device).getSecondLocation());
    }
    return false;
  }

  public static Permission getPermission() {
    return permission;
  }
}
