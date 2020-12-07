package models;

import java.time.LocalTime;

public class TimePeriod {
  private LocalTime start = LocalTime.of(0,0,0);
  private LocalTime end = LocalTime.of(23,59,59);

  public LocalTime getStart() {
    return start;
  }

  public void setStart(LocalTime start) {
    this.start = start;
  }

  public LocalTime getEnd() {
    return end;
  }

  public void setEnd(LocalTime end) {
    this.end = end;
  }

  public boolean isInPeriod(LocalTime time) {
    return ((end.isBefore(start)) && ((time.isBefore(end)) || (time.isAfter(start))))
            || ((start.isBefore(end)) && ((time.isBefore(end)) && (time.isAfter(start))));
  }
}
