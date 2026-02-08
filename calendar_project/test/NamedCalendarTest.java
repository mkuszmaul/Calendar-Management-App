import calendar.model.Event;
import calendar.model.IEvent;
import calendar.model.INamedCalendar;
import calendar.model.NamedCalendar;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the NamedCalendar class's ability to handle names, timezones,
 * and copying events.
 */
public class NamedCalendarTest {

  @Test
  public void testConstructorAndBasicProperties() {
    String calendarName = "Work Calendar";
    TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
    INamedCalendar calendar = new NamedCalendar(calendarName, timeZone);

    assertEquals(calendarName, calendar.getName());
    assertEquals(timeZone, calendar.getTimeZone());
  }

  @Test
  public void testSetName() {
    INamedCalendar calendar = new NamedCalendar("Meeting", TimeZone.getDefault());
    String newName = "Zoom Meeting";
    calendar.setName(newName);
    assertEquals(newName, calendar.getName());
  }

  @Test
  public void testSetTimeZone() {
    INamedCalendar calendar = new NamedCalendar("Calendar", TimeZone.getTimeZone("UTC"));
    TimeZone newTimeZone = TimeZone.getTimeZone("America/Los_Angeles");
    calendar.setTimeZone(newTimeZone);
    assertEquals(newTimeZone, calendar.getTimeZone());
  }

  @Test
  public void testInheritedCalendarFunctionality() {
    INamedCalendar calendar = new NamedCalendar("Calendar One", TimeZone.getDefault());

    //adding and retrieving events
    LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 1, 11, 0);

    IEvent event = Event.getBuilder()
          .subject("Meeting")
          .startDateTime(start)
          .endDateTime(end)
          .description("Team sync")
          .location("Conference room")
          .isPublic(true)
          .build();

    calendar.addEvent(event);

    //test getEvents
    ArrayList<IEvent> events = calendar.getEvents(start, end);
    assertEquals(1, events.size());
    assertEquals("Meeting", events.get(0).getSubject());

    //test isEventAt
    assertTrue(calendar.isEventAt(start));
    assertFalse(calendar.isEventAt(start.minusHours(1)));

    //test event editing
    String newSubject = "Updated Meeting";
    calendar.editEventSubject("Meeting", start, end, newSubject);
    events = calendar.getEvents(start, end);
    assertEquals(newSubject, events.get(0).getSubject());
  }

  @Test
  public void testMultipleTimeZones() {
    //two calendars in different time zones
    INamedCalendar nycCalendar = new NamedCalendar("NYC",
          TimeZone.getTimeZone("America/New_York"));
    INamedCalendar laCalendar = new NamedCalendar("LA",
          TimeZone.getTimeZone("America/Los_Angeles"));

    //add same event to both calendars
    //2 PM EST
    LocalDateTime nycStart = LocalDateTime.of(2024, 1, 1, 14, 0);
    //3 PM EST
    LocalDateTime nycEnd = LocalDateTime.of(2024, 1, 1, 15, 0);

    IEvent nycEvent = Event.getBuilder()
          .subject("Cross-timezone Meeting")
          .startDateTime(nycStart)
          .endDateTime(nycEnd)
          .build();

    //11 AM PST (2 PM EST)
    LocalDateTime laStart = LocalDateTime.of(2024, 1, 1, 11, 0);
    //12 PM PST (3 PM EST)
    LocalDateTime laEnd = LocalDateTime.of(2024, 1, 1, 12, 0);

    IEvent laEvent = Event.getBuilder()
          .subject("Cross-timezone Meeting")
          .startDateTime(laStart)
          .endDateTime(laEnd)
          .build();

    nycCalendar.addEvent(nycEvent);
    laCalendar.addEvent(laEvent);

    //verify events exist in both calendars at appropriate times
    assertTrue(nycCalendar.isEventAt(nycStart));
    assertTrue(laCalendar.isEventAt(laStart));
    assertEquals(1, nycCalendar.getEvents(nycStart, nycEnd).size());
    assertEquals(1, laCalendar.getEvents(laStart, laEnd).size());
  }

  @Test
  public void testEventStorageInCalendarTimezone() {
    TimeZone est = TimeZone.getTimeZone("America/New_York");
    INamedCalendar calendar = new NamedCalendar("EST Calendar", est);

    //create event at 10 AM EST
    LocalDateTime estStart = LocalDateTime.of(2024, 1, 1, 10, 0);
    LocalDateTime estEnd = LocalDateTime.of(2024, 1, 1, 11, 0);

    IEvent event = Event.getBuilder()
          .subject("EST Meeting")
          .startDateTime(estStart)
          .endDateTime(estEnd)
          .build();

    calendar.addEvent(event);

    //verify event is stored and retrieved in EST
    assertTrue("Event should exist at EST time", calendar.isEventAt(estStart));
    ArrayList<IEvent> events = calendar.getEvents(estStart, estEnd);
    assertEquals("Should find one event", 1, events.size());
    assertEquals("Event should be at EST time", estStart, events.get(0).getStartDateTime());

    //change calendar to PST
    TimeZone pst = TimeZone.getTimeZone("America/Los_Angeles");
    calendar.setTimeZone(pst);

    //convert the time to PST
    java.time.ZonedDateTime estTime = estStart.atZone(est.toZoneId());
    java.time.ZonedDateTime pstTime = estTime.withZoneSameInstant(pst.toZoneId());
    LocalDateTime pstStart = pstTime.toLocalDateTime();
    LocalDateTime pstEnd = pstStart.plusHours(1);

    //verify event is now stored and retrieved in PST
    assertTrue("Event should exist at PST time", calendar.isEventAt(pstStart));
    events = calendar.getEvents(pstStart, pstEnd);
    assertEquals("Should find one event", 1, events.size());
    assertEquals("Event should be at PST time", pstStart, events.get(0).getStartDateTime());

    //verify event is not at original EST time
    assertFalse("Event should not exist at original EST time", calendar.isEventAt(estStart));
  }
}