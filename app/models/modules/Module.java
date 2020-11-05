package models.modules;

import models.Observable;
import models.Observer;

import java.util.Set;
import java.util.HashSet;

/**
 * Template for all modules registered at [[models.modules.SHS SHS]]. Contains common attributes.
 * ===Attributes===
 * `name (private [[java.lang.String String]]):` Unique identifier for the Module.
 *
 * @version 1
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 */
public abstract class Module implements Observable {
  private String name;
  private final Set<Observer> observers = new HashSet<>();

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

  @Override
  public void addObserver(Observer observer) {
    this.observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    this.observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.observe(this);
    }
  }
}
