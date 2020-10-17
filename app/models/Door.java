package models;

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

  public boolean isLocked() {
    return isLocked;
  }

  public void setLocked(boolean locked) {
    isLocked = locked;
  }

  @Override
  public String getStatus() {
    return super.getStatus() + "," + (isLocked?statusLocked:statusNotLocked);
  }

  @Override
  public void setStatus(String status) {
    if (status.equals(Device.statusOpen) || status.equals(Device.statusClosed)) {
      super.setStatus(status);
    }
  }

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
