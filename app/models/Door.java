package models;

/**
 * Extends a [[models.Connection Connection]]: introduces a barrier between both [[models.Location Locations]], new statuses, and new actions.
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
   * Get the Device status.
   * @return a [[java.lang.String String]] that compounds the device status, a comma, and the condition of the lock.
   */
  @Override
  public String getStatus() {
    return super.getStatus() + "," + (isLocked?statusLocked:statusNotLocked);
  }

  /**
   * Set the Device status. Only accepts `statusOpen, statusClose`
   */
  @Override
  public void setStatus(String status) {
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
  public boolean doAction(String action) {
    if (action.equals(Device.actionOpen) && !isLocked) {
      super.setStatus(Device.statusOpen);
      return true;
    } else if (action.equals(Device.actionClose)) {
      super.setStatus(Device.statusClosed);
      return true;
    } else if (action.equals(actionLock) && getStatus().equals(Device.statusClosed)) {
      setLocked(true);
      return true;
    } else if (action.equals(actionUnlock)) {
      setLocked(false);
      return true;
    }
    return false;
  }
}
