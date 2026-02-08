import calendar.model.Event;
import calendar.model.IEvent;
import calendar.model.ISmartCalendarsModel;
import calendar.model.SmartCalendarsModel;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A testing class for SmartCalendars, the newest implementation of the model. Tests
 * model's ability to retrieve lists of calendar names and events.
 */
public class TestSmartCalendars {
  /**
   * Tests model's ability to retrieve list of current calendar names.
   */
  @Test
  public void testGetCalendarNames() {
    ISmartCalendarsModel model = new SmartCalendarsModel();
    assertEquals(model.getCalendarNames(), new ArrayList<String>());
    try {
      model.createCalendar("Maggie's calendar", TimeZone.getDefault());
      model.createCalendar("Calendar 2", TimeZone.getDefault());
      model.createCalendar("Calendar 3", TimeZone.getTimeZone("Europe/London"));
    }
    catch (Exception e) {
      throw new RuntimeException("Shouldn't have thrown an exception");
    }
    assertTrue(model.getCalendarNames().contains("Maggie's calendar"));
    assertTrue(model.getCalendarNames().contains("Calendar 2"));
    assertTrue(model.getCalendarNames().contains("Calendar 3"));
  }

  /**
   * Tests model's ability to retrieve list of all events after a certain time.
   */
  @Test
  public void testGetEvents() {
    ISmartCalendarsModel model = new SmartCalendarsModel();
    try {
      model.createCalendar("Default", TimeZone.getDefault());
      model.useCalendar("Default");
    }
    catch (Exception e) {
      throw new RuntimeException("Shouldn't have thrown an exception");
    }
    LocalDateTime start1 =
          LocalDateTime.of(2025, 6, 15, 18, 30);
    LocalDateTime end1 =
          LocalDateTime.of(2025, 6, 15, 23, 30);
    LocalDateTime start2 =
          LocalDateTime.of(2030, 10, 15, 12, 30);
    LocalDateTime end2 = LocalDateTime.of(2030, 10, 15, 17, 30);

    IEvent event1 = Event.getBuilder().subject("Party!").startDateTime(start1)
          .endDateTime(end1).description("Bob's birthday!").build();
    IEvent event2 = Event.getBuilder().subject("Work").startDateTime(start2)
                .endDateTime(end2).build();
    model.addEvent(event1);
    model.addEvent(event2);
    assertTrue(model.getEvents(start1, LocalDateTime.MAX).contains(event1));
    assertTrue(model.getEvents(start1, LocalDateTime.MAX).contains(event2));
  }
}
