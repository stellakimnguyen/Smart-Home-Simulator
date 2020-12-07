package models.devices;

import models.Location;
import models.exceptions.DeviceException;

import java.util.HashSet;
import java.util.Set;

/**
 * Template for all devices existing in a [[models.Location Location]]. Contains common attributes and a basic set of actions and statuses.
 * ===Attributes===
 * `name (private [[java.lang.String String]]):` Identifier for the device. Unique within the [[models.Location Location]] that contains it.
 *
 * `displayName (private [[java.lang.String String]]):` [[java.lang.String String]] representation of the device. Unique, as it includes the [[models.Location Location]] that contains it.
 *
 * `status (private [[java.lang.String String]]):` Current condition of the device.
 *
 * `location (private [[models.Location Location]]):` [[models.Location Location]] this Device is in.
 *
 * `permittedStatus (private [[java.util.Set Set]]&#91;[[java.lang.String String]]&#93;):` The [[java.util.Set Set]] of statuses the device accepts.
 *
 * ===Common Actions `(public)`===
 * `actionOn, actionOff, actionOpen, actionClose`
 *
 * ===Common Statuses `(public)`===
 * `statusOn, statusOff, statusOpen, statusClose`
 *
 * @version 3
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public abstract class Device {
  private String name;
  private String displayName;
  private String status;
  private Location location;
  private final Set<String> permittedStatus;

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
    this.permittedStatus = new HashSet<>();
    setDisplayName();
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
   * Set the Device status, if the new status is in `permittedStatus`.
   */
  public void setStatus(String status) {
    if (permittedStatus.contains(status)) {
      this.status = status;
    }
  }

  /**
   * Add a status to the [[java.util.Set Set]] of statuses this device accepts.
   * Statuses added can not be withdrawn
   */
  void permitStatus(String status) {
    permittedStatus.add(status);
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
    if (!checkLocationTypeAllowed(location)) {
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
    setDisplayName();
    return true;
  }

  /**
   * Default action system.
   *
   * @param action String with the action code to be performed.
   * @return true if the action was performed, false otherwise.
   */
  public abstract boolean doAction(String action) throws DeviceException;

  /**
   * setLocation Template Step, it verifies if the [[models.devices.Device Device]] is allowed at a given [[models.Location LocationType]].
   * By default, devices are not allowed on "Outside" locations.
   * @return true if the device is allowed to be in that [[models.Location LocationType]], false otherwise.
   */
  public boolean checkLocationTypeAllowed(Location location) {
    // By default, devices are not allowed on "Outside" locations
    return location.getLocationType() != Location.LocationType.Outside;
  }

  /**
   * toString Template Step, it returns the [[java.lang.String String]] representation of the [[models.Location Location]] linked to this device.
   */
  public String getLocationString() {
    return (location!=null? location.getName() : "");
  }

  /**
   * Get the Unique display name of the Device
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Helper method that refreshes the `displayName` attribute
   */
  public void setDisplayName() {
    displayName = "[" + getLocationString() + "] " + getName();
  }

  @Override
  public String toString() {
    return displayName;
  }
}
