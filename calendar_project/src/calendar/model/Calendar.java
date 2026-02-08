package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents a calendar that manages events and event series.
 */
public class Calendar implements ICalendarModel {
  private final List<IEvent> events;
  private final List<EventSeries> eventSeries;

  public Calendar() {
    this.events = new ArrayList<IEvent>();
    this.eventSeries = new ArrayList<EventSeries>();
  }

  @Override
  public void editEventSubject(String subject, LocalDateTime startDate,
                               LocalDateTime endDate, String newSubject) {
    IEvent event = findEvent(subject, startDate, endDate);
    modifyEvent(event, "subject", newSubject);
  }

  @Override
  public void editEventStart(String subject, LocalDateTime startDate,
                             LocalDateTime endDate, LocalDateTime newStartDate) {
    IEvent event = findEvent(subject, startDate, endDate);
    modifyEvent(event, "start", newStartDate);
  }

  @Override
  public void editEventEnd(String subject, LocalDateTime startDate,
                           LocalDateTime endDate, LocalDateTime newEndDate) {
    IEvent event = findEvent(subject, startDate, endDate);
    modifyEvent(event, "end", newEndDate);
  }

  @Override
  public void editEventDescription(String subject, LocalDateTime startDate,
                                   LocalDateTime endDate, String newDescription) {
    IEvent event = findEvent(subject, startDate, endDate);
    modifyEvent(event, "description", newDescription);
  }

  @Override
  public void editEventLocation(String subject, LocalDateTime startDate,
                                LocalDateTime endDate, String newLocation) {
    IEvent event = findEvent(subject, startDate, endDate);
    modifyEvent(event, "location", newLocation);
  }

  @Override
  public void editEventStatus(String subject, LocalDateTime startDate,
                              LocalDateTime endDate, String status) {
    IEvent event = findEvent(subject, startDate, endDate);
    modifyEvent(event, "status", status);
  }

  /**
   * Helper method to edit a property for all events in a series or from a specific date forward.
   *
   * @param subject The subject of the series to edit
   * @param startDate The start date of the event in the series
   * @param property The property to modify ("subject", "description", etc.)
   * @param value The new value for the property
   * @param editWholeSeries Whether to edit all events or just from startDate forward
   */
  private void editSeriesProperty(String subject, LocalDateTime startDate,
                                  Object value, String property, boolean editWholeSeries) {
    EventSeries series = findEventSeries(subject, startDate);
    if (series != null) {
      if (editWholeSeries) {
        //first remove all events from this series
        List<IEvent> seriesEvents = series.getEvents();
        events.removeAll(seriesEvents);

        //modify the series events
        series.modifyAllEvents(property, value);

        //add back the modified events
        events.addAll(series.getEvents());
      } else {
        //get all events from this series
        List<IEvent> seriesEvents = series.getEvents();

        IEvent startingEvent = null;
        for (IEvent event : seriesEvents) {
          if (event.getStartDateTime().equals(startDate)) {
            startingEvent = event;
            break;
          }
        }

        if (startingEvent != null) {
          for (IEvent event : new ArrayList<>(events)) {
            if (seriesEvents.contains(event) &&
                  !event.getStartDateTime().isBefore(startDate)) {
              events.remove(event);
            }
          }

          series.modifyEventsFromDate(startingEvent, property, value);

          for (IEvent event : series.getEvents()) {
            if (!event.getStartDateTime().isBefore(startDate)) {
              events.add(event);
            }
          }
        }
      }
    }
  }

  @Override
  public void editSeriesSubject(String subject, LocalDateTime startDate,
                                String newSubject, boolean editWholeSeries) {
    editSeriesProperty(subject, startDate, newSubject, "subject", editWholeSeries);
  }

  @Override
  public void editSeriesStart(String subject, LocalDateTime startDate,
                              LocalDateTime newStartDate, boolean editWholeSeries) {
    editSeriesProperty(subject, startDate, newStartDate, "start", editWholeSeries);
  }

