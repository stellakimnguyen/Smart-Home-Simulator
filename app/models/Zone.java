package models;

import models.devices.TemperatureControl;

import java.util.HashSet;
import java.util.Set;

public class Zone {
  private final Set<TemperatureControl> locations = new HashSet<>();
  private Temperature targetTemperature = new Temperature();
  private Temperature defaultTemperature = new Temperature();
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

  public Temperature getTargetTemperature() {
    return targetTemperature;
  }

  public void setTargetTemperature(Temperature targetTemperature) {
    this.targetTemperature = targetTemperature;
  }

  public Temperature getDefaultTemperature() {
    return defaultTemperature;
  }

  public void setDefaultTemperature(Temperature defaultTemperature) {
    this.defaultTemperature = defaultTemperature;
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

  public void changePeriod(int period) {
    switch (period) {
      case 1:
        targetTemperature = periodTemperature1;
        return;
      case 2:
        targetTemperature = periodTemperature2;
        return;
      case 3:
        targetTemperature = periodTemperature3;
        return;
      default:
        targetTemperature = defaultTemperature;
    }
  }
}
