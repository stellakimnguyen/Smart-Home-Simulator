package models.modules;

import models.Observable;
import models.Temperature;
import models.TimePeriod;
import models.Zone;
import models.devices.TemperatureControl;
import models.devices.Window;
import models.exceptions.DeviceException;

import javax.inject.Singleton;
import java.time.LocalTime;
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
  private TimePeriod period1;
  private boolean isPeriod1Active;
  private TimePeriod period2;
  private boolean isPeriod2Active;
  private TimePeriod period3;
  private boolean isPeriod3Active;
  private int currentPeriod;
  private Temperature summerTemperature;
  private Temperature winterTemperature;
  private boolean isAway;
  private Temperature minThreshold;
  private Temperature maxThreshold;

  private static final SHH instance = new SHH("SHH");

  /**
   * Get the Singleton Class instance for SHH.
   */
  public static SHH getInstance(){
    return instance;
  }

  private SHH(String name) {
    super(name);
    Clock.getInstance().addObserver(this);
    currentPeriod = 0;
    summerTemperature = new Temperature();
    winterTemperature = new Temperature();
    minThreshold = new Temperature();
    minThreshold.setTemperature(0);
    maxThreshold = new Temperature();
    maxThreshold.setTemperature(10000);
    SHP.getInstance().addObserver(this);
  }

  public Map<Integer, Zone> getZones() {
    return zones;
  }

  public void setZones(Map<Integer, Zone> zones) {
    this.zones = zones;
  }

  public TimePeriod getPeriod1() {
    return period1;
  }

  public void setPeriod1(TimePeriod period1) {
    this.period1 = period1;
  }

  public boolean isPeriod1Active() {
    return isPeriod1Active;
  }

  public void togglePeriod1() {
    isPeriod1Active = !isPeriod1Active;
  }

  public TimePeriod getPeriod2() {
    return period2;
  }

  public void setPeriod2(TimePeriod period2) {
    this.period2 = period2;
  }

  public boolean isPeriod2Active() {
    return isPeriod2Active;
  }

  public void togglePeriod2() {
    isPeriod2Active = !isPeriod2Active;
  }

  public TimePeriod getPeriod3() {
    return period3;
  }

  public void setPeriod3(TimePeriod period3) {
    this.period3 = period3;
  }

  public boolean isPeriod3Active() {
    return isPeriod3Active;
  }

  public void togglePeriod3() {
    isPeriod3Active = !isPeriod3Active;
  }

  public Temperature getSummerTemperature() {
    return summerTemperature;
  }

  public void setSummerTemperature(Temperature summerTemperature) {
    this.summerTemperature = summerTemperature;
  }

  public Temperature getWinterTemperature() {
    return winterTemperature;
  }

  public void setWinterTemperature(Temperature winterTemperature) {
    this.winterTemperature = winterTemperature;
  }

  public Temperature getMinThreshold() {
    return minThreshold;
  }

  public void setMinThreshold(Temperature minThreshold) {
    this.minThreshold = minThreshold;
  }

  public Temperature getMaxThreshold() {
    return maxThreshold;
  }

  public void setMaxThreshold(Temperature maxThreshold) {
    this.maxThreshold = maxThreshold;
  }

  @Override
  public void observe(Observable observable) {
    if (observable instanceof TemperatureControl) {
      TemperatureControl toObserve = (TemperatureControl)observable;
      if (toObserve.getStatus().equals(TemperatureControl.statusCooling) && SHS.getInstance().isSummer()) {
        if (isAway) {
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
      if (toObserve.getLocation().getTemperature().compareTo(minThreshold) <= 0) {
        SHC.logger.log(this,"The temperature at " + toObserve.getLocation().getName() + " is at or below " + minThreshold.getTemperatureString() + " °C", Logger.MessageType.danger);
      } else if (toObserve.getLocation().getTemperature().compareTo(maxThreshold) >= 0) {
        SHC.logger.log(this,"The temperature at " + toObserve.getLocation().getName() + " is at or above " + maxThreshold.getTemperatureString() + " °C", Logger.MessageType.danger);
      }
    }

    if (observable instanceof Clock) {
      LocalTime time = ((Clock)observable).getTime().toLocalTime();
      int period;
      if (isAway && SHS.getInstance().isSummer()) {
        period = -1;
      } else if (isAway && SHS.getInstance().isWinter()) {
        period = -2;
      } else if (isPeriod1Active && period1.isInPeriod(time)) {
        period = 1;
      } else if (isPeriod2Active && period2.isInPeriod(time)) {
        period = 2;
      } else if (isPeriod3Active && period3.isInPeriod(time)) {
        period = 3;
      } else {
        period = 0;
      }
      if (period != currentPeriod) {
        changePeriod(period);
      }
    }

    if (observable instanceof SHP) {
      isAway = ((SHP)observable).isAway();
    }
  }

  private void changePeriod(int period) {
    currentPeriod = period;
    if (period == -1) {
      changeTargetTemperature(summerTemperature);
    } else if (period == -2) {
      changeTargetTemperature(winterTemperature);
    } else {
      for (Zone zone : zones.values()) {
        zone.changePeriod(period);
      }
    }
  }

  private void changeTargetTemperature(Temperature targetTemperature) {
    for (Zone zone : zones.values()) {
      zone.setTargetTemperature(targetTemperature);
    }
  }
}
