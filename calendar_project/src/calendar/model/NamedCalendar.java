package calendar.model;

import java.util.TimeZone;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

  /**
   * Converts a LocalDateTime from one timezone to another.
   * @param dateTime the time to convert
   * @param fromZone the source timezone
   * @param toZone the target timezone
   * @return the converted LocalDateTime
   */
  private LocalDateTime convertTimeZone(LocalDateTime dateTime, TimeZone fromZone,
                                        TimeZone toZone) {
    java.time.ZonedDateTime zonedDateTime = dateTime.atZone(fromZone.toZoneId());
    java.time.ZonedDateTime convertedDateTime =
          zonedDateTime.withZoneSameInstant(toZone.toZoneId());
    return convertedDateTime.toLocalDateTime();
  }

  /**
   * Sets the timezone of this calendar. All events will be adjusted to the new timezone.
   * @param timeZone the new timezone
   */
  public void setTimeZone(TimeZone timeZone) {
    TimeZone oldTimeZone = this.timeZone;
    this.timeZone = timeZone;

    List<IEvent> eventsCopy = new ArrayList<>(getAllEvents());

    for (IEvent event : eventsCopy) {
      removeEvent(event);
    }
    for (IEvent event : eventsCopy) {
      LocalDateTime adjustedStart = convertTimeZone(event.getStartDateTime(), oldTimeZone,
            timeZone);
      LocalDateTime adjustedEnd = convertTimeZone(event.getEndDateTime(), oldTimeZone, timeZone);

      IEvent adjustedEvent = Event.getBuilder()
            .subject(event.getSubject())
            .startDateTime(adjustedStart)
            .endDateTime(adjustedEnd)
            .description(event.getDescription())
            .location(event.getLocation())
            .isPublic(event.isPublic())
            .build();

      addEvent(adjustedEvent);
    }
  }
}