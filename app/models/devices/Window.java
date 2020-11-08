package models.devices;

import models.Location;
import models.exceptions.InvalidActionException;
import models.exceptions.SameStatusException;
import models.modules.SHS;
import models.exceptions.WindowBlockedException;

/**
 * Extends a [[models.devices.Connection Connection]]: fixes the [[models.Location Location]] it connects to to [[models.modules.SHS `Outside`]] and introduces a barrier between both [[models.Location Locations]], new statuses, and new actions.
 * ===Attributes===
 * `isBlocked (private boolean):` the condition of the barrier.
 *
 * ===Class Actions `(public)`===
 * `actionBlock, actionUnblock`
 *
 * ===Class Statuses `(public)`===
 * `statusBlocked, statusNotBlocked`
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class Window extends Connection {
  private boolean isBlocked;

  public static final String actionBlock = "block";
  public static final String actionUnblock = "unblock";
  public static final String statusBlocked = "blocked";
  public static final String statusNotBlocked = "not blocked";

  public Window(String name) {
    super(name);
    this.isBlocked = false;
    super.setStatus(Device.statusClosed);
  }

  /**
   * Get the condition of the barrier.
   */
  public boolean isBlocked() {
    return isBlocked;
  }

  /**
   * Set the condition of the barrier.
   */
  public void setBlocked(boolean blocked) {
    isBlocked = blocked;
  }

  /**
   * Get the Device status.
   * @return a [[String]] that compounds the device status, a comma, and the condition of the barrier.
   */
  @Override
  public String getFullStatus() {
    return super.getStatus() + "," + (isBlocked?statusBlocked:statusNotBlocked);
  }

  /**
   * Set the Device status. Only accepts `statusOpen, statusClose`
   * @param status the new status.
   */
  @Override
  void setStatus(String status) {
    if (status.equals(Device.statusOpen) || status.equals(Device.statusClosed)) {
      super.setStatus(status);
    }
  }

  /**
   * Default action system. Only accepts `actionOpen, actionClose, actionBlock, actionUnblock`
   *
   * @param action String with the action code to be performed.
   * @return true if the action was performed, false otherwise.
   */
  @Override
  public boolean doAction(String action) throws WindowBlockedException, SameStatusException, InvalidActionException {
    switch (action) {
      case Device.actionOpen:
        if (getStatus().equals(Window.statusOpen)) {
          throw new SameStatusException(this);
        } else if (isBlocked) {
          throw new WindowBlockedException(this);
        }
        setStatus(Device.statusOpen);
        return true;
      case Device.actionClose:
        if (getStatus().equals(Window.statusClosed)) {
          throw new SameStatusException(this);
        } else if (isBlocked) {
          throw new WindowBlockedException(this);
        }
        setStatus(Device.statusClosed);
        return true;
      case actionBlock:
        if (isBlocked) {
          throw new SameStatusException(this);
        }
        setBlocked(true);
        return true;
      case actionUnblock:
        if (!isBlocked) {
          throw new SameStatusException(this);
        }
        setBlocked(false);
        return true;
      default:
        throw new InvalidActionException(this);
    }
  }

  /**
   * By design, a window always connects to Outside
   * @return the [[models.modules.SHS Outside]] instance registered in [[models.modules.SHS SHS]]
   */
  @Override
  public Location getSecondLocation() {
    return SHS.getOutside();
  }

  @Override
  public String toString() {
    return "[" + getLocation().getName() + "] " + getName();
  }
}
