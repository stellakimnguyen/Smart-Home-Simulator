package models;

/**
 * A virtual person that will interact with the Smart Home.
 * ===Inner Class===
 * `UserType (public [[enum]]):` Predefined values for the type of users:
 *  - `Parent:` A home inhabitant. Only two parents per home are allowed.
 *  - `Child_Underage, Child_Teenager, Child_Adult:` A home inhabitant. Has limited function access.
 *  - `Guest:` A person not inhabiting the house that was allowed entrance by house inhabitants.
 *  - `Stranger:` A person not inhabiting the house that entered the house by infraction.
 *
 * ===Attributes===
 * `name (private [[java.lang.String String]]):` Unique identifier for the User.
 *
 * `userType (private UserType):` The type of user.
 *
 * `location (private [[models.Location Location]]):` The location the user is currently at, defaults to [[models.SHS Outside]].
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class User {
  private String name;
  private UserType type;
  private Location location;

  public enum UserType {
    Parent,
    Child_Underage,
    Child_Teenager,
    Child_Adult,
    Guest,
    Stranger
  }

  /**
   * Helper method to verify that a [[java.lang.String String]] value exists within UserType.
   * @param typeString the value to verify.
   * @return true if a UserType with that name exists, false otherwise.
   */
  public static boolean isTypeStringValid(String typeString){
    if (typeString == null || typeString.trim().equals("")) {
      return false;
    }
    try {
      UserType.valueOf(typeString);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  public User(String name, UserType type) {
    this.name = name;
    this.type = type;
    this.location = SHS.getOutside();
  }

  /**
   * Get the User name.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the User name, with no whitespaces.
   */
  public String getId() {
    return name.replaceAll(" ","");
  }


  /**
   * Set the User name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the User type.
   */
  public UserType getType() {
    return type;
  }

  /**
   * Set the User type.
   */
  public void setType(UserType type) {
    this.type = type;
  }

  /**
   * Get the User location.
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Set the User location.
   */
  public void setLocation(Location location) {
    if (location == null) {
      location = SHS.getOutside();
    }
    this.location = location;
  }
}
