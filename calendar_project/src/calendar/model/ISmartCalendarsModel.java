package calendar.model;

import java.util.List;

/**
 * Represents a multiple-calendars model for a calendar app with additional helper
 * method(s) to support a GUI version of the calendar app.
 */
public interface ISmartCalendarsModel extends ICalendarsModel {
  /**
   * Return list of names of all stored calendars.
   * @return list of calendar names
   */
  public List<String> getCalendarNames();
}
