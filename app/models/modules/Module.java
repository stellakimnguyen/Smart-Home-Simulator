package models.modules;

import models.Location;
import models.Observable;
import models.Observer;
import models.devices.Device;
import models.devices.MovementDetector;

import java.util.Set;
import java.util.HashSet;

/**
 * Template for all modules registered at [[models.modules.SHS SHS]]. Contains common attributes.
 * All modules are [[models.Observable Observables]] and [[models.Observer Observers]]
 * ===Attributes===
 * `name (private [[java.lang.String String]]):` Unique identifier for the Module.
 *
 * `observers (private [[java.util.Set Set]]&#91;[[models.Observer Observer]]&#93;):` Set of [[models.Observer Observers]]
 * observing this [[models.modules.Module Module]].
 *
 * @version 2
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public abstract class Module implements Observer, Observable {
  private String name;
  private final Set<Observer> observers = new HashSet<>();

  public Module(String name) {
    this.name = name;
  }

  /**
   * Get the Module name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the Module name.
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void addObserver(Observer observer) {
    this.observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    this.observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.observe(this);
    }
  }

  public void setRegistrationForAll(String deviceType, boolean isRegistering) {
    for (Location location : SHS.getInstance().getHome().values()) {
      for (Device device : location.getDeviceMap().values()) {
        switch (deviceType) {
          case "MovementDetector":
            if (device instanceof MovementDetector) {
              if (isRegistering) {
                ((MovementDetector) device).addObserver(this);
              } else {
                ((MovementDetector) device).removeObserver(this);
              }
            }
            break;
          default:
            //Do nothing TODO some kind of error handling
        }
      }
    }
  }
}
