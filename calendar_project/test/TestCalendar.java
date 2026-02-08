import calendar.model.Calendar;
import calendar.model.IEvent;
import calendar.model.Event;
import calendar.model.EventSeries;
import calendar.model.ICalendarModel;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A testing class for the Calendar implementation of the ICalendarModel interface.
 */
public class TestCalendar {

  //tests both addEvent and getEvents in conjunction
  @Test
  public void testAddAndGetEvents() {
    ICalendarModel myCal = new Calendar();
    ArrayList<IEvent> events = new ArrayList<>();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);
    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    assertEquals(events, myCal.getEvents(startTime, endTime));
    myCal.addEvent(partyEvent);
    events.add(partyEvent);
    assertEquals(myCal.getEvents(startTime, endTime), events);
    try {
      myCal.addEvent(partyEvent);
      fail("Should have thrown an exception");
    } catch (Exception e) {
      assertEquals("An event with the same details already exists", e.getMessage());
    }
    try {
      IEvent similarEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
            .endDateTime(endTime).description("Fun!!!").build();
      myCal.addEvent(similarEvent);
      fail("Should have thrown an exception");
    } catch (Exception e) {
      assertEquals("An event with the same details already exists", e.getMessage());
    }

    try {
      ArrayList<DayOfWeek> tuesdayAndFriday = new ArrayList<DayOfWeek>();
      tuesdayAndFriday.add(DayOfWeek.TUESDAY);
      tuesdayAndFriday.add(DayOfWeek.FRIDAY);
      myCal.makeEventSeriesNTimes("Party", tuesdayAndFriday,
            startTime, endTime, 10);
      fail("Should have thrown an exception");
    } catch (Exception e) {
      assertEquals("Event series conflicts with existing events", e.getMessage());
    }
  }

  @Test
  public void testMakeEventSeriesNTimes() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);
    LocalDateTime laterTime = LocalDateTime.of(2025, 5, 30,
          0, 0);
    ArrayList<DayOfWeek> tuesdayAndFriday = new ArrayList<DayOfWeek>();
    tuesdayAndFriday.add(DayOfWeek.TUESDAY);
    tuesdayAndFriday.add(DayOfWeek.FRIDAY);
    myCal.makeEventSeriesNTimes("Party", tuesdayAndFriday,
          startTime, endTime, 10);
    Set<DayOfWeek> setOfWeekdays = new HashSet<DayOfWeek>(tuesdayAndFriday);
    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    ArrayList<IEvent> events = new ArrayList<IEvent>(new
          EventSeries(partyEvent, setOfWeekdays, 10).getEvents());
    assertEquals(partyEvent, myCal.getEvents(startTime, endTime).get(0));
    assertEquals(events, myCal.getEvents(startTime, laterTime));
  }

  @Test
  public void testMakeEventSeriesUntilDate() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25, 7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2, 25, 10, 30);
    LocalDateTime laterTime = LocalDateTime.of(2025, 5, 30, 0, 0);

    ArrayList<DayOfWeek> tuesdayAndFriday = new ArrayList<DayOfWeek>();
    tuesdayAndFriday.add(DayOfWeek.TUESDAY);
    tuesdayAndFriday.add(DayOfWeek.FRIDAY);


    myCal.makeEventSeriesUntilDate("Party", tuesdayAndFriday, startTime, endTime, laterTime);


    ICalendarModel expectedCal = new Calendar();
    expectedCal.makeEventSeriesUntilDate("Party", tuesdayAndFriday, startTime, endTime,
          laterTime);


    assertEquals(expectedCal.getEvents(startTime, laterTime),
          myCal.getEvents(startTime, laterTime));

    IEvent firstEvent = myCal.getEvents(startTime, endTime).get(0);
    assertEquals("Party", firstEvent.getSubject());
    assertEquals(startTime, firstEvent.getStartDateTime());
    assertEquals(endTime, firstEvent.getEndDateTime());
  }

  @Test
  public void testIsEventAt() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);
    assertFalse(myCal.isEventAt(startTime));

    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    myCal.addEvent(partyEvent);
    assertTrue(myCal.isEventAt(startTime));
    assertFalse(myCal.isEventAt(LocalDateTime.of(2020, 2,
          25, 10, 31)));

    ArrayList<DayOfWeek> weekDays = new ArrayList<>(Arrays.asList(
          DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
    myCal.makeEventSeriesNTimes("Work", weekDays,
          LocalDateTime.of(2024, 1, 1, 9, 0),
          LocalDateTime.of(2025, 1, 1, 17, 0),
          50);
    assertTrue(myCal.isEventAt(
          LocalDateTime.of(2024, 1, 1, 12, 0)));
    assertTrue(myCal.isEventAt(
          LocalDateTime.of(2024, 2, 15, 12, 0)));
    assertFalse(myCal.isEventAt(
          LocalDateTime.of(2025, 1, 1, 12, 0)));
  }

  @Test
  public void testEditSubject() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);
    assertFalse(myCal.isEventAt(startTime));

    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    myCal.addEvent(partyEvent);
    assertEquals(partyEvent, myCal.getEvents(startTime, endTime).get(0));
    myCal.editEventSubject("Party", startTime, endTime, "Party!!!!");
    IEvent newPartyEvent = Event.getBuilder().subject("Party!!!!")
          .startDateTime(startTime).endDateTime(endTime).build();
    assertEquals(newPartyEvent, myCal.getEvents(startTime, endTime).get(0));

    try {
      myCal.editEventSubject("not real event", startTime, endTime,
            "eventName");
      fail();
    } catch (Exception e) {
      assertEquals("Event not found in calendar", e.getMessage());
    }
  }

  @Test
  public void testEditDescription() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);
    assertFalse(myCal.isEventAt(startTime));

    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    myCal.addEvent(partyEvent);
    assertEquals(partyEvent, myCal.getEvents(startTime, endTime).get(0));

    myCal.editEventDescription("Party", startTime, endTime, "Fun!");
    IEvent newPartyEvent = Event.getBuilder().subject("Party")
          .startDateTime(startTime).endDateTime(endTime)
          .description("Fun!").build();
    assertEquals(newPartyEvent, myCal.getEvents(startTime, endTime).get(0));

    try {
      myCal.editEventDescription("not real event", startTime, endTime,
            "eventDesc");
      fail();
    } catch (Exception e) {
      assertEquals("Event not found in calendar", e.getMessage());
    }
  }

  @Test
  public void testEditLocation() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);
    assertFalse(myCal.isEventAt(startTime));

    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    myCal.addEvent(partyEvent);
    assertEquals(partyEvent, myCal.getEvents(startTime, endTime).get(0));

    myCal.editEventLocation("Party", startTime, endTime, "My house!");
    IEvent newPartyEvent = Event.getBuilder().subject("Party")
          .startDateTime(startTime).endDateTime(endTime)
          .location("My house!").build();
    assertEquals(newPartyEvent, myCal.getEvents(startTime, endTime).get(0));

    try {
      myCal.editEventLocation("not real event", startTime, endTime,
            "my house!");
      fail();
    } catch (Exception e) {
      assertEquals("Event not found in calendar", e.getMessage());
    }
  }

  @Test
  public void testEditStatus() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);
    assertFalse(myCal.isEventAt(startTime));

    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    myCal.addEvent(partyEvent);
    assertEquals(partyEvent, myCal.getEvents(startTime, endTime).get(0));

    myCal.editEventStatus("Party", startTime, endTime, "true");
    IEvent newPartyEvent = Event.getBuilder().subject("Party")
          .startDateTime(startTime).endDateTime(endTime)
          .isPublic(true).build();
    assertEquals(newPartyEvent, myCal.getEvents(startTime, endTime).get(0));

    try {
      myCal.editEventStatus("not real event", startTime, endTime,
            "true");
      fail();
    } catch (Exception e) {
      assertEquals("Event not found in calendar", e.getMessage());
    }
  }

  @Test
  public void testEditStartTime() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);

    LocalDateTime newStartTime = LocalDateTime.of(2020, 2,
          25, 9, 45);
    assertFalse(myCal.isEventAt(startTime));

    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    myCal.addEvent(partyEvent);
    assertEquals(partyEvent, myCal.getEvents(startTime, endTime).get(0));

    myCal.editEventStart("Party", startTime, endTime, newStartTime);
    IEvent newPartyEvent = Event.getBuilder().subject("Party")
          .startDateTime(newStartTime).endDateTime(endTime).build();
    assertEquals(newPartyEvent, myCal.getEvents(newStartTime, endTime).get(0));

    try {
      myCal.editEventStart("not real event", newStartTime, endTime,
            newStartTime);
      fail();
    } catch (Exception e) {
      assertEquals("Event not found in calendar", e.getMessage());
    }
  }

  @Test
  public void testEditEndTime() {
    ICalendarModel myCal = new Calendar();
    LocalDateTime startTime = LocalDateTime.of(2020, 2, 25,
          7, 30);
    LocalDateTime endTime = LocalDateTime.of(2020, 2,
          25, 10, 30);

    LocalDateTime newEndTime = LocalDateTime.of(2020, 2,
          26, 2, 0);
    assertFalse(myCal.isEventAt(startTime));

    IEvent partyEvent = Event.getBuilder().subject("Party").startDateTime(startTime)
          .endDateTime(endTime).build();
    myCal.addEvent(partyEvent);
    assertEquals(partyEvent, myCal.getEvents(startTime, endTime).get(0));

    myCal.editEventEnd("Party", startTime, endTime, newEndTime);
    IEvent newPartyEvent = Event.getBuilder().subject("Party")
          .startDateTime(startTime).endDateTime(newEndTime).build();
    assertEquals(newPartyEvent, myCal.getEvents(startTime, newEndTime).get(0));

    try {
      myCal.editEventEnd("not real event", newEndTime, endTime,
            newEndTime);
      fail();
    } catch (Exception e) {
      assertEquals("Event not found in calendar", e.getMessage());
    }
  }

  @Test
  public void testEditSeriesSubject() {
    ICalendarModel calendar = new Calendar();
    LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 0);
    ArrayList<DayOfWeek> weekDays = new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY,
          DayOfWeek.WEDNESDAY));

    calendar.makeEventSeriesNTimes("Meeting", weekDays, start, end, 4);

    //initial subjects
    System.out.println("Initial events:");
    for (IEvent event : calendar.getEvents(start, LocalDateTime.of(2024, 12,
          31, 23, 59))) {
      System.out.println("Subject: " + event.getSubject());
    }

    //editing whole series
    calendar.editSeriesSubject("Meeting", start, "OOD Meeting",
          true);

    //print subjects after update
    System.out.println("\nAfter update:");
    for (IEvent event : calendar.getEvents(start, LocalDateTime.of(2024, 12,
          31, 23, 59))) {
      System.out.println("Subject: " + event.getSubject());
      assertEquals("OOD Meeting", event.getSubject());
    }

    //test editing from a specific event
    LocalDateTime secondEventStart = start.plusDays(2); // Next Wednesday
    calendar.editSeriesSubject("OOD Meeting", secondEventStart,
          "Changed Meeting", false);

    //print subjects after second update
    System.out.println("\nAfter second update:");
    List<IEvent> events = calendar.getEvents(start, LocalDateTime.of(2024, 12,
          31, 23, 59));
    for (IEvent event : events) {
      System.out.println("Subject: " + event.getSubject() + " at " + event.getStartDateTime());
    }

    //first event should remain unchanged
    assertEquals("OOD Meeting", calendar.getEvents(start, start).get(0).getSubject());

    //later events should be changed
    for (IEvent event : calendar.getEvents(secondEventStart, LocalDateTime.of(2024, 12,
          31, 23, 59))) {
      assertEquals("Changed Meeting", event.getSubject());
    }
  }

  @Test
  public void testEditSeriesDateTime() {
    ICalendarModel calendar = new Calendar();
    LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 0);
    ArrayList<DayOfWeek> weekDays = new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY,
          DayOfWeek.WEDNESDAY));

    calendar.makeEventSeriesNTimes("Meeting", weekDays, start, end, 4);

    //editing start time for whole series
    LocalDateTime newStart = start.plusHours(1);
    LocalDateTime newEnd = end.plusHours(1);
    calendar.editSeriesStart("Meeting", start, newStart, true);
    calendar.editSeriesEnd("Meeting", start, newEnd, true);

    for (IEvent event : calendar.getEvents(newStart, LocalDateTime.of(2024, 12,
          31, 23, 59))) {
      assertEquals(event.getStartDateTime().getHour(), 10);
      assertEquals(event.getEndDateTime().getHour(), 11);
    }
  }

  @Test
  public void testEditSeriesProperties() {
    ICalendarModel calendar = new Calendar();
    LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 0);
    ArrayList<DayOfWeek> weekDays = new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY,
          DayOfWeek.WEDNESDAY));

    calendar.makeEventSeriesNTimes("Meeting", weekDays, start, end, 4);

    //test editing description, location and status
    calendar.editSeriesDescription("Meeting", start, "Team Sync",
          true);
    calendar.editSeriesLocation("Meeting", start, "Conference Room",
          true);
    calendar.editSeriesStatus("Meeting", start, "true",
          true);

    for (IEvent event : calendar.getEvents(start, LocalDateTime.of(2024, 12,
          31, 23, 59))) {
      assertEquals("Team Sync", event.getDescription());
      assertEquals("Conference Room", event.getLocation());
      assertTrue(event.isPublic());
    }
  }

  @Test
  public void testEventSeriesConflicts() {
    ICalendarModel calendar = new Calendar();
    LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 0);
    ArrayList<DayOfWeek> weekDays = new ArrayList<>(List.of(DayOfWeek.MONDAY));

    calendar.makeEventSeriesNTimes("Meeting 1", weekDays, start, end, 4);

    //attempt to create conflicting series
    try {
      calendar.makeEventSeriesNTimes("Meeting 1", weekDays, start, end, 4);
      fail("Should have thrown exception for conflicting series");
    } catch (IllegalArgumentException e) {
      assertEquals("Event series conflicts with existing events", e.getMessage());
    }

    //non-conflicting series
    LocalDateTime laterStart = start.plusHours(2);
    LocalDateTime laterEnd = end.plusHours(2);
    calendar.makeEventSeriesNTimes("Meeting 2", weekDays, laterStart, laterEnd, 4);

    //verify both series exist
    List<IEvent> allEvents = calendar.getEvents(start, LocalDateTime.of(2024, 12,
          31, 23, 59));
    //4 events from each series
    assertEquals(8, allEvents.size());
  }

  @Test
  public void testMakeEventSeriesUntilDateWithConflicts() {
    ICalendarModel calendar = new Calendar();
    LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 0);
    LocalDateTime seriesEndDate = LocalDateTime.of(2024, 1, 31,
          23, 59);
    ArrayList<DayOfWeek> weekDays = new ArrayList<>(Arrays.asList(DayOfWeek.MONDAY,
          DayOfWeek.WEDNESDAY));

    //create first series
    calendar.makeEventSeriesUntilDate("Meeting 1", weekDays, start, end, seriesEndDate);

    //attempt to create conflicting series
    try {
      calendar.makeEventSeriesUntilDate("Meeting 1", weekDays, start, end, seriesEndDate);
      fail("Should have thrown exception for conflicting series");
    } catch (IllegalArgumentException e) {
      assertEquals("Event series conflicts with existing events", e.getMessage());
    }

    //non-conflicting series
    ArrayList<DayOfWeek> differentDays = new ArrayList<>(Arrays.asList(DayOfWeek.TUESDAY,
          DayOfWeek.THURSDAY));
    calendar.makeEventSeriesUntilDate("Meeting 2", differentDays, start, end,
          seriesEndDate);

    List<IEvent> allEvents = calendar.getEvents(start, seriesEndDate);
    for (IEvent event : allEvents) {
      DayOfWeek day = event.getStartDateTime().getDayOfWeek();
      if (event.getSubject().equals("Meeting 1")) {
        assertTrue(weekDays.contains(day));
      } else {
        assertTrue(differentDays.contains(day));
      }
    }
  }
}