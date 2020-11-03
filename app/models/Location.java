package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * `userMap (private [[java.util.Map Map]]&#91;[[java.lang.String String]], [[models.User User]]&#93;):` The map of all [[models.User Users]] at this location.
 *
 * `defaultTemperature (private static final int):` The temperature that locations default to. Set to 20.00 Celsius.
 *
 * @version 2
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public class Location implements Observable {
  private String name;
  private int temperature;
  private LocationType locationType;
  private final Map<String, Device> deviceMap = new HashMap<>();
  private final Map<String, User> userMap = new HashMap<>();
  private final Set<Observer> observers = new HashSet<>();

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
   * Get the Location temperature String.
   */
  public String getTemperatureString() {
    int base = temperature/100;
    int decimal = temperature%100;
    return base + "." + (decimal<10?"0"+decimal:decimal);
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
   * Get the [[java.util.Map Map]] of [[models.Device Devices]] at the Location. It is a clone therefore modifying the
   * returned map will not affect the location.
   */
  public Map<String, Device> getDeviceMap() {
    return new HashMap<>(deviceMap);
  }

  /**
   * Add a [[models.Device Device]] to the [[java.util.Map Map]] of [[models.Device Devices]] of this location.
   * @param device the [[models.Device Device]] to be added
   */
  public void addDevice(Device device) {
    this.deviceMap.put(device.getName(), device);
  }

  /**
   * Remove a [[models.Device Device]] from the [[java.util.Map Map]] of [[models.Device Devices]] of this location.
   * @param device the [[models.Device Device]] to be removed
   */
  public void removeDevice(Device device) {
    this.deviceMap.remove(device.getName());
  }

  /**
   * Get a [[java.util.Map Map]] of [[models.User Users]] at the Location. It is a clone therefore modifying the
   * returned map will not affect the location.
   */
  public Map<String, User> getUserMap() {
    return new HashMap<>(userMap);
  }

  /**
   * Add a [[models.User User]] to the [[java.util.Map Map]] of [[models.User Users]] of this location.
   * @param user the [[models.User User]] to be added
   */
  public void addUser(User user) {
    this.userMap.put(user.getName(), user);
    notifyObservers();
  }

  /**
   * Remove a [[models.User User]] from the [[java.util.Map Map]] of [[models.User Users]] of this location.
   * @param user the [[models.User User]] to be removed
   */
  public void removeUser(User user) {
    this.userMap.remove(user.getName());
    notifyObservers();
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
}
