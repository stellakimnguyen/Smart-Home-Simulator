package models.modules;

import models.Location;
import models.Observable;
import models.Observer;
import models.User;
import models.devices.Light;
import models.devices.MovementDetector;
import models.exceptions.DeviceException;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Smart Home security module, it handles permissions and alarms. It is a Singleton Class.
 *
 * ===Attributes===
 * `currentTime (private [[java.time.LocalDateTime LocalDateTime]]):` Simulation's current time.
 *
 * @version 2
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
@Singleton
public class SHP extends Module implements Observer {
  private boolean isAway;
  private LocalDateTime timeToContactAuthorities;
  private boolean isUnderInvasion;
  private int timeBeforeAuthorities;
  private final Set<Light> awayLights = new HashSet<>();
  private LocalTime awayLightStart = LocalTime.of(21,0,0);
  private LocalTime awayLightEnd = LocalTime.of(5,15,0);
  private boolean isTimeForLightsOn = false;

  private static final SHP instance = new SHP();

  /**
   * Get the Singleton Class instance for SHP.
   */
  public static SHP getInstance(){
    return instance;
  }

  private SHP() {
    super("SHP");
    isAway = false;
    isUnderInvasion = false;
    timeBeforeAuthorities = 10;
  }

  public void toggleAway() {
    Clock clock = Clock.getInstance();
    if (!isAway) {
      for (User user : SHS.getInstance().getUserMap().values()) {
        if (user.getLocation().getLocationType() != Location.LocationType.Outside) {
          SHC.logger.log("Away mode could not be turned on, there is someone in the house.", Logger.MessageType.warning);
          return;
        }
      }
      isAway = true;
      setRegistrationForAll("MovementDetector", true);
      clock.addObserver(this);
      SHC.logger.log(this, "Away mode has been turned on.", Logger.MessageType.normal);
      SHC.lockAllDoors();
      SHC.closeAllWindows();
    } else {
      isAway = false;
      setRegistrationForAll("MovementDetector", false);
      clock.removeObserver(this);
      isUnderInvasion = false;
      timeToContactAuthorities = null;
      SHC.logger.log(this, "Away mode has been turned off.", Logger.MessageType.normal);
    }

  }

  public boolean isAway() {
    return isAway;
  }

  public LocalDateTime getTimeToContactAuthorities() {
    return timeToContactAuthorities;
  }

  public void setTimeToContactAuthorities(LocalDateTime timeToContactAuthorities) {
    this.timeToContactAuthorities = timeToContactAuthorities;
  }

  public boolean isUnderInvasion() {
    return isUnderInvasion;
  }

  public void setUnderInvasion(boolean underInvasion) {
    isUnderInvasion = underInvasion;
  }

  public int getTimeBeforeAuthorities() {
    return timeBeforeAuthorities;
  }

  public void setTimeBeforeAuthorities(int timeBeforeAuthorities) {
    this.timeBeforeAuthorities = timeBeforeAuthorities;
  }

  public void registerLight(Light light) {
    awayLights.add(light);
  }

  public void unregisterLight(Light light) {
    awayLights.remove(light);
  }

  public boolean isLightRegistered(Light light) {
    return awayLights.contains(light);
  }

  public boolean isAutoLightsInAwayMode() {
    return !awayLightStart.equals(awayLightEnd);
  }

  /**
   * Get the Auto Light Start time String.
   */
  public String getAwayLightStartString() {
    return awayLightStart.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
  }

  public LocalTime getAwayLightStart() {
    return awayLightStart;
  }

  public void setAwayLightStart(LocalTime awayLightStart) {
    this.awayLightStart = awayLightStart;
  }

  /**
   * Get the Auto Light End time String.
   */
  public String getAwayLightEndString() {
    return awayLightEnd.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
  }

  public LocalTime getAwayLightEnd() {
    return awayLightEnd;
  }

  public void setAwayLightEnd(LocalTime awayLightEnd) {
    this.awayLightEnd = awayLightEnd;
  }

  @Override
  public void observe(Observable observable) {
    if (isAway) {
      if (observable instanceof MovementDetector) {
        MovementDetector movementDetector = (MovementDetector)observable;
        if (movementDetector.getStatus().equals(MovementDetector.statusOn)) {
          if (!isUnderInvasion) {
            isUnderInvasion = true;
            timeToContactAuthorities = Clock.getInstance().getTime().plusSeconds(timeBeforeAuthorities);
            // TODO mobile alert.
          }
          SHC.logger.log(this, "Movement has been detected at '" + movementDetector.getLocation().getName() + "' while in away mode.", Logger.MessageType.warning);
        }
      }
      if (observable instanceof Clock) {
        Clock clock = (Clock)observable;
        if (isUnderInvasion) {
          LocalDateTime simulationDateTime = clock.getTime();
          if (simulationDateTime.isAfter(timeToContactAuthorities.minusSeconds(1))) {
            // TODO mobile alert.
            SHC.logger.log(this, "Authorities have been contacted because of the break in.", Logger.MessageType.danger); // TODO Actually contact authorities.
            toggleAway();
          }
        }
        if (isAutoLightsInAwayMode()) {
          LocalTime simulationTime = clock.getTime().toLocalTime();
          // Converse check for simulation time: check for case where start and end are on different days.
          if (((awayLightEnd.isBefore(awayLightStart)) && ((simulationTime.isBefore(awayLightEnd)) || (simulationTime.isAfter(awayLightStart))))
                  || ((awayLightStart.isBefore(awayLightEnd)) && ((simulationTime.isBefore(awayLightEnd)) && (simulationTime.isAfter(awayLightStart))))) {
            isTimeForLightsOn = true;
            for (Light light : awayLights) {
              try {
                light.doAction(Light.actionOn);
                SHC.logger.log(this,"The " + light.getName() + " has been turned " + Light.statusOn + " during Away mode.", Logger.MessageType.normal);
              } catch (DeviceException e) {
                //Do nothing
              }
            }
          } else {
            if (isTimeForLightsOn) {
              isTimeForLightsOn = false;
              for (Light light : awayLights) {
                try {
                  light.doAction(Light.actionOff);
                  SHC.logger.log(this,"The " + light.getName() + " has been turned " + Light.statusOff + " during Away mode.", Logger.MessageType.normal);
                } catch (DeviceException e) {
                  //Do nothing
                }
              }
            }
          }
        }
      }
    }
  }
}
