package models.permissions;

import models.devices.Device;
import models.devices.Door;
import models.User;

public abstract class PermitDoorLockUnlock {
  private static final Permission permission = new Permission();

  public static void authorize(User.UserType userType, PermissionLocation permissionLocation) {
    permission.authorize(userType, permissionLocation);
  }

  public static boolean isAuthorized(User user, Device device) {
    boolean hasPermission;
    if (device instanceof Door) {
      Door door = (Door) device;
      hasPermission = permission.isAuthorized(user, door.getLocation()) || permission.isAuthorized(user, door.getSecondLocation());
      return hasPermission && (user.getLocation().equals(door.getLocation()) || user.getLocation().equals(door.getSecondLocation()) || door.canBeLockedRemotely());
    }
    return false;
  }

  public static Permission getPermission() {
    return permission;
  }
}
