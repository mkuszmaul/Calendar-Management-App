package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * A mock implementation of the Calendars model for testing. Contains shell
 * methods to be called with minimum implementation.
 */
public class MockCalendar implements ICalendarsModel {
  private String log;

  public MockCalendar() {
    log = "";
  }

  @Override
  public void editEventSubject(String subject, LocalDateTime startDate,
                               LocalDateTime endDate, String newSubject) {
    //shell method for mock class
    log += "editEventSubject(" + subject + ", " + startDate.toString() +
          ", " + endDate.toString() + ", " + newSubject + ")\n";
  }

  @Override
  public void editEventStart(String subject, LocalDateTime startDate,
                             LocalDateTime endDate, LocalDateTime newStartDate) {
    //shell method for mock class
    log += "editEventStart(" + subject + ", " + startDate.toString() +
          ", " + endDate.toString() + ", " + newStartDate.toString() + ")\n";
  }

  @Override
  public void editEventEnd(String subject, LocalDateTime startDate,
                           LocalDateTime endDate, LocalDateTime newEndDate) {
    //shell method for mock class
    log += "editEventEnd(" + subject + ", " + startDate.toString() +
          ", " + endDate.toString() + ", " + newEndDate.toString() + ")\n";
  }

  @Override
  public void editEventDescription(String subject, LocalDateTime startDate,
                                   LocalDateTime endDate, String newDescription) {
    //shell method for mock class
    log += "editEventDescription(" + subject + ", " + startDate.toString() +
          ", " + endDate.toString() + ", " + newDescription + ")\n";
  }

  @Override
  public void editEventLocation(String subject, LocalDateTime startDate,
                                LocalDateTime endDate, String newLocation) {
    //shell method for mock class
    log += "editEventLocation(" + subject + ", " + startDate.toString() +
          ", " + endDate.toString() + ", " + newLocation + ")\n";
  }

  @Override
  public void editEventStatus(String subject, LocalDateTime startDate,
                              LocalDateTime endDate, String status) {
    //shell method for mock class
    log += "editEventStatus(" + subject + ", " + startDate.toString() +
          ", " + endDate.toString() + ", " + status + ")\n";
  }


  //NOTE FOR EDITING EVENT SERIES: if given event exists but is not part of
  //series, edit just that event
  @Override
  public void editSeriesSubject(String subject, LocalDateTime startDate,
                                String newSubject, boolean editWholeSeries) {
    //shell method for mock class
    log += "editSeriesSubject(" + subject + ", " + startDate.toString() +
          ", " + newSubject + ", " + editWholeSeries + ")\n";
  }

  @Override
  public void editSeriesStart(String subject, LocalDateTime startDate,
                              LocalDateTime newStartDate, boolean editWholeSeries) {
    //shell method for mock class
    log += "editSeriesStart(" + subject + ", " + startDate.toString() +
          ", " + newStartDate.toString() + ", " + editWholeSeries + ")\n";
  }

  @Override
  public void editSeriesEnd(String subject, LocalDateTime startDate,
                            LocalDateTime newEndDate, boolean editWholeSeries) {
    //shell method for mock class
    log += "editSeriesEnd(" + subject + ", " + startDate.toString() +
          ", " + newEndDate.toString() + ", " + editWholeSeries + ")\n";
  }

  @Override
  public void editSeriesDescription(String subject, LocalDateTime startDate,
                                    String newDescription, boolean editWholeSeries) {
    //shell method for mock class
    log += "editSeriesDescription(" + subject + ", " + startDate.toString() +
          ", " + newDescription + ", " + editWholeSeries + ")\n";
  }

  @Override
  public void editSeriesLocation(String subject, LocalDateTime startDate,
                                 String newLocation, boolean editWholeSeries) {
    //shell method for mock class
    log += "editSeriesLocation(" + subject + ", " + startDate.toString() +
          ", " + newLocation + ", " + editWholeSeries + ")\n";
  }

  @Override
  public void editSeriesStatus(String subject, LocalDateTime startDate,
                               String newStatus, boolean editWholeSeries) {
    //shell method for mock class
    log += "editSeriesStatus(" + subject + ", " + startDate.toString() +
          ", " + newStatus + ", " + editWholeSeries + ")\n";
  }


  @Override
  public void addEvent(IEvent event) {
    //shell method for mock class
    log += "addEvent(" + event.toString() + ")\n";
  }


  @Override
  public void makeEventSeriesNTimes(String subject,
                                    ArrayList<DayOfWeek> daysOfWeek,
                                    LocalDateTime start, LocalDateTime end,
                                    int n) {
    //shell method for mock class
    log += "makeEventSeriesNTimes(" + subject + ", " + daysOfWeek.toString()
          + ", " + start.toString() + ", " + end.toString() + ", " + n + ")\n";
  }


  @Override
  public void makeEventSeriesUntilDate(String subject,
                                       ArrayList<DayOfWeek> daysOfWeek,
                                       LocalDateTime start, LocalDateTime end,
                                       LocalDateTime endDate) {
    //shell method for mock class
    log += "makeEventSeriesUntilDate(" + subject + ", " + daysOfWeek.toString()
          + ", " + start.toString() + ", " + end.toString() + ", " + endDate.toString() + ")\n";
  }


  @Override
  public ArrayList<IEvent> getEvents(LocalDateTime start, LocalDateTime end) {
    log += "getEvents(" + start.toString() + ", " + end.toString() + ")\n";
    return new ArrayList<>();
  }


  @Override
  public boolean isEventAt(LocalDateTime time) {
    log += "isEventAt(" + time.toString() + ")\n";
    return false;
  }

  @Override
  public void createCalendar(String name, TimeZone timeZone) throws Exception {
    log += "createCalendar(" + name + ", " + timeZone.toZoneId().toString() + ")\n";
  }

  @Override
  public void editCalendar(String name, CalendarProperty property, Object newPropertyValue)
        throws Exception {
    log += "editCalendar(" + name + ", " + property.toString() + ", "
          + newPropertyValue.toString() + ")\n";
  }

  @Override
  public void useCalendar(String name) throws Exception {
    log += "useCalendar(" + name + ")\n";
  }

  @Override
  public void copyEventCalendar(String eventName, LocalDateTime originalStartTime, String targetCalendarName, LocalDateTime toStartTime) throws Exception {
    log += "copyEventCalendar(" + eventName + ", " + originalStartTime.toString()
          + ", " + targetCalendarName + ", " + toStartTime.toString() + ")\n";
  }

  @Override
  public void copyEventsOnDay(LocalDate startDate, String targetCalendarName, LocalDate toDate) throws Exception {
    log += "copyEventsOnDay(" + startDate.toString() + ", " + targetCalendarName + ", "
          + toDate.toString() + ")\n";
  }

  @Override
  public void copyEventsBetween(LocalDate startDate, LocalDate endDate, String targetCalendarName, LocalDate toStartDate) throws Exception {
    log += "copyEventsBetween(" + startDate.toString() + ", " + endDate.toString()
          + ", " + targetCalendarName + ", " + toStartDate.toString() + ")\n";
  }

  @Override
  public String toString() {
    return log;
  }
}
