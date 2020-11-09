package models.modules;

import models.ClockThread;
import models.Location;
import models.User;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main class of the Smart Home System, it stores the data necessary for the execution of the simulation. It is a Singleton Class.
 *
 * ===Attributes===
 * `currentTime (private [[java.time.LocalDateTime LocalDateTime]]):` Simulation's current time.
 *
 * `isRunning (private boolean):` The status of the simulation.
 *
 * `activeUser (private [[models.User User]]):` The user currently logged in.
 *
 * `userMap (private [[java.util.Map Map]]&#91;[[java.lang.String String]], [[models.User User]]&#93;):` The map of all registered [[models.User Users]].
 *
 * `moduleList (private [[java.util.List List]]&#91;[[models.modules.Module Module]]&#93;):` The List of all registered [[models.modules.Module Modules]].
 *
 * `home (private [[java.util.Map Map]]&#91;[[java.lang.String String]], [[models.Location Location]]&#93;):` The map of all registered [[models.Location Locations]] within the home.
 *
 * `outside (private static final [[models.Location Location]]):` The predefined outside [[models.Location Location]], to be injected into every `home`.
 *
 * `instance (private static final SHS):` The instance of this singleton class.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
@Singleton
public class SHS extends Module {
  private boolean isRunning;
  private User activeUser;
  private final Clock clock = Clock.getInstance();

  private Map<String, User> userMap;
  private List<Module> moduleList;
  private Map<String, Location> home;

  private static final Location outside = new Location("Outside", Location.LocationType.Outside);
  private static final SHS instance = new SHS("SHS");

  /**
   * Get the Singleton Class instance for SHS.
   */
  public static SHS getInstance(){
    return instance;
  }

  /**
   * Get the predefined outside [[models.Location Location]].
   */
  public static Location getOutside() {
    return outside;
  }

  private SHS(String name) {
    super(name);
    this.userMap = new HashMap<>();
    User defaultUser = new User("Starting User", User.UserType.Parent);
    this.userMap.put(defaultUser.getName(), defaultUser);
    this.activeUser = defaultUser;
    this.moduleList = new LinkedList<>();
    this.home = new HashMap<>();
    this.isRunning = false;
    this.home.put(outside.getName(), outside);
  }

  /**
   * Get the SHS' current time.
   */
  public LocalDateTime getSimulationTime() {
    return clock.getTime();
  }

  /**
   * Get the SHS' current time String.
   */
  public String getTimeString() {
    return getSimulationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
  }

  /**
   * Get the SHS' current date String.
   */
  public String getDateString() {
    return getSimulationTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }

  /**
   * Set the SHS' current time.
   */
  public void setSimulationTime(LocalDateTime simulationTime) {
    if (simulationTime != null) {
      clock.setTime(simulationTime);
    }
  }

  /**
   * Get the SHS' status.
   */
  public boolean isRunning() {
    return isRunning;
  }

  /**
   * Set the SHS' status.
   */
  public void setRunning(boolean running) {
    isRunning = running;
  }

  /**
   * Get the SHS' logged in [[models.User User]].
   */
  public User getActiveUser() {
    return activeUser;
  }

  /**
   * Set the SHS' logged in [[models.User User]]. It must be registered in `userMap`.
   */
  public void setActiveUser(String activeUserName) {
    if (userMap.containsKey(activeUserName)) {
      this.activeUser = userMap.get(activeUserName);
    }
  }

  /**
   * Get the [[java.util.Map Map]] of [[models.User Users]] registered in the SHS.
   */
  public Map<String, User> getUserMap() {
    return userMap;
  }

  /**
   * Set the [[java.util.Map Map]] of [[models.User Users]] registered in the SHS.
   */
  public void setUserMap(Map<String, User> userMap) {
    if (userMap != null) {
      this.userMap = userMap;
    }
  }

  /**
   * Get the number of [[models.User Parents]] registered in the SHS.
   */
  public int getParentAmount(){
    int amount = 0;
    for (User toCheck : userMap.values()) {
      if (toCheck.getType() == User.UserType.Parent) {
        amount++;
      }
    }
    return amount;
  }

  /**
   * Get the [[java.util.List List]] of [[models.modules.Module Modules]] registered in the SHS.
   */
  public List<Module> getModuleList() {
    return moduleList;
  }

  /**
   * Set the [[java.util.List List]] of [[models.modules.Module Modules]] registered in the SHS.
   */
  public void setModuleList(List<Module> moduleList) {
    if (moduleList != null) {
      this.moduleList = moduleList;
    }
  }

  /**
   * Get the [[java.util.Map Map]] of [[models.Location Locations]] registered in the SHS.
   */
  public Map<String, Location> getHome() {
    return new TreeMap<>(home);
  }

  /**
   * Set the [[java.util.Map Map]] of [[models.Location Locations]] registered in the SHS. It will automatically inject `Outside` into the new [[java.util.Map Map]].
   */
  public void setHome(Map<String, Location> home) {
    if (home != null) {
      this.home = home;
      this.home.put(outside.getName(), outside);
    }
  }

  /**
   * Set the temperature of all `Outside` and `Outdoor` [[models.Location Locations]] registered in the SHS.
   * @param temperature int representation of a two digit precision decimal, by multiplying it by 100.
   */
  public void setOutsideTemperature(int temperature) {
    for (Location location : home.values()) {
      if (location.getLocationType() == Location.LocationType.Outdoor || location.getLocationType() == Location.LocationType.Outside) {
        location.setTemperature(temperature);
      }
    }
  }

  public int getTimeMultiplier() {
    return clock.getTimeMultiplier();
  }

  public void setTimeMultiplier(int timeMultiplier) {
    clock.setTimeMultiplier(timeMultiplier);
  }

  public void startClock() {
    clock.startClock();
  }

  public void stopClock() {
    clock.stopClock();
  }
}
