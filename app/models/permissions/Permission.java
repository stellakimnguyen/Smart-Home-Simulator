package models.permissions;

import models.Location;
import models.User;
import models.User.UserType;

import java.util.HashSet;
import java.util.Set;

public class Permission {
  private Set<UserType> local = new HashSet<>();
  private Set<UserType> home = new HashSet<>();
  private Set<UserType> always = new HashSet<>();

  public Permission() {
    authorize(UserType.Parent, PermissionLocation.always);
  }

  public Set<UserType> getLocal() {
    return new HashSet<>(local);
  }

  public void setLocal(Set<UserType> local) {
    this.local = local;
  }

  public Set<UserType> getHome() {
    return new HashSet<>(home);
  }

  public void setHome(Set<UserType> home) {
    this.home = home;
  }

  public Set<UserType> getAlways() {
    return new HashSet<>(always);
  }

  public void setAlways(Set<UserType> always) {
    this.always = always;
  }

  public void authorize(UserType userType, PermissionLocation permissionLocation) {
    switch (permissionLocation) {
      case never: local.remove(userType);
      case local: home.remove(userType);
      case home: always.remove(userType);
    }
    switch (permissionLocation) {
      case always: always.add(userType);
      case home: home.add(userType);
      case local: local.add(userType);
    }
  }

  public boolean isAuthorized(User user, Location location) {
    Location userLocation = user.getLocation();
    UserType userType = user.getType();
    return ((userLocation == location) && (local.contains(userType)) // User is in the same Location as the device
            || (userLocation.getLocationType() == Location.LocationType.Outside) && (always.contains(userType)) // User is outside of Home
            || (home.contains(userType))); //User is in Home but not the same Location as the device
  }
}
