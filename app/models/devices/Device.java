package models.devices;

import models.Location;
import models.exceptions.DeviceException;

/**
 * Template for all devices existing in a [[models.Location Location]]. Contains common attributes and a basic set of actions and statuses.
 * ===Attributes===
 * `name (private [[java.lang.String String]]):` Identifier for the device. Unique within the [[models.Location Location]] that contains it.
 *
 * `status (private [[java.lang.String String]]):` Current condition of the device.
 *
 * `location (private [[models.Location Location]]):` [[models.Location Location]] this Device is in.
 *
 * ===Common Actions `(public)`===
 * `actionOn, actionOff, actionOpen, actionClose`
 *
 * ===Common Statuses `(public)`===
 * `statusOn, statusOff, statusOpen, statusClose`
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
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

  /**
   * Get the Device name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the Device name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the Device status.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Get the Device's full status.
   */
  public String getFullStatus() {
    return status;
  }

  /**
   * Set the Device status.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Get the [[models.Location Location]] the Device is in.
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Set the [[models.Location Location]] the Device is in.
   * @return true if the [[models.Location Location]] was changed successfully, false otherwise.
   */
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
      this.location.removeDevice(this);
    }
    this.location = location;
    this.location.addDevice(this);
    return true;
  }

  /**
   * Default action system.
   *
   * @param action String with the action code to be performed.
   * @return true if the action was performed, false otherwise.
   */
  public abstract boolean doAction(String action) throws DeviceException;

  @Override
  public String toString() {
    return "[" + getLocation().getName() + "] " + getName();
  }
}
