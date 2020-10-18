package models;

/**
 * Extends a [[models.Device Device]]: represents a light source.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class Light extends Device {
  public Light(String name) {
    super(name);
    setStatus(Device.statusOff);
  }

  /**
   * Set the Device status. Only accepts `statusOn, statusOff`
   */
  @Override
  public void setStatus(String status) {
    if (status.equals(Device.statusOff) || status.equals(Device.statusOn)) {
      super.setStatus(status);
    }
  }

  /**
   * Default action system. Only accepts `actionOn, actionOff`
   *
   * @param action String with the action code to be performed.
   * @return true if the action was performed, false otherwise.
   */
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
