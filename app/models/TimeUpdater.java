package models;

import models.modules.SHS;

public class TimeUpdater extends Thread {
  private volatile boolean running;

  public void terminate() {
    running = false;
  }

  public void runOld() {
    running = true;
    long startTime = System.currentTimeMillis();
    SHS shs = SHS.getInstance();
    long elapsedTime = System.currentTimeMillis() - startTime;
    long timeTillNextDisplayChange = 1000 - (elapsedTime % 1000);
    System.out.println("\ntimeTillNext: " + timeTillNextDisplayChange);

    while (running) {
      try {
        Thread.sleep(timeTillNextDisplayChange);
      } catch (InterruptedException e) {
        running = false;
      }
      startTime = System.currentTimeMillis();
      System.out.println("\nold simulationTime: " + shs.getSimulationTime());
      shs.setSimulationTime(shs.getSimulationTime().plusSeconds(shs.getTimeMultiplier()));
      System.out.println("new simulationTime: " + shs.getSimulationTime());
      elapsedTime = System.currentTimeMillis() - startTime;
      timeTillNextDisplayChange = 1000 - elapsedTime;
    }
  }

  public void run() {
    running = true;
    long startTime = System.currentTimeMillis();
    SHS shs = SHS.getInstance();
    long elapsedTime = System.currentTimeMillis() - startTime;
    long timeTillNextDisplayChange = 500 - (elapsedTime % 500);

    while (running) {
      try {
        Thread.sleep(timeTillNextDisplayChange);
      } catch (InterruptedException e) {
        running = false;
      }
      startTime = System.currentTimeMillis();
      shs.setSimulationTime(shs.getSimulationTime().plusNanos(shs.getTimeMultiplier() * 500000000L));
      elapsedTime = System.currentTimeMillis() - startTime;
      timeTillNextDisplayChange = 500 - elapsedTime;
    }
  }

}
