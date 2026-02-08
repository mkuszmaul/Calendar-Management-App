package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Implementation of ICalendarsModel that supports multiple calendars.
 * Each calendar has its own name and timezone, and can contain its own set of events.
 */
public class CalendarsModel implements ICalendarsModel {
  private final Map<String, INamedCalendar> calendars;
  private INamedCalendar currentCalendar;

  /**
   * Creates a new CalendarsModel with no calendars.
   */
  public CalendarsModel() {
    this.calendars = new HashMap<>();
  }

  @Override
  public void createCalendar(String name, TimeZone timeZone) throws Exception {
    if (calendars.containsKey(name)) {
      throw new Exception("Calendar with name '" + name + "' already exists");
    }

    // Validate timezone using ZoneId
    try {
      java.time.ZoneId.of(timeZone.getID());
    } catch (java.time.zone.ZoneRulesException e) {
      throw new IllegalArgumentException("Invalid timezone: " + timeZone.getID());
    }

    INamedCalendar newCalendar = new NamedCalendar(name, timeZone);
    calendars.put(name, newCalendar);

    if (currentCalendar == null) {
      currentCalendar = newCalendar;
    }
  }

  @Override
  public void editCalendar(String name, CalendarProperty property, Object newPropertyValue)
        throws Exception {
    INamedCalendar calendar = calendars.get(name);
    if (calendar == null) {
      throw new Exception("Calendar with name '" + name + "' does not exist");
    }

    switch (property) {
      case NAME:
        if (!(newPropertyValue instanceof String)) {
          throw new Exception("New name must be a string");
        }
        String newName = (String) newPropertyValue;
        if (calendars.containsKey(newName) && !newName.equals(name)) {
          throw new Exception("Calendar with name '" + newName + "' already exists");
        }

        calendars.remove(name);
        calendar.setName(newName);
        calendars.put(newName, calendar);
        break;

      case TIMEZONE:
        if (!(newPropertyValue instanceof TimeZone)) {
          throw new Exception("New timezone must be a TimeZone object");
        }
        calendar.setTimeZone((TimeZone) newPropertyValue);
        break;

      default:
        throw new Exception("Unknown calendar property: " + property);
    }
  }

  @Override
  public void useCalendar(String name) throws Exception {
    INamedCalendar calendar = calendars.get(name);
    if (calendar == null) {
      throw new Exception("Calendar with name '" + name + "' does not exist");
    }
    currentCalendar = calendar;
  }

  @Override
  public void copyEventCalendar(String eventName, LocalDateTime originalStartTime,
                                String targetCalendarName, LocalDateTime toStartTime)
        throws Exception {
    if (currentCalendar == null) {
      throw new Exception("No current calendar selected");
    }

    INamedCalendar targetCalendar = calendars.get(targetCalendarName);
    if (targetCalendar == null) {
      throw new Exception("Target calendar '" + targetCalendarName + "' does not exist");
    }

    ArrayList<IEvent> events = currentCalendar.getEvents(originalStartTime, originalStartTime);
    IEvent eventToCopy = null;
    for (IEvent event : events) {
      if (event.getSubject().equals(eventName) && event.getStartDateTime()
            .equals(originalStartTime)) {
        eventToCopy = event;
        break;
      }
    }

    if (eventToCopy == null) {
      throw new Exception("Event not found in current calendar");
    }

    //calculate the duration of the original event
    long durationMinutes = java.time.Duration.between(
          eventToCopy.getStartDateTime(),
          eventToCopy.getEndDateTime()
    ).toMinutes();

    IEvent newEvent = Event.getBuilder()
          .subject(eventToCopy.getSubject())
          .startDateTime(toStartTime)
          .endDateTime(toStartTime.plusMinutes(durationMinutes))
          .description(eventToCopy.getDescription())
          .location(eventToCopy.getLocation())
          .isPublic(eventToCopy.isPublic())
          .build();

    targetCalendar.addEvent(newEvent);
  }

