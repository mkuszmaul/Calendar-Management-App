package calendar.model;

import java.time.LocalDateTime;

/**
 * An interface for events. Represents a single event on the calendar, with a subject,
 * start date, end date, and possibly more.
 */
public interface IEvent {

  /**
   * Getter for this event's subject.
   *
   * @return subject as string.
   */
  public String getSubject();

  /**
   * Getter for this event's start time.
   *
   * @return start time as LocalDateTime
   */
  public LocalDateTime getStartDateTime();

  /**
   * Getter for this event's end time.
   *
   * @return end time as LocalDateTime
   */
  public LocalDateTime getEndDateTime();

  /**
   * Getter for this event's description (null if description null).
   *
   * @return description as String.
   */
  public String getDescription();

  /**
   * Getter for this event's location (null if location null).
   *
   * @return location as String.
   */
  public String getLocation();

  /**
   * Getter for this event's status as public or not.
   *
   * @return true if event is public.
   */
  public boolean isPublic();
}
