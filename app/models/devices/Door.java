package models.devices;

import models.Location;
import models.exceptions.*;

/**
 * Extends a [[models.devices.Connection Connection]]: introduces a barrier between both [[models.Location Locations]], new statuses, and new actions.
 * ===Attributes===
 * `isLocked (private boolean):` the condition of the lock.
 *
 * ===Class Actions `(public)`===
 * `actionLock, actionUnlock`
 *
 * ===Class Statuses `(public)`===
 * `statusLocked, statusNotLocked`
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class Door extends Connection {
  private boolean isLocked;

  public static final String actionLock = "lock";
  public static final String actionUnlock = "unlock";
  public static final String statusLocked = "locked";
  public static final String statusNotLocked = "not locked";

  public Door(String name) {
    super(name);
    this.isLocked = false;
    super.setStatus(Device.statusClosed);
  }

  /**
   * Get the condition of the lock.
   */
  public boolean isLocked() {
    return isLocked;
  }

  /**
   * Set the condition of the lock.
   */
  public void setLocked(boolean locked) {
    isLocked = locked;
  }

  /**
   * Check if the door can be locked remotely. Only Doors that go to [[models.Location Outside]] [[models.Location Locations]]
   * or that join [[models.Location Indoor]] and [[models.Location Outdoor]] [[models.Location Locations]] can be remotely locked.
   * @return true if door can be locked remotely, false otherwise.
   */
  public boolean canBeLockedRemotely() {
    switch (getSecondLocation().getLocationType()) {
      case Indoor: //outdoor-indoor
       return (getLocation().getLocationType() == Location.LocationType.Outdoor); // 1st location is not outdoor, Door can't be locked
      case Outside: //indoor-outside, outdoor-outside
        return true;
      case Outdoor: //indoor-outdoor
        return (getLocation().getLocationType() == Location.LocationType.Indoor); // 1st location is not indoor, Door can't be locked
      default:
        return false;
    }
  }

  /**
   * Get the Device status.
   * @return a [[java.lang.String String]] that compounds the device status, a comma, and the condition of the lock.
   */
  @Override
  public String getFullStatus() {
    return super.getStatus() + "," + (isLocked?statusLocked:statusNotLocked);
  }

  /**
   * Set the Device status. Only accepts `statusOpen, statusClose`
   */
  @Override
  void setStatus(String status) {
    if (status.equals(Device.statusOpen) || status.equals(Device.statusClosed)) {
      super.setStatus(status);
    }
  }

  /**
   * Default action system. Only accepts `actionOpen, actionClose, actionLock, actionUnlock`
   *
   * @param action String with the action code to be performed.
   * @return true if the action was performed, false otherwise.
   */
  @Override
  public boolean doAction(String action) throws DoorLockedException, SameStatusException, DoorOpenException, InvalidActionException {
    switch (action) {
      case Device.actionOpen:
        if (getStatus().equals(Device.statusOpen)) {
          throw new SameStatusException(Device.statusOpen, this);
        } else if (isLocked) {
          throw new DoorLockedException(this);
        }
        super.setStatus(Device.statusOpen);
        return true;
      case Device.actionClose:
        if (getStatus().equals(Device.statusClosed)) {
          throw new SameStatusException(Device.statusClosed, this);
        }
        super.setStatus(Device.statusClosed);
        return true;
      case Door.actionLock:
        if (isLocked) {
          throw new SameStatusException(Door.statusLocked, this);
        } else if (getStatus().equals(Device.statusOpen)) {
          throw new DoorOpenException(this);
        }
        setLocked(true);
        return true;
      case Door.actionUnlock:
        if (!isLocked) {
          throw new SameStatusException(Door.statusNotLocked, this);
        }
        setLocked(false);
        return true;
      default:
        throw new InvalidActionException(this);
    }
  }
}
