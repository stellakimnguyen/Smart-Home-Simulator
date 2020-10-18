package models;

import java.util.HashMap;
import java.util.Map;

/**
 * A physical location.
 * ===Inner Class===
 * `LocationType (public [[enum]]):` Predefined values for the type of locations:
 *  - `Indoor:` A room that is part of the house
 *  - `Outdoor:` A location that is still part of the house, but is not enclosed.
 *  - `Outside:` A location that is not in the house
 *
 * ===Attributes===
 * `name (private [[java.lang.String String]]):` Unique identifier for the Location.
 *
 * `temperature (private int):` The temperature at the location. Represents a two digit precision decimal.
 *
 * `locationType (private LocationType):` The type of the location.
 *
 * `deviceMap (private [[java.util.Map Map]]&#91;[[java.lang.String String]], [[models.Device Device]]&#93;):` The map of all [[models.Device Devices]] at this location.
 *
 * `defaultTemperature (private static final int):` The temperature that locations default to. Set to 20.00 Celsius.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class Location {
  private String name;
  private int temperature;
  private LocationType locationType;
  private Map<String, Device> deviceMap;

  public static final int defaultTemperature = 2000;

  public enum LocationType {
    Outdoor, // A room that is still part of the house, but is not enclosed.
    Indoor, // A room that is part of the house
    Outside // Not in the house
  }

  public Location(String name, LocationType locationType) {
    this.name = name;
    this.temperature = defaultTemperature;
    this.locationType = locationType;
    this.deviceMap = new HashMap<>();
  }

  /**
   * Get the Location name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the Location name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the Location temperature.
   */
  public int getTemperature() {
    return temperature;
  }

  /**
   * Set the Location temperature.
   */
  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  /**
   * Get the Location type.
   */
  public LocationType getLocationType() {
    return locationType;
  }

  /**
   * Set the Location type.
   */
  public void setLocationType(LocationType locationType) {
    if (locationType == LocationType.Outside) {
      this.locationType = LocationType.Outdoor;
    } else if (locationType != null) {
      this.locationType = locationType;
    }
  }

  /**
   * Get the [[java.util.Map Map]] of [[models.Device Devices]] at the Location.
   */
  public Map<String, Device> getDeviceMap() {
    return deviceMap;
  }

  /**
   * Set the [[java.util.Map Map]] of [[models.Device Devices]] at the Location.
   */
  public void setDeviceMap(Map<String, Device> deviceMap) {
    if (deviceMap != null) {
      this.deviceMap = deviceMap;
    }
  }
}
