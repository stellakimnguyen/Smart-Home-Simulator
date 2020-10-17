package models;

public abstract class Device {
  private String name;
  private String status;
  private Location location;

  // Common strings
  public static final String actionOn = "turn on";
  public static final String actionOff = "turn off";
  public static final String statusOn = "on";
  public static final String statusOff = "off";
  public static final String actionOpen = "open";
  public static final String actionClose = "close";
  public static final String statusOpen = "open";
  public static final String statusClosed = "closed";

  public Device(String name) {
    this.name = name;
    this.location = null;
    this.status = "";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Location getLocation() {
    return location;
  }

  public boolean setLocation(Location location) {
    // null check for new location
    if (location == null) {
      return false; // TODO throw some sort of exception once error handling is in place
    }
    // devices are not allowed on "Outside" locations
    if (location.getLocationType() == Location.LocationType.Outside) {
      return false; // TODO throw some sort of exception once error handling is in place
    }
    // check if device with name exists in new location
    if (location.getDeviceMap().containsKey(this.name)) {
      return false; // TODO throw some sort of exception once error handling is in place
    }
    // if device is already at a location, remove it from that map.
    if (this.location != null) {
      this.location.getDeviceMap().remove(this.name);
    }
    this.location = location;
    this.location.getDeviceMap().put(this.name, this);
    return true;
  }

  /**
   * Default action system.
   *
   * @param action String with the action code to be performed
   * @return true if the action was performed, false otherwise.
   */
  public abstract boolean doAction(String action);
}
