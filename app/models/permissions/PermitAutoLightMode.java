package models.permissions;

import models.Location;
import models.User;
import models.devices.Device;
import models.devices.Light;
import models.modules.SHS;

public abstract class PermitAutoLightMode {
  private static final Permission permission = new Permission();

  public static void authorize(User.UserType userType, PermissionLocation permissionLocation) {
    permission.authorize(userType, permissionLocation);
  }

  public static boolean isAuthorized(User user) {
    //Get a random Inside Location
    for(Location location : SHS.getInstance().getHome().values()) {
      if (!location.getLocationType().equals(Location.LocationType.Outside)) {
        return permission.isAuthorized(user, location);
      }
    }
    return permission.isAuthorized(user, user.getLocation());
  }

  public static Permission getPermission() {
    return permission;
  }
}
