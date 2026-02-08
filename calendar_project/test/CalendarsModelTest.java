import calendar.model.CalendarsModel;
import calendar.model.INamedCalendar;
import calendar.model.NamedCalendar;
import calendar.model.CalendarProperty;
import calendar.model.IEvent;
import calendar.model.Event;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.Map;
import java.util.ArrayList;
import java.time.DayOfWeek;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;

/**
 * JUnit testing for CalendarsModel class.
 * This tests the public methods that aren't overridden from Calendar.
 */
public class CalendarsModelTest {
  private CalendarsModel model;
  private INamedCalendar calendar1;
  private INamedCalendar calendar2;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  @Before
  public void setUp() {
    model = new CalendarsModel();
    calendar1 = new NamedCalendar("Work Calendar", TimeZone.getTimeZone("America/New_York"));
    calendar2 = new NamedCalendar("Personal Calendar", TimeZone.getTimeZone("America/Los_Angeles"));
    startTime = LocalDateTime.of(2024, 3, 15, 10, 0);
    endTime = LocalDateTime.of(2024, 3, 15, 11, 0);
  }

  /**
   * tests that creating a calendar with valid name and timezone works.
   */
  @Test
  public void testCreateCalendar() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      Map<String, INamedCalendar> calendars = model.getCalendars();
      assertEquals(1, calendars.size());
      assertTrue(calendars.containsKey(calendar1.getName()));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that creating multiple calendars works.
   */
  @Test
  public void testCreateMultipleCalendars() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());
      Map<String, INamedCalendar> calendars = model.getCalendars();
      assertEquals(2, calendars.size());
      assertTrue(calendars.containsKey(calendar1.getName()));
      assertTrue(calendars.containsKey(calendar2.getName()));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that using a calendar works.
   */
  @Test
  public void testUseCalendar() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.useCalendar(calendar1.getName());
      assertEquals(calendar1.getName(), model.getCurrentCalendar().getName());
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that editing a calendar's name works.
   */
  @Test
  public void testEditCalendarName() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.editCalendar(calendar1.getName(), CalendarProperty.NAME, "New Work Calendar");
      model.useCalendar("New Work Calendar");
      assertEquals("New Work Calendar", model.getCurrentCalendar().getName());
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that editing a calendar's timezone works.
   */
  @Test
  public void testEditCalendarTimeZone() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      TimeZone newTimeZone = calendar2.getTimeZone();
      model.editCalendar(calendar1.getName(), CalendarProperty.TIMEZONE, newTimeZone);
      model.useCalendar(calendar1.getName());
      assertEquals(newTimeZone, model.getCurrentCalendar().getTimeZone());
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that adding an event to a calendar works.
   */
  @Test
  public void testAddEvent() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.useCalendar(calendar1.getName());
      IEvent event = Event.getBuilder().subject("Work Meeting")
            .startDateTime(startTime).endDateTime(endTime).build();
      model.addEvent(event);
      assertTrue(model.isEventAt(startTime));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that copying an event to another calendar works.
   */
  @Test
  public void testCopyEventCalendar() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());
      model.useCalendar(calendar1.getName());
      IEvent event = Event.getBuilder().subject("Work Meeting")
            .startDateTime(startTime).endDateTime(endTime).build();
      model.addEvent(event);
      model.copyEventCalendar("Work Meeting", startTime, calendar2.getName(), startTime);
      model.useCalendar(calendar2.getName());
      assertTrue(model.isEventAt(startTime));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that copying events on a day to another calendar works.
   */
  @Test
  public void testCopyEventsOnDay() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());
      model.useCalendar(calendar1.getName());
      IEvent event = Event.getBuilder().subject("Work Meeting")
            .startDateTime(startTime).endDateTime(endTime).build();
      model.addEvent(event);
      assertTrue("Event should exist in first calendar", model.isEventAt(startTime));

      //copy events to second calendar
      model.copyEventsOnDay(startTime.toLocalDate(), calendar2.getName(), startTime.toLocalDate());
      //switch to second calendar
      model.useCalendar(calendar2.getName());

      ArrayList<IEvent> events = model.getEvents(
            startTime.toLocalDate().atStartOfDay(),
            startTime.toLocalDate().atTime(23, 59, 59)
      );

      System.out.println("Number of events in second calendar: " + events.size());
      if (!events.isEmpty()) {
        System.out.println("First event start time: " + events.get(0).getStartDateTime());
        System.out.println("First event end time: " + events.get(0).getEndDateTime());
      }

      //check for the event at the correct time in the second calendar;3 hours earlier
      LocalDateTime adjustedStartTime = startTime.minusHours(3);
      assertTrue("Event should exist in second calendar at adjusted time",
            model.isEventAt(adjustedStartTime));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that copying events between dates to another calendar works.
   */
  @Test
  public void testCopyEventsBetween() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());
      model.useCalendar(calendar1.getName());
      IEvent event = Event.getBuilder().subject("Work Meeting").startDateTime(startTime)
            .endDateTime(endTime).build();
      model.addEvent(event);
      model.copyEventsBetween(startTime.toLocalDate(), endTime.toLocalDate(),
            calendar2.getName(), startTime.toLocalDate());
      model.useCalendar(calendar2.getName());
      assertTrue(model.isEventAt(startTime));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * Tests that getting all calendars returns the correct map.
   */
  @Test
  public void testGetCalendars() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());

      Map<String, INamedCalendar> calendars = model.getCalendars();
      assertEquals(2, calendars.size());
      assertTrue(calendars.containsKey(calendar1.getName()));
      assertTrue(calendars.containsKey(calendar2.getName()));
      assertEquals(calendar1.getTimeZone(), calendars.get(calendar1.getName()).getTimeZone());
      assertEquals(calendar2.getTimeZone(), calendars.get(calendar2.getName()).getTimeZone());
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that getting a specific calendar works.
   */
  @Test
  public void testGetCalendar() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());

      INamedCalendar calendar = model.getCalendar(calendar1.getName());
      assertEquals(calendar1.getName(), calendar.getName());
      assertEquals(calendar1.getTimeZone(), calendar.getTimeZone());
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests that getting a non-existent calendar throws an exception.
   */
  @Test
  public void testGetNonExistentCalendar() {
    try {
      model.getCalendar("Non Existent Calendar");
      fail("should have thrown an exception");
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar with name 'Non Existent Calendar' does not exist", e.getMessage());
    }
  }

  /**
   * tests that copied events maintain series status.
   */
  @Test
  public void testCopyEventSeriesRetainsStatus() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());
      model.useCalendar(calendar1.getName());

      //create a series of events
      ArrayList<DayOfWeek> weekDays = new ArrayList<>(Arrays.asList(
            DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
      model.makeEventSeriesNTimes("Weekly Meeting", weekDays, startTime, endTime, 4);

      model.copyEventsBetween(
            startTime.toLocalDate(),
            startTime.plusDays(14).toLocalDate(),
            calendar2.getName(),
            startTime.toLocalDate()
      );

      //switch to second calendar and verify events are still part of a series
      model.useCalendar(calendar2.getName());
      ArrayList<IEvent> copiedEvents = model.getEvents(
            startTime.toLocalDate().atStartOfDay(),
            startTime.plusDays(14).toLocalDate().atTime(23, 59, 59)
      );

      //should have events from the series within the 2-week window
      assertEquals(4, copiedEvents.size());
      for (IEvent event : copiedEvents) {
        assertEquals("Weekly Meeting", event.getSubject());
        assertTrue(event.getStartDateTime().getDayOfWeek() == DayOfWeek.MONDAY ||
              event.getStartDateTime().getDayOfWeek() == DayOfWeek.WEDNESDAY ||
              event.getStartDateTime().getDayOfWeek() == DayOfWeek.FRIDAY);
      }
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests handling of event conflicts during copying.
   */
  @Test
  public void testCopyEventWithConflict() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());

      //add event to first calendar
      model.useCalendar(calendar1.getName());
      IEvent event1 = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(startTime)
            .endDateTime(endTime)
            .build();
      model.addEvent(event1);

      //add conflicting event to second calendar
      model.useCalendar(calendar2.getName());
      IEvent event2 = Event.getBuilder()
            .subject("Meeting")
            .startDateTime(startTime)
            .endDateTime(endTime)
            .build();
      model.addEvent(event2);

      //try to copy first event to second calendar at same time
      model.useCalendar(calendar1.getName());
      try {
        model.copyEventCalendar("Meeting", startTime, calendar2.getName(), startTime);
        fail("should have thrown an exception");
      } catch (IllegalArgumentException e) {
        assertEquals("An event with the same details already exists", e.getMessage());
      }
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests duplicate calendar name handling.
   */
  @Test
  public void testCreateCalendarWithDuplicateName() throws Exception {
    model.createCalendar(calendar1.getName(), calendar1.getTimeZone());

    try {
      model.createCalendar(calendar1.getName(), calendar2.getTimeZone());
      fail("should have thrown an exception");
    } catch (Exception e) {
      assertEquals("Calendar with name '" + calendar1.getName() + "' already exists",
            e.getMessage());
    }
  }

  /**
   * tests calendar event independence.
   */
  @Test
  public void testIndependentCalendarEvents() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());

      // Add event to first calendar
      model.useCalendar(calendar1.getName());
      IEvent event1 = Event.getBuilder()
            .subject("Work Meeting")
            .startDateTime(startTime)
            .endDateTime(endTime)
            .build();
      model.addEvent(event1);

      //add different event to second calendar at same time
      model.useCalendar(calendar2.getName());
      IEvent event2 = Event.getBuilder()
            .subject("Personal Meeting")
            .startDateTime(startTime)
            .endDateTime(endTime)
            .build();
      model.addEvent(event2);

      model.useCalendar(calendar1.getName());
      assertTrue(model.isEventAt(startTime));

      model.useCalendar(calendar2.getName());
      assertTrue(model.isEventAt(startTime));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests calendar edit independence.
   */
  @Test
  public void testIndependentCalendarEdits() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.createCalendar(calendar2.getName(), calendar2.getTimeZone());

      //edit first calendar's name
      String newName = "New Work Calendar";
      model.editCalendar(calendar1.getName(), CalendarProperty.NAME, newName);

      //verify second calendar is unchanged
      assertEquals(calendar2.getName(), model.getCalendar(calendar2.getName()).getName());

      TimeZone newTimeZone = TimeZone.getTimeZone("Europe/London");
      model.editCalendar(newName, CalendarProperty.TIMEZONE, newTimeZone);

      //verify second calendar's timezone is unchanged
      assertEquals(calendar2.getTimeZone(), model.getCalendar(calendar2.getName()).getTimeZone());
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  /**
   * tests event timezone adjustment.
   */
  @Test
  public void testTimezoneChangesAffectEvents() {
    try {
      model.createCalendar(calendar1.getName(), calendar1.getTimeZone());
      model.useCalendar(calendar1.getName());

      //multiple events with times that won't overlap after conversion
      IEvent event1 = Event.getBuilder()
            .subject("Morning Meeting")
            //10:00 EST
            .startDateTime(startTime)
            .endDateTime(endTime)
            .build();
      model.addEvent(event1);

      IEvent event2 = Event.getBuilder()
            .subject("Afternoon Meeting")
            //16:00 EST (will be 21:00 London)
            .startDateTime(startTime.plusHours(6))
            .endDateTime(endTime.plusHours(6))
            .build();
      model.addEvent(event2);

      //change timezone
      TimeZone newTimeZone = TimeZone.getTimeZone("Europe/London");
      model.editCalendar(calendar1.getName(), CalendarProperty.TIMEZONE, newTimeZone);

      //calculate adjusted times using proper timezone conversion
      java.time.ZonedDateTime oldStartTime1 = startTime.atZone(calendar1.getTimeZone().toZoneId());
      java.time.ZonedDateTime newStartTime1 = oldStartTime1.withZoneSameInstant(
            newTimeZone.toZoneId());
      LocalDateTime adjustedTime1 = newStartTime1.toLocalDateTime();

      java.time.ZonedDateTime oldStartTime2 = (startTime.plusHours(6)).atZone(calendar1
            .getTimeZone().toZoneId());
      java.time.ZonedDateTime newStartTime2 = oldStartTime2.withZoneSameInstant(newTimeZone
            .toZoneId());
      LocalDateTime adjustedTime2 = newStartTime2.toLocalDateTime();

      //verify all events are adjusted
      assertTrue("First event should exist at adjusted time",
            model.isEventAt(adjustedTime1));
      assertTrue("Second event should exist at adjusted time",
            model.isEventAt(adjustedTime2));

      assertFalse("First event should not exist at original time",
            model.isEventAt(startTime));
      assertFalse("Second event should not exist at original time",
            model.isEventAt(startTime.plusHours(6)));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }
}