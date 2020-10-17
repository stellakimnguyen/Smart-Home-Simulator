package models;

public class User {
  private String name;
  private userType type;
  private Location location;

  public enum userType {
    Parent,
    Child_Underage,
    Child_Teenager,
    Child_Adult,
    Guest,
    Stranger
  }

  public static boolean isTypeStringValid(String typeString){
    if (typeString == null || typeString.trim().equals("")) {
      return false;
    }
    try {
      userType.valueOf(typeString);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  public User(String name, userType type) {
    this.name = name;
    this.type = type;
    this.location = SHS.getOutside();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public userType getType() {
    return type;
  }

  public void setType(userType type) {
    this.type = type;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    if (location == null) {
      location = SHS.getOutside();
    }
    this.location = location;
  }
}
