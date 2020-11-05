package models.devices;

import models.Location;
import models.Observable;
import models.Observer;

/**
 * Extends a [[models.devices.Device Device]]: represents a light source.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class MovementDetector extends Device implements Observer, Observable {
  public MovementDetector(String name) {
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

  /**
   * Do nothing, since only the [[models.modules.SHP SHP]] observes this Device
   * @param observer ignored.
   */
  @Override
  public void addObserver(Observer observer) {}

  /**
   * Do nothing, since only the [[models.modules.SHP SHP]] observes this Device
   * @param observer ignored.
   */
  @Override
  public void removeObserver(Observer observer) {}

  /**
   * Notify the Singleton [[models.modules.SHP SHP]] of changes to this Device.
   */
  @Override
  public void notifyObservers() {
    models.modules.SHP.getInstance().observe(this);
  }

  @Override
  public void observe(Observable observable) {
    if (observable instanceof Location) {
      Location toObserve = (Location) observable;
      if (toObserve == getLocation()) {
        if (toObserve.getUserMap().size() == 0) {
          doAction(Device.actionOff);
          notifyObservers();
        } else if (getStatus().equals(Device.actionOff)) {
          doAction(Device.actionOn);
          notifyObservers();
        }
      }
    }
  }
}
