package models;

public class Connection extends Device {
  private Location secondLocation;

  public Connection(String name) {
    super(name);
    super.setStatus(Device.statusOpen);
  }

  public Location getSecondLocation() {
    return secondLocation;
  }

  public boolean setSecondLocation(Location secondLocation) {
    // null check for new location
    if (secondLocation == null) {
      return false; // TODO throw some sort of exception once error handling is in place
    }
    // check if device with name exists in new location
    if (secondLocation.getDeviceMap().containsKey(getName())) {
      return false; // TODO throw some sort of exception once error handling is in place
    }
    // check if there is a location cycle
    if (getLocation().equals(secondLocation)) {
      return false; // TODO throw some sort of exception once error handling is in place
    }
    // if device is already at a location, remove it from that map.
    if (this.secondLocation != null) {
      this.secondLocation.getDeviceMap().remove(getName());
    }
    this.secondLocation = secondLocation;
    this.secondLocation.getDeviceMap().put(getName(), this);
    return true;
  }

  @Override
  public void setStatus(String status) {
  } // do nothing

  @Override
  public boolean doAction(String action) {
    return false; // By default, no action is performed
  }
}
