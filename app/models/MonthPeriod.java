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
}
