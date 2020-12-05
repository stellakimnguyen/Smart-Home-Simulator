package models.devices;

import models.Location;
import models.Observable;
import models.Temperature;
import models.exceptions.DeviceException;
import models.exceptions.InvalidActionException;
import models.exceptions.SameStatusException;
import models.modules.Clock;

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

  public static final String actionHeat = "start heating";
  public static final String actionCool = "start cooling";
  public static final String statusHeating = "heating";
  public static final String statusCooling = "cooling";

  public TemperatureControl(String name) {
    super(name);
    permitStatus(statusOff);
    permitStatus(statusHeating);
    permitStatus(statusCooling);
    setStatus(statusOff);
    isOnManualMode = false;
    targetTemperature = new Temperature();
    Clock.getInstance().addObserver(this);
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
    //TODO handle the heating/cooling on the SHH at every tick of the clock.


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
