package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * An interface for the model of the calendar app. Provides signatures for
 * methods required to run the inner workings of the app, such as creating and
 * editing events.
 */
public interface ICalendarModel {

  /**
   * Edits the subject of a single event.
   *
   * @param subject    (initial) subject of event to be modified
   * @param startDate  starting time of event to be modified
   * @param endDate    ending time of event to be modified
   * @param newSubject new subject for event to be modified to
   */
  public void editEventSubject(String subject, LocalDateTime startDate,
                               LocalDateTime endDate, String newSubject);

  /**
   * Edits the start time of a single event.
   *
   * @param subject      subject of event to be modified
   * @param startDate    (initial) starting time of event to be modified
   * @param endDate      ending time of event to be modified
   * @param newStartDate new start time for event to be modified to
   */
  public void editEventStart(String subject, LocalDateTime startDate,
                             LocalDateTime endDate, LocalDateTime newStartDate);

  /**
   * Edits the end time of a single event.
   *
   * @param subject    subject of event to be modified
   * @param startDate  starting time of event to be modified
   * @param endDate    (initial) ending time of event to be modified
   * @param newEndDate new end time for event to be modified to
   */
  public void editEventEnd(String subject, LocalDateTime startDate,
                           LocalDateTime endDate, LocalDateTime newEndDate);

  /**
   * Edits the description of a single event.
   *
   * @param subject        subject of event to be modified
   * @param startDate      starting time of event to be modified
   * @param endDate        ending time of event to be modified
   * @param newDescription new description for event to be modified to
   */
  public void editEventDescription(String subject, LocalDateTime startDate,
                                   LocalDateTime endDate, String newDescription);

  /**
   * Edits the location of a single event.
   *
   * @param subject     subject of event to be modified
   * @param startDate   starting time of event to be modified
   * @param endDate     ending time of event to be modified
   * @param newLocation new location for event to be modified to
   */
  public void editEventLocation(String subject, LocalDateTime startDate,
                                LocalDateTime endDate, String newLocation);

  public void editEventStatus(String subject, LocalDateTime startDate,
                              LocalDateTime endDate, String status);


  //NOTE FOR EDITING EVENT SERIES: if given event exists but is not part of
  //series, edit just that event

  /**
   * Edits the subject of an event series.
   *
   * @param subject         (initial) subject of series to be modified
   * @param startDate       starting time of an event in series to be modified
   * @param newSubject      new subject for series to be modified to
   * @param editWholeSeries true if entire series should be modified, false if only
   *                        events starting at or after the given time should be modified.
   */
  public void editSeriesSubject(String subject, LocalDateTime startDate,
                                String newSubject, boolean editWholeSeries);

  /**
   * Edits the start time of an event series.
   *
   * @param subject         subject of series to be modified
   * @param startDate       (initial) starting time of an event in series to be modified
   * @param newStartDate    new start time for series to be modified to
   * @param editWholeSeries true if entire series should be modified, false if only
   *                        events starting at or after the given time should be modified.
   */
  public void editSeriesStart(String subject, LocalDateTime startDate,
                              LocalDateTime newStartDate, boolean editWholeSeries);

  /**
   * Edits the end time of an event series.
   *
   * @param subject         subject of series to be modified
   * @param startDate       starting time of an event in series to be modified
   * @param newEndDate      new end time for series to be modified to
   * @param editWholeSeries true if entire series should be modified, false if only
   *                        events starting at or after the given time should be modified.
   */
  public void editSeriesEnd(String subject, LocalDateTime startDate,
                            LocalDateTime newEndDate, boolean editWholeSeries);

  /**
   * Edits the end time of an event series.
   *
   * @param subject         subject of series to be modified
   * @param startDate       starting time of an event in series to be modified
   * @param newDescription  new description for series to be modified to
   * @param editWholeSeries true if entire series should be modified, false if only
   *                        events starting at or after the given time should be modified.
   */
  public void editSeriesDescription(String subject, LocalDateTime startDate,
                                    String newDescription, boolean editWholeSeries);

  /**
   * Edits the end time of an event series.
   *
   * @param subject         subject of series to be modified
   * @param startDate       starting time of an event in series to be modified
   * @param newLocation     new location for series to be modified to
   * @param editWholeSeries true if entire series should be modified, false if only
   *                        events starting at or after the given time should be modified.
   */
  public void editSeriesLocation(String subject, LocalDateTime startDate,
                                 String newLocation, boolean editWholeSeries);


  /**
   * Edits the end time of an event series.
   *
   * @param subject         subject of series to be modified
   * @param startDate       starting time of an event in series to be modified
   * @param newStatus       new status for series to be modified to ("true" for public status,
   *                        "false" for private status).
   * @param editWholeSeries true if entire series should be modified, false if only
   *                        events starting at or after the given time should be modified.
   */
  public void editSeriesStatus(String subject, LocalDateTime startDate,
                               String newStatus, boolean editWholeSeries);

  /**
   * Adds given event to calendar.
   *
   * @param event event to be added
   */
  public void addEvent(IEvent event);

  /**
   * Makes (and adds to calendar) an event series with the given
   * subject, on the given days of the week, at the given times.
   * The event series should repeat n times.
   *
   * @param subject    event subject
   * @param daysOfWeek list of weekdays on which event should repeat
   * @param start      start time for event
   * @param end        end time for event
   * @param n          number of times event should repeat
   */
  public void makeEventSeriesNTimes(String subject,
                                    ArrayList<DayOfWeek> daysOfWeek,
                                    LocalDateTime start, LocalDateTime end,
                                    int n);

  /**
   * Makes (and adds to calendar) an event series with the given
   * subject, on the given days of the week, at the given times.
   * The event series should repeat until the given date (inclusive).
   *
   * @param subject    event subject
   * @param daysOfWeek list of weekdays on which event should repeat
   * @param start      start time for event
   * @param end        end time for event
   * @param endDate    event series repeats until this date (inclusive).
   */
  public void makeEventSeriesUntilDate(String subject,
                                       ArrayList<DayOfWeek> daysOfWeek,
                                       LocalDateTime start, LocalDateTime end,
                                       LocalDateTime endDate);

  /**
   * Returns all events on the calendar between the given start and
   * end times (inclusive).
   *
   * @param start start time - return any events occurring between this and end
   * @param end   end time - return any events occurring between this and start
   * @return ArrayList of events occurring between given times
   */
  public ArrayList<IEvent> getEvents(LocalDateTime start, LocalDateTime end);

  /**
   * Returns true if there is an event occurring at the given time.
   *
   * @param time time at which an event may be occurring
   * @return true iff an event is occurring at this time.
   */
  public boolean isEventAt(LocalDateTime time);
}
