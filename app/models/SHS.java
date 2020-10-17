package models;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.*;

@Singleton
public class SHS extends Module {
  private LocalDateTime currentTime;
  private boolean isRunning;
  private User activeUser;

  private Map<String, User> userMap;
  private List<Module> moduleList;
  private Map<String, Location> home;

  private static final SHS instance = new SHS("SHS");
  private static final Location outside = new Location("Outside", Location.LocationType.Outside);
  public static SHS getInstance(){
    return instance;
  }
  public static Location getOutside() {
    return outside;
  }

  private SHS(String name) {
    super(name);
    this.userMap = new HashMap<>();
    this.moduleList = new LinkedList<>();
    this.home = new HashMap<>();
    this.isRunning = false;
    this.home.put(outside.getName(), outside);
    this.currentTime = LocalDateTime.now();
  }

  public LocalDateTime getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(LocalDateTime currentTime) {
    if (currentTime != null) {
      this.currentTime = currentTime;
    }
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void setRunning(boolean running) {
    isRunning = running;
  }

  public User getActiveUser() {
    return activeUser;
  }

  public void setActiveUser(String activeUserName) {
    if (userMap.containsKey(activeUserName)) {
      this.activeUser = userMap.get(activeUserName);
    }
  }

  public Map<String, User> getUserMap() {
    return userMap;
  }

  public void setUserMap(Map<String, User> userMap) {
    if (userMap != null) {
      this.userMap = userMap;
    }
  }

  public List<Module> getModuleList() {
    return moduleList;
  }

  public void setModuleList(List<Module> moduleList) {
    if (moduleList != null) {
      this.moduleList = moduleList;
    }
  }

  public Map<String, Location> getHome() {
    return home;
  }

  public void setHome(Map<String, Location> home) {
    if (home != null) {
      this.home = home;
      this.home.put(outside.getName(), outside);
    }
  }

  public void setOutsideTemperature(int temperature) {
    for (Location location : home.values()) {
      if (location.getLocationType() == Location.LocationType.Outdoor || location.getLocationType() == Location.LocationType.Outside) {
        location.setTemperature(temperature);
      }
    }
  }
}
