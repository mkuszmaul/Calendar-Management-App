package calendar.controller;

import calendar.model.IEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * A controller for a calendar app with a GUI rather than pure text input. Initializes a
 * model and handles accepting user input from the viewer and calling the appropriate method
 * in the model.
 */
public interface GUICalendarController {
  /**
   * Returns list of names of all calendars currently stored in app.
   * @return list of names of calendars
   */
  public List<String> getCalendarNames();

  /**
   * Switches the calendar in use to the calendar with the given name.
   * @param name name of calendar to select
   */
  public void switchCalendar(String name);

  /**
   * Returns whether there are events taking place on the given date.
   * (on the calendar currently in use).
   * @param date date on which to check for events
   * @return true if there is at least one event on given date
   */
  public boolean hasEventsOnDate(LocalDate date);

  //event management

  /**
   * Create and add to current calendar an event with the given subject, start, and end,
   * and optionally with the given description, location, and public status. Description and
   * location may be null but subject, start, and end must be assigned.
   * @param subject subject to assign event
   * @param start start time to assign event
   * @param end end time to assign event
   * @param description description to assign event (can be null)
   * @param location location to assign event (can be null)
   * @param isPublic public status to assign event
   */
  public void createEvent(String subject, LocalDateTime start, LocalDateTime end,
                          String description, String location, boolean isPublic);

  /**
   * Return list of all events on this calendar starting from the given date.
   * @param date date from which to return all events
   * @return list of all events from date
   */
  public ArrayList<IEvent> getEventsFromDate(LocalDate date);

  /**
   * Creates and adds to model a calendar with given name and timezone.
   * @param name name of calendar
   * @param timezone timezone of calendar
   * @throws Exception if calendar is unable to be added (name null or duplicate)
   */
  public void createCalendar(String name, TimeZone timezone) throws Exception;
}
