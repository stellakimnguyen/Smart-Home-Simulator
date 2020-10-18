package models;

/**
 * Extends a [[models.Device Device]]: joins two [[models.Location Locations]] together, without any barriers.
 * ===Attributes===
 * `secondLocation (private [[models.Location Location]]):` Location to be joined to the one this [[models.Device Device]] is located at.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class Connection extends Device {
  private Location secondLocation;

  public Connection(String name) {
    super(name);
    super.setStatus(Device.statusOpen);
  }

  /**
   * Get the [[models.Location location]] to be joined.
   */
  public Location getSecondLocation() {
    return secondLocation;
  }
  /**
   * Set the [[models.Location location]] to be joined.
   */
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

  /**
   * Overrides the default method, so as to do nothing instead.
   * @param status ignored
   */
  @Override
  public void setStatus(String status) {
  } // do nothing

  /**
   * Overrides the default method, so as to do nothing instead.
   * @param action ignored
   * @return false
   */
  @Override
  public boolean doAction(String action) {
    return false; // By default, no action is performed
  }
}
