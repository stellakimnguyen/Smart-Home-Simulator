package models;

/**
 * Custom Observer Interface for the Observer Design Pattern.
 *
 * @version 2
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public interface Observer {
  /**
   * Method to be executed when the [[models.Observable Observable]] objects notifies this [[models.Observer Observer]]
   * @param observable the object to be observed.
   */
  void observe(Observable observable);

  /**
   * Get the Observer name.
   */
  String getName();
}
