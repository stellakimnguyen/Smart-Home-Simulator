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
 * Extends a [[models.devices.Device Device]]: represents a sensor that both [[models.Observer observes]]
 * and can be [[models.Observable observed]] by other classes.
 *
 * @version 3
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public abstract class Sensor extends Device implements Observer, Observable {
  private final Set<Observer> observers = new HashSet<>();

  public Sensor(String name) {
    super(name);
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
}