  @Override
  public void copyEventsOnDay(LocalDate startDate, String targetCalendarName,
                              LocalDate toDate) throws Exception {
    if (currentCalendar == null) {
      throw new Exception("No current calendar selected");
    }

    INamedCalendar targetCalendar = calendars.get(targetCalendarName);
    if (targetCalendar == null) {
      throw new Exception("Target calendar '" + targetCalendarName + "' does not exist");
    }

    LocalDateTime startOfDay = startDate.atStartOfDay()
          .atZone(currentCalendar.getTimeZone().toZoneId())
          .toLocalDateTime()
          .withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

    ArrayList<IEvent> eventsOnDay = currentCalendar.getEvents(startOfDay, endOfDay);

    LocalDateTime targetDate = toDate.atTime(11, 59)
          .atZone(targetCalendar.getTimeZone().toZoneId())
          .toLocalDateTime()
          .withHour(0).withMinute(0).withSecond(0);

    int hourOffset = (targetCalendar.getTimeZone().getRawOffset() -
          currentCalendar.getTimeZone().getRawOffset()) / (60 * 60 * 1000);

    for (IEvent event : eventsOnDay) {
      LocalDateTime newStartTime = targetDate
            .plusHours(event.getStartDateTime().getHour())
            .plusMinutes(event.getStartDateTime().getMinute())
            .plusHours(hourOffset);

      LocalDateTime newEndTime = targetDate
            .plusHours(event.getEndDateTime().getHour())
            .plusMinutes(event.getEndDateTime().getMinute())
            .plusHours(hourOffset);

      IEvent newEvent = Event.getBuilder()
            .subject(event.getSubject())
            .startDateTime(newStartTime)
            .endDateTime(newEndTime)
            .description(event.getDescription())
            .location(event.getLocation())
            .isPublic(event.isPublic())
            .build();

      targetCalendar.addEvent(newEvent);
    }
  }

  @Override
  public void copyEventsBetween(LocalDate startDate, LocalDate endDate, String targetCalendarName,
                                LocalDate toStartDate) throws Exception {
    if (currentCalendar == null) {
      throw new Exception("No current calendar selected");
    }

    INamedCalendar targetCalendar = calendars.get(targetCalendarName);
    if (targetCalendar == null) {
      throw new Exception("Target calendar '" + targetCalendarName + "' does not exist");
    }

    LocalDateTime start = startDate.atStartOfDay()
          .atZone(currentCalendar.getTimeZone().toZoneId())
          .toLocalDateTime();
    LocalDateTime end = endDate.atTime(11, 59)
          .atZone(currentCalendar.getTimeZone().toZoneId())
          .toLocalDateTime();
    LocalDateTime targetStart = toStartDate.atStartOfDay()
          .atZone(targetCalendar.getTimeZone().toZoneId())
          .toLocalDateTime();

    ArrayList<IEvent> events = currentCalendar.getEvents(start, end);

    for (IEvent event : events) {
      long dayOffset = java.time.Duration.between(
            start.toLocalDate().atStartOfDay(),
            event.getStartDateTime().toLocalDate().atStartOfDay()
      ).toDays();

      LocalDateTime newStartTime = targetStart.plusDays(dayOffset)
            .withHour(event.getStartDateTime().getHour())
            .withMinute(event.getStartDateTime().getMinute());

      long eventDuration = java.time.Duration.between(
            event.getStartDateTime(),
            event.getEndDateTime()
      ).toMinutes();

      IEvent newEvent = Event.getBuilder()
            .subject(event.getSubject())
            .startDateTime(newStartTime)
            .endDateTime(newStartTime.plusMinutes(eventDuration))
            .description(event.getDescription())
            .location(event.getLocation())
            .isPublic(event.isPublic())
            .build();

      targetCalendar.addEvent(newEvent);
    }
  }


  @Override
  public void addEvent(IEvent event) {
    checkCurrentCalendar();
    currentCalendar.addEvent(event);
  }

  @Override
  public ArrayList<IEvent> getEvents(LocalDateTime start, LocalDateTime end) {
    checkCurrentCalendar();
    return currentCalendar.getEvents(start, end);
  }

  @Override
  public boolean isEventAt(LocalDateTime time) {
    checkCurrentCalendar();
    return currentCalendar.isEventAt(time);
  }

  //ICalendarModel methods
  @Override
  public void editEventSubject(String subject, LocalDateTime startDate, LocalDateTime endDate,
                               String newSubject) {
    checkCurrentCalendar();
    currentCalendar.editEventSubject(subject, startDate, endDate, newSubject);
  }

  @Override
  public void editEventStart(String subject, LocalDateTime startDate, LocalDateTime endDate,
                             LocalDateTime newStartDate) {
    checkCurrentCalendar();
    currentCalendar.editEventStart(subject, startDate, endDate, newStartDate);
  }

