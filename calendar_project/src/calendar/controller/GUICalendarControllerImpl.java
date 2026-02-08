package calendar.controller;

import calendar.model.ISmartCalendarsModel;
import calendar.model.IEvent;
import calendar.model.IEventBuilder;
import calendar.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Implementation of a controller for a GUI version of the calendar app.
 * Initializes the model with a default calendar when constructed, and translates
 * user input from the viewer to the appropriate method call in the model.
 */
public class GUICalendarControllerImpl implements GUICalendarController {
  private ISmartCalendarsModel model;

  /**
   * Public constructor. Sets model with a calendar called "Default Calendar"
   * in the user's timezone.
   * @param model model to be used
   */
  public GUICalendarControllerImpl(ISmartCalendarsModel model) {
    this.model = model;
    try {
      model.createCalendar("Default Calendar", TimeZone.getDefault());
      model.useCalendar("Default Calendar");
    }
    catch (Exception e) {
      throw new RuntimeException("Error initializing model");
    }
  }

  @Override
  public List<String> getCalendarNames() {
    return model.getCalendarNames();
  }

  @Override
  public void switchCalendar(String name) {
    try {
      model.useCalendar(name);
    }
    catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public boolean hasEventsOnDate(LocalDate date) {
    return ! model.getEvents(date.atStartOfDay(), date.atTime(23, 59)).isEmpty();
  }

  //NOTE: ASSUMES END IS INITIALIZED (relevant if we want to support adding all-day events)
  @Override
  public void createEvent(String subject, LocalDateTime start, LocalDateTime end,
                          String description, String location, boolean isPublic) {
    IEventBuilder eventBuilder = Event.getBuilder();
    eventBuilder.subject(subject);
    eventBuilder.startDateTime(start);
    eventBuilder.endDateTime(end);
    if (description != null) {
      eventBuilder.description(description);
    }
    if (location != null) {
      eventBuilder.location(location);
    }
    eventBuilder.isPublic(isPublic);
    model.addEvent(eventBuilder.build());
  }

  @Override
  public ArrayList<IEvent> getEventsFromDate(LocalDate date) {
    return model.getEvents(date.atStartOfDay(), LocalDateTime.MAX);
  }

  @Override
  public void createCalendar(String name, TimeZone timezone) throws Exception {
    model.createCalendar(name, timezone);
  }
}
