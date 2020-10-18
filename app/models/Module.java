package models;

/**
 * Template for all modules registered at [[models.SHS SHS]]. Contains common attributes.
 * ===Attributes===
 * `name (private [[java.lang.String String]]):` Unique identifier for the Module.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public abstract class Module {
  private String name;

  public Module(String name) {
    this.name = name;
  }

  /**
   * Get the Module name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the Module name.
   */
  public void setName(String name) {
    this.name = name;
  }
}
