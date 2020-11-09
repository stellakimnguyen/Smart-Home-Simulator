package models.devices;

import models.Location;
import models.Observable;
import models.Observer;
import models.exceptions.DeviceException;
import models.exceptions.InvalidActionException;
import models.exceptions.SameStatusException;
import models.modules.SHC;
import models.modules.SHP;

import java.util.HashSet;
import java.util.Set;

/**
 * Extends a [[models.devices.Device Device]]: represents a light source.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class MovementDetector extends Device implements Observer, Observable {
  private final Set<Observer> observers = new HashSet<>();

  public MovementDetector(String name) {
    super(name);
    setStatus(Device.statusOff);
    addObserver(SHP.getInstance());
    if (SHC.getInstance().isAutoLights()) {
      addObserver(SHC.getInstance());
    }
  }
  /**
   * Set the [[models.Location Location]] the Device is in and registers as an [[models.Observer Observer]],
   * while unregistering from the previous [[models.Location Location]].
   * @return true if the [[models.Location Location]] was changed successfully, false otherwise.
   */
  @Override
  public boolean setLocation(Location location) {
    Location oldLocation = getLocation();
    boolean wasSuccessful = super.setLocation(location);
    if (wasSuccessful) {
      if (oldLocation!=null) {
        oldLocation.removeObserver(this);
      }
      location.addObserver(this);
    }
    return wasSuccessful;
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
          notifyObservers();
          throw new SameStatusException(this);
        }
        super.setStatus(Device.statusOff);
        notifyObservers();
        return true;
      case actionOn:
        if (getStatus().equals(statusOn)) {
          notifyObservers();
          throw new SameStatusException(this);
        }
        super.setStatus(Device.statusOn);
        notifyObservers();
        return true;
      default:
        throw new InvalidActionException(this);
    }
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for(Observer observer : observers) {
      observer.observe(this);
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
