package models.devices;

import models.Location;
import models.devices.Device;
import models.exceptions.DeviceException;

/**
 * Extends a [[models.devices.Device Device]]: joins two [[models.Location Locations]] together, without any barriers.
 * ===Attributes===
 * `secondLocation (private [[models.Location Location]]):` Location to be joined to the one this [[models.devices.Device Device]] is located at.
 *
 * @version 3
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public class Connection extends Device {
  private Location secondLocation;

  public Connection(String name) {
    super(name);
    permitStatus(statusOpen);
    setStatus(statusOpen);
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
    if (secondLocation.equals(getLocation())) {
      return false; // TODO throw some sort of exception once error handling is in place
    }
    // if device is already at a location, remove it from that map.
    if (this.secondLocation != null) {
      this.secondLocation.removeDevice(this);
    }
    this.secondLocation = secondLocation;
    this.secondLocation.addDevice(this);
    setDisplayName();
    return true;
  }

  /**
   * Overrides the default method, so as to do nothing instead.
   * @param action ignored
   * @return false
   */
  @Override
  public boolean doAction(String action) throws DeviceException {
    return false;
  }

  @Override
  public String getLocationString() {
    return super.getLocationString() + ", " + (secondLocation!=null? secondLocation.getName() : "");
  }
}
