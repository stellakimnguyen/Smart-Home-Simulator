package models;

import java.time.LocalDateTime;

public class MonthPeriod {
  private int start = 1;
  private int end = 12;

  public MonthPeriod() {}
  public MonthPeriod(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    if (start >= 1 && start <= 12) {
      this.start = start;
    }
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    if (end >= 1 && end <= 12) {
      this.end = end;
    }
  }

  public boolean isInPeriod(LocalDateTime date) {
    int time = date.getMonthValue();
    return isInPeriod(time);
  }
  public boolean isInPeriod(int time) {
    return ((end<start) && ((time<=end) || (time>=start)))
            || ((start<end) && ((time<=end) && (time>=start)));
  }

  public boolean isOverlapping(MonthPeriod o) {
    if (o == null) {
      return false;
    }
    return (isInPeriod(o.start) || isInPeriod(o.end));
  }

  public static String getMonth(int month) {
    switch (month) {
      case 1:
        return "January";
      case 2:
        return "February";
      case 3:
        return "March";
      case 4:
        return "April";
      case 5:
        return "May";
      case 6:
        return "June";
      case 7:
        return "July";
      case 8:
        return "August";
      case 9:
        return "September";
      case 10:
        return "October";
      case 11:
        return "November";
      default:
        return "December";
    }
  }

  @Override
  public String toString() {
    return getMonth(start) + " to " + getMonth(end);
  }

  public boolean equals(MonthPeriod o) {
    if (o == null) {
      return false;
    }
    return ((start == o.start) && (end == o.end));
  }
}
