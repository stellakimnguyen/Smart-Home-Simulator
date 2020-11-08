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
    long timeTillNextDisplayChange = 500 - (elapsedTime % 500);

    while (running) {
      try {
        Thread.sleep(timeTillNextDisplayChange);
        startTime = System.currentTimeMillis();
        clock.setTime(clock.getTime().plusNanos(clock.getTimeMultiplier() * 500000000L));
        elapsedTime = System.currentTimeMillis() - startTime;
        timeTillNextDisplayChange = 500 - elapsedTime;
      } catch (InterruptedException e) {
        running = false;
      }
    }
  }
}