  @Override
  public void editSeriesEnd(String subject, LocalDateTime startDate,
                            LocalDateTime newEndDate, boolean editWholeSeries) {
    editSeriesProperty(subject, startDate, newEndDate, "end", editWholeSeries);
  }

  @Override
  public void editSeriesDescription(String subject, LocalDateTime startDate,
                                    String newDescription, boolean editWholeSeries) {
    editSeriesProperty(subject, startDate, newDescription, "description", editWholeSeries);
  }

  @Override
  public void editSeriesLocation(String subject, LocalDateTime startDate,
                                 String newLocation, boolean editWholeSeries) {
    editSeriesProperty(subject, startDate, newLocation, "location", editWholeSeries);
  }

  @Override
  public void editSeriesStatus(String subject, LocalDateTime startDate,
                               String newStatus, boolean editWholeSeries) {
    editSeriesProperty(subject, startDate, newStatus, "status", editWholeSeries);
  }

  @Override
  public void addEvent(IEvent event) {
    if (event instanceof Event) {
      Event concreteEvent = (Event) event;
      if (!canAddEvent(concreteEvent)) {
        throw new IllegalArgumentException("An event with the same details already exists");
      }
      events.add(concreteEvent);
    }
  }

  @Override
  public void makeEventSeriesNTimes(String subject, ArrayList<DayOfWeek> daysOfWeek,
                                    LocalDateTime start, LocalDateTime end, int n) {
    IEvent baseEvent = Event.getBuilder()
          .subject(subject)
          .startDateTime(start)
          .endDateTime(end)
          .build();

    EventSeries series = new EventSeries(baseEvent, new HashSet<>(daysOfWeek), n);

    for (IEvent event : series.getEvents()) {
      if (!canAddEvent(event)) {
        throw new IllegalArgumentException("Event series conflicts with existing events");
      }
    }

    eventSeries.add(series);
    events.addAll(series.getEvents());
  }

  @Override
  public void makeEventSeriesUntilDate(String subject, ArrayList<DayOfWeek> daysOfWeek,
                                       LocalDateTime start, LocalDateTime end,
                                       LocalDateTime endDate) {
    IEvent baseEvent = Event.getBuilder()
          .subject(subject)
          .startDateTime(start)
          .endDateTime(end)
          .build();

    EventSeries series = new EventSeries(baseEvent, new HashSet<>(daysOfWeek), endDate);

    for (IEvent event : series.getEvents()) {
      if (!canAddEvent(event)) {
        throw new IllegalArgumentException("Event series conflicts with existing events");
      }
    }

    eventSeries.add(series);
    events.addAll(series.getEvents());
  }

  @Override
  public ArrayList<IEvent> getEvents(LocalDateTime start, LocalDateTime end) {
    ArrayList<IEvent> result = new ArrayList<IEvent>();
    for (IEvent e : events) {
      if (!e.getStartDateTime().isAfter(end) && !e.getEndDateTime().isBefore(start)) {
        result.add(e);
      }
    }
    Collections.sort(result, new Comparator<IEvent>() {
      @Override
      public int compare(IEvent e1, IEvent e2) {
        return e1.getStartDateTime().compareTo(e2.getStartDateTime());
      }
    });
    return result;
  }

  @Override
  public boolean isEventAt(LocalDateTime time) {
    return isBusy(time);
  }

  // Helper methods
  private EventSeries findEventSeries(String subject, LocalDateTime startDate) {
    for (EventSeries series : eventSeries) {
      for (IEvent event : series.getEvents()) {
        if (event.getSubject().equals(subject) &&
              event.getStartDateTime().equals(startDate)) {
          return series;
        }
      }
    }
    return null;
  }

  private LocalDateTime getEventEndTime(EventSeries series, LocalDateTime startDate) {
    for (IEvent event : series.getEvents()) {
      if (event.getStartDateTime().equals(startDate)) {
        return event.getEndDateTime();
      }
    }
    return null;
  }

