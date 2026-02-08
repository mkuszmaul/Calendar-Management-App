package calendar.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a calendar event with required and optional properties.
 */
public class Event implements IEvent {
  private final String subject;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final String description;
  private final String location;
  private final boolean isPublic;
  private final boolean isAllDay;

  private Event(String subject, LocalDateTime startDateTime,
                LocalDateTime endDateTime, String description,
                String location, boolean isPublic) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
    this.isAllDay = (endDateTime == null);
  }

  /**
   * Create and returns a new builder for an Event.
   *
   * @return a new EventBuilder instance.
   */
  public static EventBuilder getBuilder() {
    return new EventBuilder();
  }

  @Override
  public String toString() {
    String eventString = "";
    eventString += this.subject;
    if (this.description != null) {
      eventString += (": " + this.description);
    }
    if (this.location != null) {
      eventString += (" at " + this.location);
    }
    if (this.isPublic) {
      eventString += (" (public)");
    }
    eventString += (" from " + this.getStartDateTime()
          + " to " + this.getEndDateTime() + ".");
    return eventString;
  }

  /**
   * Gets the subject or title of the event.
   *
   * @return the event subject.
   */
  @Override
  public String getSubject() {
    return subject;
  }

  /**
   * Gets the start date and time of the event.
   *
   * @return the start time.
   */
  @Override
  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  /**
   * Gets the end date and time of the event.
   *
   * @return the end time.
   */
  @Override
  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  /**
   * Gets the event description, if available.
   *
   * @return the description or null.
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Gets the event location, if available.
   *
   * @return the location or null.
   */
  @Override
  public String getLocation() {
    return location;
  }

  /**
   * Checks if the event is marked as public.
   *
   * @return true if public, false if private.
   */
  @Override
  public boolean isPublic() {
    return isPublic;
  }

  /**
   * Checks if the event is an all-day event.
   *
   * @return true if all-day, false otherwise.
   */
  public boolean isAllDay() {
    return isAllDay;
  }

  /**
   * Checks if this event equals another event by subject, start/end time.
   *
   * @param o the object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Event)) {
      return false;
    }
    Event event = (Event) o;
    return Objects.equals(subject, event.subject) &&
          Objects.equals(startDateTime, event.startDateTime) &&
          Objects.equals(endDateTime, event.endDateTime);
  }

  /**
   * Returns a hash code based on subject, start, and end time.
   *
   * @return the hash code.
   */
  @Override
  public int hashCode() {
    return Objects.hash(subject, startDateTime, endDateTime);
  }

  /**
   * Builder class as a static inner class.
   */
  public static class EventBuilder implements IEventBuilder {
    private String subject;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private String location;
    private boolean isPublic;

    private EventBuilder() {
      this.description = null;
      this.location = null;
      this.isPublic = true;
    }

    /**
     * Sets the subject of the event.
     *
     * @param subject the title or name
     * @return this builder
     */
    public EventBuilder subject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets the start date and time of the event.
     *
     * @param startDateTime the event start
     * @return this builder
     */
    public EventBuilder startDateTime(LocalDateTime startDateTime) {
      this.startDateTime = startDateTime;
      return this;
    }

    /**
     * Sets the end date and time of the event.
     *
     * @param endDateTime the event end
     * @return this builder
     */
    public EventBuilder endDateTime(LocalDateTime endDateTime) {
      this.endDateTime = endDateTime;
      return this;
    }

    /**
     * Sets the event description.
     *
     * @param description the event details
     * @return this builder
     */
    public EventBuilder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the event location.
     *
     * @param location the event location
     * @return this builder
     */
    public EventBuilder location(String location) {
      this.location = location;
      return this;
    }

    /**
     * Sets whether the event is public.
     *
     * @param isPublic true if public
     * @return this builder
     */
    public EventBuilder isPublic(boolean isPublic) {
      this.isPublic = isPublic;
      return this;
    }

    /**
     * Builds and returns the Event object.
     *
     * @return the created Event
     * @throws IllegalStateException if required fields are missing
     */
    public IEvent build() {
      // Validate required fields
      if (subject == null || subject.trim().isEmpty()) {
        throw new IllegalStateException("Subject is required");
      }
      if (startDateTime == null) {
        throw new IllegalStateException("Start date/time is required");
      }

      //all-day events
      if (endDateTime == null) {
        endDateTime = startDateTime.withHour(17).withMinute(0); // 5 PM
        startDateTime = startDateTime.withHour(8).withMinute(0); // 8 AM
      } else if (endDateTime.isBefore(startDateTime)) {
        throw new IllegalStateException("End date/time cannot be before start date/time");
      }

      return new Event(subject, startDateTime, endDateTime,
            description, location, isPublic);
    }
  }
}