  @Override
  public void editEventEnd(String subject, LocalDateTime startDate, LocalDateTime endDate,
                           LocalDateTime newEndDate) {
    checkCurrentCalendar();
    currentCalendar.editEventEnd(subject, startDate, endDate, newEndDate);
  }

  @Override
  public void editEventDescription(String subject, LocalDateTime startDate, LocalDateTime endDate,
                                   String newDescription) {
    checkCurrentCalendar();
    currentCalendar.editEventDescription(subject, startDate, endDate, newDescription);
  }

  @Override
  public void editEventLocation(String subject, LocalDateTime startDate, LocalDateTime endDate,
                                String newLocation) {
    checkCurrentCalendar();
    currentCalendar.editEventLocation(subject, startDate, endDate, newLocation);
  }

  @Override
  public void editEventStatus(String subject, LocalDateTime startDate, LocalDateTime endDate,
                              String status) {
    checkCurrentCalendar();
    currentCalendar.editEventStatus(subject, startDate, endDate, status);
  }

  @Override
  public void editSeriesSubject(String subject, LocalDateTime startDate, String newSubject,
                                boolean editWholeSeries) {
    checkCurrentCalendar();
    currentCalendar.editSeriesSubject(subject, startDate, newSubject, editWholeSeries);
  }

  @Override
  public void editSeriesStart(String subject, LocalDateTime startDate, LocalDateTime newStartDate,
                              boolean editWholeSeries) {
    checkCurrentCalendar();
    currentCalendar.editSeriesStart(subject, startDate, newStartDate, editWholeSeries);
  }

  @Override
  public void editSeriesEnd(String subject, LocalDateTime startDate, LocalDateTime newEndDate,
                            boolean editWholeSeries) {
    checkCurrentCalendar();
    currentCalendar.editSeriesEnd(subject, startDate, newEndDate, editWholeSeries);
  }

  @Override
  public void editSeriesDescription(String subject, LocalDateTime startDate, String newDescription,
                                    boolean editWholeSeries) {
    checkCurrentCalendar();
    currentCalendar.editSeriesDescription(subject, startDate, newDescription, editWholeSeries);
  }

  @Override
  public void editSeriesLocation(String subject, LocalDateTime startDate, String newLocation,
                                 boolean editWholeSeries) {
    checkCurrentCalendar();
    currentCalendar.editSeriesLocation(subject, startDate, newLocation, editWholeSeries);
  }

  @Override
  public void editSeriesStatus(String subject, LocalDateTime startDate, String newStatus,
                               boolean editWholeSeries) {
    checkCurrentCalendar();
    currentCalendar.editSeriesStatus(subject, startDate, newStatus, editWholeSeries);
  }

  @Override
  public void makeEventSeriesNTimes(String subject, ArrayList<DayOfWeek> daysOfWeek,
                                    LocalDateTime start, LocalDateTime end, int n) {
    checkCurrentCalendar();
    currentCalendar.makeEventSeriesNTimes(subject, daysOfWeek, start, end, n);
  }

  @Override
  public void makeEventSeriesUntilDate(String subject, ArrayList<DayOfWeek> daysOfWeek,
                                       LocalDateTime start, LocalDateTime end,
                                       LocalDateTime endDate) {
    checkCurrentCalendar();
    currentCalendar.makeEventSeriesUntilDate(subject, daysOfWeek, start, end, endDate);
  }

  private void checkCurrentCalendar() {
    if (currentCalendar == null) {
      throw new IllegalStateException("No calendar is currently selected");
    }
  }

  /**
   * Gets the currently selected calendar.
   * @return the current calendar
   * @throws IllegalStateException if no calendar is currently selected
   */
  public INamedCalendar getCurrentCalendar() {
    checkCurrentCalendar();
    return currentCalendar;
  }

  /**
   * Gets all calendars in the model.
   * @return a map of calendar names to their calendar objects
   */
  public Map<String, INamedCalendar> getCalendars() {
    return new HashMap<>(calendars);
  }

  /**
   * Gets a specific calendar by name.
   * @param name the name of the calendar to get
   * @return the calendar with the given name
   * @throws IllegalArgumentException if no calendar exists with the given name
   */
  public INamedCalendar getCalendar(String name) {
    INamedCalendar calendar = calendars.get(name);
    if (calendar == null) {
      throw new IllegalArgumentException("Calendar with name '" + name + "' does not exist");
    }
    return calendar;
  }
}