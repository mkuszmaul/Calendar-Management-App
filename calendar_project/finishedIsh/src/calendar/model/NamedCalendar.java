package calendar.model;

import java.util.TimeZone;

/**
 * Represents a calendar with a name and timezone that manages events and event series.
 * This class extends the base Calendar class to add naming and timezone functionality.
 */
public class NamedCalendar extends Calendar implements INamedCalendar {
  private String name;
  private TimeZone timeZone;

  /**
   * Creates a new NamedCalendar with the given name and timezone.
   *
   * @param name     The name of the calendar
   * @param timeZone The timezone of the calendar
   */
  public NamedCalendar(String name, TimeZone timeZone) {
    super();
    this.name = name;
    this.timeZone = timeZone;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public TimeZone getTimeZone() {
    return timeZone;
  }

  @Override
  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
  }
}