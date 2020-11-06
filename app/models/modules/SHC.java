package models.modules;

import models.Location;
import models.Observable;
import models.Observer;
import models.devices.Device;
import models.devices.Light;
import models.devices.MovementDetector;
import models.exceptions.DeviceException;
import models.exceptions.InvalidActionException;
import models.exceptions.SameStatusException;

import javax.inject.Singleton;

/**
 * Smart Home Core functionality module, it handles Device functionality and access. It is a Singleton Class.
 *
 * ===Attributes===
 * `currentTime (private [[java.time.LocalDateTime LocalDateTime]]):` Simulation's current time.
 *
 * @version 2
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
@Singleton
public class SHC extends Module implements Observer {
  private boolean autoLights;
  public static final Logger logger = new Logger();


  private static final SHC instance = new SHC("SHC");

  /**
   * Get the Singleton Class instance for SHS.
   */
  public static SHC getInstance(){
    return instance;
  }

  private SHC(String name) {
    super(name);
  }

  public boolean isAutoLights() {
    return autoLights;
  }

  public void toggleAutoLights() {
    autoLights = !autoLights;
    for (Location location : SHS.getInstance().getHome().values()) {
      for (Device device : location.getDeviceMap().values()) {
        if (device instanceof MovementDetector) {
          if (autoLights) {
            ((MovementDetector)device).addObserver(this);
          } else {
            ((MovementDetector)device).removeObserver(this);
          }
        }
      }
    }
  }

  @Override
  public void observe(Observable observable) {
    if (observable instanceof MovementDetector) {
      MovementDetector toObserve = (MovementDetector)observable;
      for (Device device : toObserve.getLocation().getDeviceMap().values()) {
        if (device instanceof Light) {
          Light toManage = (Light) device;
          try {
            if (toObserve.getLocation().getUserMap().size() == 0) {
              if (toManage.getStatus().equals(Light.statusOn)) {
                toManage.doAction(Light.actionOff);
                logger.log(this, "The '" + toManage.getLocation().getName() + "' light has been turned off by the Auto Mode.", Logger.MessageType.normal);
              }
            } else {
              if (toManage.getStatus().equals(Light.statusOff)) {
                toManage.doAction(Light.actionOn);
                logger.log(this, "The '" + toManage.getLocation().getName() + "' light has been turned on by the Auto Mode.", Logger.MessageType.normal);
              }
            }
          } catch (DeviceException e) {
            // Do nothing
          }
        }
      }
    }
  }
}
