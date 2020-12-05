package models;

import models.modules.Clock;

public class ClockThread extends Thread {
  private volatile boolean running;
  private final Clock clock = Clock.getInstance();

  public void terminate() {
    running = false;
  }

  public void run() {
    running = true;
    long startTime = System.currentTimeMillis();
    long elapsedTime = System.currentTimeMillis() - startTime;
    long refreshRate = Clock.refreshRate;
    long timeTillNextDisplayChange = refreshRate - (elapsedTime % refreshRate);

    while (running) {
      try {
        Thread.sleep(timeTillNextDisplayChange);
        startTime = System.currentTimeMillis();
        Clock.advanceTime();
        elapsedTime = System.currentTimeMillis() - startTime;
        timeTillNextDisplayChange = refreshRate - elapsedTime;
      } catch (InterruptedException e) {
        running = false;
      }
    }
  }
}
