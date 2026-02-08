package calendar.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Represents all calendars on a given CalendarApp. Implementations
 * should have some way of storing the "current" calendar being edited,
 * and have ICalendarModel's methods be executed on the current
 * calendar. For example, if an implementation receives "useCalendar("CoolCalendar")"
 * and then "getEvents(date1, date2)", the second method call
 * should return all of the events from date1 to date2 on CoolCalendar.
 */
public interface ICalendarsModel extends ICalendarModel {
  /**
   * Create and add to the app a calendar with the given name
   * and timezone. Throws error if calendar name is non-unique.
   *
   * @param name     name of new calendar
   * @param timeZone timezone of new calendar
   */
  public void createCalendar(String name, TimeZone timeZone) throws Exception;

  /**
   * Edit the given property of a calendar with a given name
   * to the new property's value. Throws error if no such calendar
   * exists.
   *
   * @param name             name of calendar to be changed
   * @param property         property to be changed: one of NAME or TIMEZONE
   * @param newPropertyValue desired new value of property
   */
  public void editCalendar(String name, CalendarProperty property,
                    Object newPropertyValue) throws Exception;

  /**
   * Sets the current calendar to the calendar with the given name. Throws
   * error if no such calendar exists.
   *
   * @param name name of calendar
   */
  public void useCalendar(String name) throws Exception;

  /**
   * Copy a specific event with the given name and start date/time from the current
   * calendar to the target calendar to start at the specified date/time. The "to" date/time
   * is assumed to be specified in the timezone of the target calendar.
   * Throw error if no such event or target calendar exists, or if the event cannot be added
   * to the target calendar for duplication reasons.
   *
   * @param eventName          name of event to be copied
   * @param originalStartTime  start time of event in the current calendar
   * @param targetCalendarName name of the target calendar
   * @param toStartTime        start time of event in the target calendar
   */
  public void copyEventCalendar(String eventName, LocalDateTime originalStartTime,
                         String targetCalendarName, LocalDateTime toStartTime)
        throws Exception;

  /**
   * Same behavior as copyEventCalendar, except that it copies all events scheduled
   * on a given day. The times remain the same, except they are converted to the timezone
   * of the target calendar (e.g. an event that starts at 2pm in the source calendar which is
   * in EST would start at 11am in the destination calendar which is in PST).
   * Throws error with invalid arguments.
   *
   * @param startDate          date on current calendar from which events should be copied
   * @param targetCalendarName name of target calendar
   * @param toDate             date to copy events to
   */
  public void copyEventsOnDay(LocalDate startDate, String targetCalendarName, LocalDate toDate)
        throws Exception;

  /**
   * The command has the same behavior as the other copy commands, except it copies all events
   * scheduled in the specified date interval. The date string in the target calendar corresponds
   * to the start of the interval. The endpoint dates of the interval are inclusive.
   * In both the copy events commands, if an event series partly overlaps with the specified
   * range, only those events in the series that overlap with the specified range should be copied,
   * and their status as part of a series should be retained in the destination calendar.
   * Throws error given invalid arguments.
   *
   * @param startDate          start date from which to copy from
   * @param endDate            end date from which to copy from
   * @param targetCalendarName name of target calendar
   * @param toStartDate        start date from which to copy to
   */
  public void copyEventsBetween(LocalDate startDate, LocalDate endDate, String targetCalendarName,
                         LocalDate toStartDate) throws Exception;
}
