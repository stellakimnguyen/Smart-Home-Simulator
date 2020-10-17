package models;

public class Light extends Device {
  public Light(String name) {
    super(name);
    setStatus(Device.statusOff);
  }

  @Override
  public void setStatus(String status) {
    if (status.equals(Device.statusOff) || status.equals(Device.statusOn)) {
      super.setStatus(status);
    }
  }

  @Override
  public boolean doAction(String action) {
    if (action.equals(Device.actionOff)) {
      super.setStatus(Device.statusOff);
      return true;
    } else if (action.equals(Device.actionOn)) {
      super.setStatus(Device.statusOn);
      return true;
    }
    return false;
  }
}
