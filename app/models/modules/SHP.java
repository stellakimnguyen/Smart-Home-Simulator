package models.modules;

import models.Location;
import models.Observable;
import models.Observer;
import models.devices.Light;
import models.exceptions.DeviceException;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

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
  private LocalDateTime invasionTime;
  private boolean isUnderInvasion;
  private int timeBeforeAuthorities;
  private boolean autoLightsInAwayMode;
  private final Set<Light> awayLights = new HashSet<>();
  private LocalTime awayLightStart = LocalTime.of(18,0,0);
  private LocalTime awayLightEnd = LocalTime.of(21,15,0);


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
    timeBeforeAuthorities = 5;
  }

  public void toggleAway() {
    isAway = !isAway;
  }

  public boolean isAway() {
    return isAway;
  }

  public LocalDateTime getInvasionTime() {
    return invasionTime;
  }

  public void setInvasionTime(LocalDateTime invasionTime) {
    this.invasionTime = invasionTime;
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

  public void registerLight(Light light, String startString, String endString) {
    LocalTime startTime = LocalTime.parse(startString, DateTimeFormatter.ofPattern("HH:mm:ss"));
    LocalTime endTime = LocalTime.parse(endString, DateTimeFormatter.ofPattern("HH:mm:ss"));

  }

  public void toggleAutoLightsInAwayMode() {
    autoLightsInAwayMode = !autoLightsInAwayMode;
  }

  public boolean isAutoLightsInAwayMode() {
    return autoLightsInAwayMode;
  }

  public LocalTime getAwayLightStart() {
    return awayLightStart;
  }

  public void setAwayLightStart(LocalTime awayLightStart) {
    this.awayLightStart = awayLightStart;
  }

  public LocalTime getAwayLightEnd() {
    return awayLightEnd;
  }

  public void setAwayLightEnd(LocalTime awayLightEnd) {
    this.awayLightEnd = awayLightEnd;
  }

  public void addLight(Light light) {
    awayLights.add(light);
  }

  public void removeLight(Light light) {
    awayLights.remove(light);
  }

  @Override
  public void observe(Observable observable) {
    if (isAway) {
      if (observable instanceof Location) {
        Location toObserve = (Location) observable;

      }

      if (observable instanceof SHS) {
        if (autoLightsInAwayMode) {
          SHS shs = (SHS)observable;
          LocalTime simulationTime = shs.getSimulationTime().toLocalTime();
          if (simulationTime.isAfter(awayLightStart) && simulationTime.isBefore(awayLightEnd)) {
            for (Light light : awayLights) {
              try {
                light.doAction(Light.actionOn);
              } catch (DeviceException e) {
                //Do nothing
              }
            }
          } else {
            for (Light light : awayLights) {
              try {
                light.doAction(Light.actionOff);
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
