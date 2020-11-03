package models;

/**
 * Custom Observable Interface for the Observer Design Pattern.
 *
 * @version 2
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public interface Observable{
  /**
   * Register an [[models.Observer Observer]] in this [[models.Observable Observable]] object
   * @param observer the [[models.Observer Observer]] to register to be notified upon changes.
   */
  void addObserver(Observer observer);
  /**
   * Remove an [[models.Observer Observer]] from the registered [[models.Observer Observers]] in this [[models.Observable Observable]] object
   * @param observer the [[models.Observer Observer]] to be removed.
   */
  void removeObserver(Observer observer);
  /**
   * Method to notify all [[models.Observer Observers]] of a change
   */
  void notifyObservers();
}
