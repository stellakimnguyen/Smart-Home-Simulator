package models.modules;

import models.ClockThread;
import models.Observable;
import models.Observer;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class Clock implements Observable {
  private static final Clock instance = new Clock();

  private ClockThread thread;
  private final Set<Observer> observers = new HashSet<>();
  private LocalDateTime time;
  private int timeMultiplier;

  private Clock() {
    time = LocalDateTime.now().withNano(0);
    timeMultiplier = 1;
  }

  public static Clock getInstance() {
    return instance;
  }

  public void startClock() {
    if (thread == null) {
      thread = new ClockThread();
      thread.start();
    }
  }

  public void stopClock() {
    if (thread != null) {
      thread.terminate();
      thread = null;
    }
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
    notifyObservers();
  }

  public int getTimeMultiplier() {
    return timeMultiplier;
  }

  public void setTimeMultiplier(int timeMultiplier) {
    this.timeMultiplier = timeMultiplier;
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

  @Override
  public String getName() {
    return "Clock";
  }
}
