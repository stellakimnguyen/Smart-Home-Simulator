package models;

import javax.inject.Singleton;

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
public class SHP implements Observer{

  @Override
  public void observe(Observable observable) {
    //TODO
  }
}
