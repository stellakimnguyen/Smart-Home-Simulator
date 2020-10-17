package models;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.*;

@Singleton
public class SHS extends Module {
  private LocalDateTime currentTime;
  private boolean isRunning;
  private User activeUser;

  private List<User> userList;
  private List<Module> moduleList;
  private Map<String, Location> home;

  private static final SHS instance = new SHS("SHS");
  public static SHS getInstance(){
    return instance;
  }

  private SHS(String name) {
    super(name);
    this.userList = new LinkedList<>();
    this.moduleList = new LinkedList<>();
    this.home = new HashMap<>();
    this.isRunning = false;
  }

  public LocalDateTime getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(LocalDateTime currentTime) {
    this.currentTime = currentTime;
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

  public void setActiveUser(User activeUser) {
    if (userList.contains(activeUser)) {
      this.activeUser = activeUser;
    }
  }

  public List<User> getUserList() {
    return userList;
  }

  public void setUserList(List<User> userList) {
    this.userList = userList;
  }

  public List<Module> getModuleList() {
    return moduleList;
  }

  public void setModuleList(List<Module> moduleList) {
    this.moduleList = moduleList;
  }

  public Map<String, Location> getHome() {
    return home;
  }

  public void setHome(Map<String, Location> home) {
    this.home = home;
  }
}
