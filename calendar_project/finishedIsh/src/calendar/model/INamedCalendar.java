package calendar.model;

import java.util.TimeZone;

public interface INamedCalendar extends ICalendarModel {
  /**
   * Gets the calendar name.
   *
   * @return The calendar name
   */
  public String getName();

  /**
   * Sets the calendar name.
   *
   * @param name The new calendar name
   */
  public void setName(String name);

  /**
   * Gets the calendar timezone.
   *
   * @return The calendar timezone
   */
  public TimeZone getTimeZone();

  /**
   * Sets the calendar timezone.
   *
   * @param timeZone The new calendar timezone
   */
  public void setTimeZone(TimeZone timeZone);
}
