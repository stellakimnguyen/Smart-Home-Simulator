package models.devices;

import models.Location;
import models.Observable;
import models.exceptions.DeviceException;
import models.exceptions.InvalidActionException;
import models.exceptions.SameStatusException;
import models.modules.SHC;
import models.modules.SHP;

/**
 * Extends a [[models.devices.Sensor Sensor]]: represents a movement detector.
 *
 * @version 3
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public class MovementDetector extends Sensor {

  public MovementDetector(String name) {
    super(name);
    permitStatus(statusOn);
    permitStatus(statusOff);
    setStatus(statusOff);
    addObserver(SHP.getInstance());
    if (SHC.getInstance().isAutoLights()) {
      addObserver(SHC.getInstance());
    }
  }

  /**
   * Default action system. Only accepts `actionOn, actionOff`
   *
   * @param action String with the action code to be performed.
   * @return true if the action was performed, false otherwise.
   */
  @Override
  public boolean doAction(String action) throws SameStatusException, InvalidActionException {
    switch (action) {
      case actionOff:
        if (getStatus().equals(statusOff)) {
          notifyObservers();
          throw new SameStatusException(this);
        }
        super.setStatus(statusOff);
        notifyObservers();
        return true;
      case actionOn:
        if (getStatus().equals(statusOn)) {
          notifyObservers();
          throw new SameStatusException(this);
        }
        super.setStatus(statusOn);
        notifyObservers();
        return true;
      default:
        throw new InvalidActionException(this);
    }
  }

  @Override
  public void observe(Observable observable) {
    if (observable instanceof Location) {
      Location toObserve = (Location) observable;
      if (toObserve == getLocation()) {
        try {
          if (toObserve.getUserMap().size() == 0) {
            doAction(Device.actionOff);
          } else {
            doAction(Device.actionOn);
          }
        } catch (DeviceException e) {
          // Do nothing
        }
      }
    }
  }

  /**
   * setLocation Template Step, Movement Detectors are allowed at ALL [[models.Location LocationTypes]].
   * @return true.
   */
  @Override
  public boolean checkLocationTypeAllowed(Location location) {
    return true;
  }
}
