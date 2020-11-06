package models.modules;

import models.User;
import models.devices.Device;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {
  private final List<Message> messageList = new ArrayList<>();
  private FileWriter fileWriter;

  public enum MessageType {
    normal,
    success,
    warning,
    danger
  }

  public static class Message {
    public final String timeStamp;
    public final String simulationTime;
    public final String actor;
    public final String message;
    public final MessageType type;

    private Message(String actor, String message, MessageType type) {
      this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
      this.simulationTime = SHS.getInstance().getTimeString();
      this.actor = actor;
      this.message = message;
      this.type = type;
    }

    @Override
    public String toString() {
      return timeStamp +  " [" + simulationTime + "] " + actor + " : " + message;
    }

    public String toHTML() {
      return  "<p class=\"text-" + type + "\">" + toString() + "</p>";
    }
  }

  Logger() {
    try {
      fileWriter = new FileWriter("target/logFile.txt",false);
    } catch (IOException e) {
      System.exit(-1);
    }
  }

  public void log(String message, MessageType type) {
    Message toLog = new Message("User", message, type);
    messageList.add(toLog);
    try {
      fileWriter.write(toLog.toString() + '\n');
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void log(User actor, String message, MessageType type) {
    Message toLog = new Message(actor.getName() + " (" + actor.getType() + ')', message, type);
    messageList.add(toLog);
    try {
      fileWriter.write(toLog.toString() + '\n');
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void log(Module actor, String message, MessageType type) {
    Message toLog = new Message("User", message, type);
    messageList.add(toLog);
    try {
      fileWriter.write(toLog.toString() + '\n');
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void log(Device actor, String message, MessageType type) {
    Message toLog = new Message(actor.getName() + " (" + actor.getLocation().getName() + ')', message, type);
    messageList.add(toLog);
    try {
      fileWriter.write(toLog.toString() + '\n');
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for(Message message : messageList) {
      stringBuilder.append(message.toString());
      stringBuilder.append('\n');
    }
    return stringBuilder.toString();
  }

  public String toHtml() {
    StringBuilder stringBuilder = new StringBuilder();
    for(Message message : messageList) {
      stringBuilder.append(message.toHTML());
    }
    return stringBuilder.toString();
  }
}
