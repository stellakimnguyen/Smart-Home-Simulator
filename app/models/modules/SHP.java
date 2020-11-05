package models.modules;

import models.Location;
import models.Observable;
import models.Observer;

import javax.inject.Singleton;
import java.time.LocalDateTime;

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
public class SHP implements Observer {
  private boolean isAway;
  private LocalDateTime invasionTime;
  private boolean isUnderInvasion;
  private int timeBeforeAuthorities;

  private static final SHP instance = new SHP();

  /**
   * Get the Singleton Class instance for SHP.
   */
  public static SHP getInstance(){
    return instance;
  }

  private SHP() {
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

  @Override
  public void observe(Observable observable) {
    if (isAway) {
      if (observable instanceof Location) {
        Location toObserve = (Location) observable;

      }
    }
  }
}
