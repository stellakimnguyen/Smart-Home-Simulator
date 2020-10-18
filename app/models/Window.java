package models;

/**
 * Extends a [[models.Connection Connection]]: fixes the [[models.Location Location]] it connects to to [[models.SHS `Outside`]] and introduces a barrier between both [[models.Location Locations]], new statuses, and new actions.
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
  public String getStatus() {
    return super.getStatus() + "," + (isBlocked?statusBlocked:statusNotBlocked);
  }

  /**
   * Set the Device status. Only accepts `statusOpen, statusClose`
   * @param status the new status.
   */
  @Override
  public void setStatus(String status) {
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
  public boolean doAction(String action) {
    if (action.equals(Device.actionOpen)) {
      super.setStatus(Device.statusOpen);
      return true;
    } else if (action.equals(Device.actionClose)) {
      super.setStatus(Device.statusClosed);
      return true;
    } else if (action.equals(actionBlock)) {
      setBlocked(true);
    } else if (action.equals(actionUnblock)) {
      setBlocked(false);
    }
    return false;
  }
}
