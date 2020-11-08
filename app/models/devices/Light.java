package models.devices;

import models.Location;
import models.exceptions.InvalidActionException;
import models.exceptions.SameStatusException;

/**
 * Extends a [[models.devices.Device Device]]: represents a light source.
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
  void setStatus(String status) {
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
  public boolean doAction(String action) throws SameStatusException, InvalidActionException {
    switch (action) {
      case actionOff:
        if (getStatus().equals(statusOff)) {
          throw new SameStatusException(this);
        }
        super.setStatus(Device.statusOff);
        return true;
      case actionOn:
        if (getStatus().equals(statusOn)) {
          throw new SameStatusException(this);
        }
        super.setStatus(Device.statusOn);
        return true;
      default:
        throw new InvalidActionException(this);
    }
  }

  /**
   * setLocation Template Step, Lights are allowed at ALL [[models.Location LocationTypes]].
   * @return true.
   */
  @Override
  public boolean checkLocationTypeAllowed(Location location) {
    return true;
  }
}
