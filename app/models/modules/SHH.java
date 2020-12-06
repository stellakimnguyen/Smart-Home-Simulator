package models.modules;

import models.Observable;
import models.Zone;
import models.devices.TemperatureControl;
import models.devices.Window;
import models.exceptions.DeviceException;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

/**
 * Smart Home Heating functionality module, it handles HVAC functionality. It is a Singleton Class.
 *
 * ===Attributes===
 * `currentTime (private [[java.time.LocalDateTime LocalDateTime]]):` Simulation's current time.
 *
 * @version 3
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
@Singleton
public class SHH extends Module {
  private Map<Integer,Zone> zones;

  private static final SHH instance = new SHH("SHH");

  /**
   * Get the Singleton Class instance for SHH.
   */
  public static SHH getInstance(){
    return instance;
  }

  private SHH(String name) {
    super(name);
  }

  public Map<Integer, Zone> getZones() {
    return zones;
  }

  public void setZones(Map<Integer, Zone> zones) {
    this.zones = zones;
  }

  @Override
  public void observe(Observable observable) {
    if (observable instanceof TemperatureControl) {
      TemperatureControl toObserve = (TemperatureControl)observable;
      switch (toObserve.getStatus()) {
        case TemperatureControl.statusCooling:
          if (SHS.getInstance().isSummer()) {
            if (!SHP.getInstance().isAway()) {
              Set<Window> windows = SHS.getWindows(toObserve.getLocation());
              boolean isBlocked = false;
              for (Window window : windows) {
                if (window.isBlocked() && window.getStatus().equals(Window.statusClosed)) {
                  isBlocked = true;
                  SHC.logger.log(this,"Window " + window.toString() + " is " + Window.statusBlocked + ", can not open it to reduce energy consumption.", Logger.MessageType.danger);
                }
              }
              if (!isBlocked && (windows.size() > 0)) {
                for (Window window : windows) {
                  try {
                    window.doAction(Window.actionOpen);
                    SHC.logger.log(this,"Window " + window.toString() + " is now " + Window.statusOpen, Logger.MessageType.normal);
                  } catch (DeviceException e) {
                    // do nothing (window is already open)
                  }
                }
                try {
                  toObserve.doAction(TemperatureControl.actionOff);
                  SHC.logger.log(this,"All windows in [" + toObserve.getLocationString() + "] are now " + Window.statusOpen, Logger.MessageType.normal);
                } catch (DeviceException e) {
                  e.printStackTrace(); //Should never happen
                }
              }
            }
          }
          break;
        case TemperatureControl.statusOff:

      }

    }
  }
}
