package models.permissions;

import models.devices.Device;
import models.devices.Door;
import models.Location;
import models.User;

public abstract class PermitDoorLockUnlock {
  private static final Permission permission = new Permission();

  public static void authorize(User.UserType userType, PermissionLocation permissionLocation) {
    permission.authorize(userType, permissionLocation);
  }

  public static boolean isAuthorized(User user, Device device) {
    if (device instanceof Door) {
      Door door = (Door) device;
      /*
       * 4 combinations of valid "lockable" doors:
       * indoor-outside, indoor-outdoor, outdoor-indoor, outdoor-outside
       */
      switch (door.getSecondLocation().getLocationType()) {
        case Indoor: //outdoor-indoor
          if (door.getLocation().getLocationType() != Location.LocationType.Outdoor) {
            return false; // 1st location is not outdoor, Door can't be locked
          }
        case Outside: //indoor-outside, outdoor-outside
        case Outdoor: //indoor-outdoor
          return permission.isAuthorized(user, device.getLocation()) || permission.isAuthorized(user, ((Door) device).getSecondLocation());
      }
    }
    return false;
  }

  public static Permission getPermission() {
    return permission;
  }
}
