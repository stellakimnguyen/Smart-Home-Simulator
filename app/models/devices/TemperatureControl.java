package models.devices;

import models.Location;
import models.Observable;
import models.Temperature;
import models.Zone;
import models.exceptions.InvalidActionException;
import models.exceptions.SameStatusException;
import models.modules.Clock;
import models.modules.SHH;
import models.modules.SHS;

/**
 * Extends a [[models.devices.Sensor Sensor]]: represents a HVAC system (thermostat, heater and AC).
 *
 * @version 3
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public class TemperatureControl extends Sensor {
  private boolean isOnManualMode;
  private final Temperature targetTemperature;
  private Zone zone;

  public static final String actionPause = "pause";
  public static final String actionHeat = "start heating";
  public static final String actionCool = "start cooling";
  public static final String statusPaused = "paused";
  public static final String statusHeating = "heating";
  public static final String statusCooling = "cooling";

  public TemperatureControl(String name) {
    super(name);
    permitStatus(statusOff);
    permitStatus(statusPaused);
    permitStatus(statusHeating);
    permitStatus(statusCooling);
    setStatus(statusPaused);
    isOnManualMode = false;
    targetTemperature = new Temperature();
    Clock.getInstance().addObserver(this);
    addObserver(SHH.getInstance());
  }

  public boolean isOnManualMode() {
    return isOnManualMode;
  }

  public void toggleOnManualMode() {
    isOnManualMode = !isOnManualMode;
  }

  public Temperature getTargetTemperature() {
    return targetTemperature;
  }

  public void setTargetTemperature(int temperature) {
    targetTemperature.setTemperature(temperature);
  }

  public Zone getZone() {
    return zone;
  }

  public void setZone(Zone zone) {
    zone.addLocation(this);
    if (this.zone != null) {
      this.zone.removeLocation(this);
    }
    this.zone = zone;
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
        super.setStatus(statusOff);
        return true;
      case actionHeat:
        if (getStatus().equals(statusHeating)) {
          throw new SameStatusException(this);
        }
        super.setStatus(statusHeating);
        return true;
      case actionCool:
        if (getStatus().equals(statusCooling)) {
          throw new SameStatusException(this);
        }
        super.setStatus(statusCooling);
        return true;
      default:
        throw new InvalidActionException(this);
    }
  }

  @Override
  public void observe(Observable observable) {
    if (observable instanceof Clock) {
      Temperature locationTemperature = getLocation().getTemperature();
      int temperatureOffset = Clock.getInstance().getTimeMultiplier();
      int difference = locationTemperature.compareTo(targetTemperature);
      switch (getStatus()) {
        case statusCooling:
          temperatureOffset = Math.min(Math.abs(difference), temperatureOffset * 10);
          if (difference > 0) {
            locationTemperature.offsetTemperature(-temperatureOffset);
          } else {
            setStatus(statusPaused);
          }
          break;
        case statusHeating:
          temperatureOffset = Math.min(Math.abs(difference), temperatureOffset * 10);
          if (difference < 0) {
            locationTemperature.offsetTemperature(temperatureOffset);
          } else {
            setStatus(statusPaused);
          }
          break;
        case statusPaused:
          if (difference > 20) {
            setStatus(statusCooling);
          } else if (difference < -20) {
            setStatus(statusHeating);
          }
        case statusOff:
          difference = locationTemperature.compareTo(SHS.getOutside().getTemperature());
          temperatureOffset = Math.min(Math.abs(difference), temperatureOffset * 5);
          if (difference < 0) {
            locationTemperature.offsetTemperature(temperatureOffset);
          } else if (difference > 0){
            locationTemperature.offsetTemperature(-temperatureOffset);
          }
      }
      notifyObservers();
    }
  }

  /**
   * setLocation Template Step, Temperature Control Devices are allowed only at [[models.Location Indoor LocationTypes]].
   */
  @Override
  public boolean checkLocationTypeAllowed(Location location) {
    // By default, TemperatureControl is only allowed in "Indoor" locations
    return location.getLocationType() == Location.LocationType.Indoor;
  }
}
