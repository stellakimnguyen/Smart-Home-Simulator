package models;

import controllers.HomeController;

import java.util.HashMap;
import java.util.Map;

public class Location {
  private String name;
  private int temperature;
  private LocationType locationType;
  private Map<String, Device> deviceMap;

  public enum LocationType {
    Outdoor, // A room that is still part of the house, but is not enclosed.
    Indoor, // A room that is part of the house
    Outside // Not in the house
  }

  public Location(String name, LocationType locationType) {
    this.name = name;
    this.temperature = HomeController.defaultTemperature;
    this.locationType = locationType;
    this.deviceMap = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getTemperature() {
    return temperature;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  public LocationType getLocationType() {
    return locationType;
  }

  public void setLocationType(LocationType locationType) {
    if (locationType == LocationType.Outside) {
      this.locationType = LocationType.Outdoor;
    } else if (locationType != null) {
      this.locationType = locationType;
    }
  }

  public Map<String, Device> getDeviceMap() {
    return deviceMap;
  }

  public void setDeviceMap(Map<String, Device> deviceMap) {
    if (deviceMap != null) {
      this.deviceMap = deviceMap;
    }
  }
}
