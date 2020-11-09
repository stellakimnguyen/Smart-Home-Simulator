package models.permissions;

import models.Location;
import models.User;
import models.User.UserType;
import models.modules.SHS;

import java.util.HashMap;
import java.util.Map;

public class Permission {
  private final Map<UserType,PermissionLocation> permissions = new HashMap<>();

  public Permission() {
    authorize(UserType.Stranger, PermissionLocation.never);
    authorize(UserType.Child_Adult, PermissionLocation.never);
    authorize(UserType.Child_Teenager, PermissionLocation.never);
    authorize(UserType.Child_Underage, PermissionLocation.never);
    authorize(UserType.Guest, PermissionLocation.never);
    authorize(UserType.Parent, PermissionLocation.always);
  }

  public void authorize(UserType userType, PermissionLocation permissionLocation) {
    if (permissionLocation == null) {
      permissionLocation = PermissionLocation.never;
    }
    permissions.put(userType, permissionLocation);
  }

  public Map<UserType, PermissionLocation> getPermissions() {
    return new HashMap<>(permissions);
  }

  public boolean isAuthorized(User user, Location location) {
    Location userLocation = user.getLocation();
    int weight = permissions.get(user.getType()).weight;
    return ((userLocation == location) && (weight > 0) // User is in the same Location as the device
            || ((userLocation == SHS.getOutside()) && (weight > 2)) // User is outside of Home
            || ((userLocation != SHS.getOutside()) && (weight > 1))); //User is in Home but not the same Location as the device
  }
}
