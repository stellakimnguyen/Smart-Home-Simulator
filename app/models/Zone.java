package models;

import models.devices.TemperatureControl;

import java.util.HashSet;
import java.util.Set;

public class Zone {
  private final Set<TemperatureControl> locations = new HashSet<>();
  private Temperature periodTemperature1 = new Temperature();
  private Temperature periodTemperature2 = new Temperature();
  private Temperature periodTemperature3 = new Temperature();

  public Zone() {
  }
  public Zone(TemperatureControl location) {
    locations.add(location);
  }

  public boolean addLocation(TemperatureControl temperatureControl) {
    return locations.add(temperatureControl);
  }

  public boolean removeLocation(TemperatureControl temperatureControl) {
    return locations.remove(temperatureControl);
  }

  public Set<TemperatureControl> getLocations() {
    return locations;
  }

  public Temperature getPeriodTemperature1() {
    return periodTemperature1;
  }

  public void setPeriodTemperature1(Temperature periodTemperature1) {
    this.periodTemperature1 = periodTemperature1;
  }

  public Temperature getPeriodTemperature2() {
    return periodTemperature2;
  }

  public void setPeriodTemperature2(Temperature periodTemperature2) {
    this.periodTemperature2 = periodTemperature2;
  }

  public Temperature getPeriodTemperature3() {
    return periodTemperature3;
  }

  public void setPeriodTemperature3(Temperature periodTemperature3) {
    this.periodTemperature3 = periodTemperature3;
  }
}
