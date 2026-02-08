package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;


/**
 * Represents a series of recurring events in the calendar.
 */
public class EventSeries {
  private final List<IEvent> events;
  private final Set<DayOfWeek> repeatDays;
  private final LocalDateTime seriesStartDate;
  private final int occurrences;
  private LocalDateTime seriesEndDate;

  /**
   * Creates a new event series with a specific number of occurrences.
   *
   * @param baseEvent   The template event for the series
   * @param repeatDays  The days of the week on which the event repeats
   * @param occurrences The number of times the event should repeat
   */
  public EventSeries(IEvent baseEvent, Set<DayOfWeek> repeatDays, int occurrences) {
    if (baseEvent == null || repeatDays == null || repeatDays.isEmpty()) {
      throw new IllegalArgumentException("Base event and repeat days cannot be null or empty");
    }
    if (occurrences <= 0) {
      throw new IllegalArgumentException("Occurrences must be positive");
    }

    this.events = new ArrayList<IEvent>();
    this.repeatDays = new HashSet<DayOfWeek>(repeatDays);
    this.occurrences = occurrences;
    this.seriesStartDate = baseEvent.getStartDateTime();

    generateEvents(baseEvent);
  }

  /**
   * Creates a new event series that repeats until a specific end date.
   *
   * @param baseEvent  The template event for the series
   * @param repeatDays The days of the week on which the event repeats
   * @param endDate    The date until which the events should be generated
   */
  public EventSeries(IEvent baseEvent, Set<DayOfWeek> repeatDays, LocalDateTime endDate) {
    if (baseEvent == null || repeatDays == null || repeatDays.isEmpty() || endDate == null) {
      throw new IllegalArgumentException("Base event, repeat days, and end date cannot be null");
    }
    if (endDate.isBefore(baseEvent.getStartDateTime())) {
      throw new IllegalArgumentException("End date cannot be before start date");
    }

    this.events = new ArrayList<IEvent>();
    this.repeatDays = new HashSet<DayOfWeek>(repeatDays);
    this.occurrences = -1;
    this.seriesStartDate = baseEvent.getStartDateTime();
    this.seriesEndDate = endDate;

    generateEvents(baseEvent);
  }

  private void generateEvents(IEvent baseEvent) {
    LocalDateTime currentDate = baseEvent.getStartDateTime();
    int count = 0;

    while ((occurrences == -1 && !currentDate.isAfter(seriesEndDate)) ||
          (occurrences > 0 && count < occurrences)) {

      if (repeatDays.contains(currentDate.getDayOfWeek())) {
        IEvent newEvent = Event.getBuilder()
              .subject(baseEvent.getSubject())
              .startDateTime(currentDate)
              .endDateTime(currentDate.plusHours(
                    baseEvent.getEndDateTime().getHour() - baseEvent.getStartDateTime().getHour()
              ))
              .description(baseEvent.getDescription())
              .location(baseEvent.getLocation())
              .isPublic(baseEvent.isPublic())
              .build();

        events.add(newEvent);
        count++;
      }

      currentDate = currentDate.plusDays(1);
    }

    //set the series end date for both occurrence-based and date-based series
    if (occurrences > 0) {
      IEvent lastEvent = events.get(events.size() - 1);
      seriesEndDate = lastEvent.getStartDateTime();
    } else {
      //for date-based series, we already have the end date set from constructor
      seriesEndDate = currentDate.minusDays(1);
    }
  }

  /**
   * Modifies events in the series starting from a specific event.
   *
   * @param startingEvent The event from which to start modifications
   * @param property      The property to modify
   * @param value         The new value
   */
  public void modifyEventsFromDate(IEvent startingEvent, String property, Object value) {
    List<IEvent> modifiedEvents = new ArrayList<IEvent>();
    for (IEvent event : events) {
      if (!event.getStartDateTime().isBefore(startingEvent.getStartDateTime())) {
        Event.EventBuilder builder = Event.getBuilder()
              .subject(event.getSubject())
              .startDateTime(event.getStartDateTime())
              .endDateTime(event.getEndDateTime())
              .description(event.getDescription())
              .location(event.getLocation())
              .isPublic(event.isPublic());

        updateEventBuilder(builder, property, value);
        modifiedEvents.add(builder.build());
      } else {
        modifiedEvents.add(event);
      }
    }
    events.clear();
    events.addAll(modifiedEvents);
  }

  /**
   * Modifies all events in the series.
   *
   * @param property The property to modify
   * @param value    The new value
   */
  public void modifyAllEvents(String property, Object value) {
    List<IEvent> modifiedEvents = new ArrayList<IEvent>();
    for (IEvent event : events) {
      Event.EventBuilder builder = Event.getBuilder()
            .subject(event.getSubject())
            .startDateTime(event.getStartDateTime())
            .endDateTime(event.getEndDateTime())
            .description(event.getDescription())
            .location(event.getLocation())
            .isPublic(event.isPublic());

      updateEventBuilder(builder, property, value);
      modifiedEvents.add(builder.build());
    }
    events.clear();
    events.addAll(modifiedEvents);
  }

  private void updateEventBuilder(Event.EventBuilder builder, String property, Object value) {
    switch (property.toLowerCase()) {
      case "subject":
        builder.subject((String) value);
        break;
      case "description":
        builder.description((String) value);
        break;
      case "location":
        builder.location((String) value);
        break;
      case "status":
        builder.isPublic(Boolean.parseBoolean((String) value));
        break;
      case "start":
        if (value instanceof LocalDateTime) {
          LocalDateTime newStartTime = (LocalDateTime) value;
          IEvent originalEvent = builder.build();
          int duration = originalEvent.getEndDateTime().getHour()
                - originalEvent.getStartDateTime().getHour();
          LocalDateTime newEventStart = originalEvent.getStartDateTime()
                .withHour(newStartTime.getHour());
          builder.startDateTime(newEventStart);
          builder.endDateTime(newEventStart.plusHours(duration));
        }
        break;
      case "end":
        if (value instanceof LocalDateTime) {
          LocalDateTime newEndTime = (LocalDateTime) value;
          IEvent originalEvent = builder.build();
          builder.endDateTime(originalEvent.getEndDateTime().withHour(newEndTime.getHour()));
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }

  /**
   * Gets all events in the series.
   *
   * @return Unmodifiable list of all events in the series
   */
  public List<IEvent> getEvents() {
    return Collections.unmodifiableList(events);
  }

  /**
   * Gets series start date.
   *
   * @return start date of series
   */
  public LocalDateTime getSeriesStartDate() {
    return seriesStartDate;
  }

  /**
   * Gets end start date.
   *
   * @return end date of series
   */
  public LocalDateTime getSeriesEndDate() {
    return seriesEndDate;
  }

  /**
   * Gets weekdays on which series repeats.
   *
   * @return repeat weekdays of series.
   */
  public Set<DayOfWeek> getRepeatDays() {
    return Collections.unmodifiableSet(repeatDays);
  }
}