  private boolean canAddEvent(IEvent event) {
    for (IEvent e : events) {
      if (e.getSubject().equals(event.getSubject()) &&
            e.getStartDateTime().equals(event.getStartDateTime()) &&
            e.getEndDateTime().equals(event.getEndDateTime())) {
        return false;
      }
    }
    return true;
  }

  private IEvent modifyEvent(IEvent oldEvent, String property, Object value) {
    if (!events.contains(oldEvent)) {
      throw new IllegalArgumentException("Event not found in calendar");
    }

    Event.EventBuilder builder = Event.getBuilder()
          .subject(oldEvent.getSubject())
          .startDateTime(oldEvent.getStartDateTime())
          .endDateTime(oldEvent.getEndDateTime())
          .description(oldEvent.getDescription())
          .location(oldEvent.getLocation())
          .isPublic(oldEvent.isPublic());

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
          builder.startDateTime((LocalDateTime) value);
        }
        break;
      case "end":
        if (value instanceof LocalDateTime) {
          builder.endDateTime((LocalDateTime) value);
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }

    IEvent newEvent = builder.build();
    events.remove(oldEvent);
    events.add(newEvent);
    return newEvent;
  }

  private IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end) {
    for (IEvent e : events) {
      if (e.getSubject().equals(subject) &&
            e.getStartDateTime().equals(start) &&
            e.getEndDateTime().equals(end)) {
        return e;
      }
    }
    return null;
  }

  private boolean isSameDate(LocalDateTime date1, LocalDateTime date2) {
    return date1.toLocalDate().equals(date2.toLocalDate());
  }

  /**
   * Returns unmodifiable list of all events in calendar.
   * @return list of all events
   */
  public List<IEvent> getAllEvents() {
    return Collections.unmodifiableList(events);
  }

  /**
   * Returns unmodifiable list of all event series in calendar.
   * @return list of all event series
   */
  public List<EventSeries> getAllEventSeries() {
    return Collections.unmodifiableList(eventSeries);
  }

  /**
   * Gets all events on a specific date.
   *
   * @param date The date to get events for
   * @return List of events on that date
   */
  public List<IEvent> getEventsOnDate(LocalDateTime date) {
    List<IEvent> result = new ArrayList<IEvent>();
    for (IEvent e : events) {
      if (isSameDate(e.getStartDateTime(), date)) {
        result.add(e);
      }
    }
    Collections.sort(result, new Comparator<IEvent>() {
      @Override
      public int compare(IEvent e1, IEvent e2) {
        return e1.getStartDateTime().compareTo(e2.getStartDateTime());
      }
    });
    return result;
  }

  /**
   * Gets all events within a date range.
   *
   * @param start Start of the range
   * @param end End of the range
   * @return List of events within the range
   */
  public List<IEvent> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    List<IEvent> result = new ArrayList<IEvent>();
    for (IEvent e : events) {
      if (!e.getStartDateTime().isAfter(end) && !e.getEndDateTime().isBefore(start)) {
        result.add(e);
      }
    }
    Collections.sort(result, new Comparator<IEvent>() {
      @Override
      public int compare(IEvent e1, IEvent e2) {
        return e1.getStartDateTime().compareTo(e2.getStartDateTime());
      }
    });
    return result;
  }

  /**
   * Checks if there are any events at a specific date and time.
   *
   * @param dateTime The date and time to check
   * @return true if there are events, false otherwise
   */
  public boolean isBusy(LocalDateTime dateTime) {
    for (IEvent e : events) {
      if (!dateTime.isBefore(e.getStartDateTime()) && !dateTime.isAfter(e.getEndDateTime())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes an event from the calendar.
   * @param event the event to remove
   */
  protected void removeEvent(IEvent event) {
    events.remove(event);
  }
}