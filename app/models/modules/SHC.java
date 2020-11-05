package models.modules;

import javax.inject.Singleton;

/**
 * Smart Home Core functionality module, it handles Device functionality and access. It is a Singleton Class.
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
public class SHC extends Module {
  private static final SHC instance = new SHC("SHC");

  /**
   * Get the Singleton Class instance for SHS.
   */
  public static SHC getInstance(){
    return instance;
  }

  private SHC(String name) {
    super(name);
  }
}
