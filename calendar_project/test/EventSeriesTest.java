import calendar.model.Event;
import calendar.model.EventSeries;
import calendar.model.IEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * A JUnit test class for the EventSeries class.
 */
public class EventSeriesTest {
  private IEvent baseEvent;
  private Set<DayOfWeek> repeatDays;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private EventSeries series;

  @Before
  public void setUp() {
    startTime = LocalDateTime.of(2024, 3, 15, 10, 0);
    endTime = LocalDateTime.of(2024, 3, 15, 11, 0);

    baseEvent = Event.getBuilder()
          .subject("Zoom Call")
          .startDateTime(startTime)
          .endDateTime(endTime)
          .description("OOD Project")
          .location("Shillman Hall")
          .isPublic(true)
          .build();

    repeatDays = new HashSet<>();
    repeatDays.add(DayOfWeek.MONDAY);
    repeatDays.add(DayOfWeek.WEDNESDAY);
    repeatDays.add(DayOfWeek.FRIDAY);

    series = new EventSeries(baseEvent, repeatDays, 4);
  }

  /**
   * Tests that getEvents() returns the correct number of events.
   * Tests that first event matches base event.
   */
  @Test
  public void testGetEvents() {
    List<IEvent> events = series.getEvents();
    assertEquals(4, events.size());

    // Check first event
    IEvent firstEvent = events.get(0);
    assertEquals("Zoom Call", firstEvent.getSubject());
    assertEquals(startTime, firstEvent.getStartDateTime());
    assertEquals(endTime, firstEvent.getEndDateTime());
    assertEquals("OOD Project", firstEvent.getDescription());
    assertEquals("Shillman Hall", firstEvent.getLocation());
    assertTrue(firstEvent.isPublic());
  }

  /**
   * Tests that repeat days are stored and returned correctly.
   */
  @Test
  public void testGetRepeatDays() {
    Set<DayOfWeek> days = series.getRepeatDays();
    assertEquals(3, days.size());
    assertTrue(days.contains(DayOfWeek.MONDAY));
    assertTrue(days.contains(DayOfWeek.WEDNESDAY));
    assertTrue(days.contains(DayOfWeek.FRIDAY));
  }

  /**
   * Tests that events don't go past the given series end date.
   */
  @Test
  public void testSeriesWithEndDate() {
    LocalDateTime seriesEndDate = startTime.plusDays(14); // 2 weeks
    EventSeries dateSeries = new EventSeries(baseEvent, repeatDays, seriesEndDate);

    List<IEvent> events = dateSeries.getEvents();
    for (IEvent event : events) {
      assertTrue(event.getStartDateTime().isBefore(seriesEndDate) ||
            event.getStartDateTime().equals(seriesEndDate));
    }
  }

  /**
   * Tests modifying a property for all events in the series.
   */
  @Test
  public void testModifyAllEvents() {
    String newSubject = "Modified Series";
    series.modifyAllEvents("subject", newSubject);

    List<IEvent> events = series.getEvents();
    for (IEvent event : events) {
      assertEquals(newSubject, event.getSubject());
    }
  }

  /**
   * Tests modifying events from a specific date forward.
   */
  @Test
  public void testModifyEventsFromDate() {
    String newLocation = "New Location";
    IEvent secondEvent = series.getEvents().get(1);
    series.modifyEventsFromDate(secondEvent, "location", newLocation);

    List<IEvent> events = series.getEvents();
    assertEquals("Shillman Hall", events.get(0).getLocation());
    for (int i = 1; i < events.size(); i++) {
      assertEquals(newLocation, events.get(i).getLocation());
    }
  }

  /**
   * Tests that constructor fails if base event is null.
   */
  @Test
  public void testConstructorWithNullEvent() {
    try {
      new EventSeries(null, repeatDays, 4);
      fail("Expected IllegalArgumentException for null event");
    } catch (IllegalArgumentException e) {
      assertEquals("Base event and repeat days cannot be null or empty", e.getMessage());
    }
  }

  /**
   * Tests that constructor fails if repeat days set is null.
   */
  @Test
  public void testConstructorWithNullRepeatDays() {
    try {
      new EventSeries(baseEvent, null, 4);
      fail("Expected IllegalArgumentException for null repeat days");
    } catch (IllegalArgumentException e) {
      assertEquals("Base event and repeat days cannot be null or empty", e.getMessage());
    }
  }

  /**
   * Tests that constructor fails if repeat days set is empty.
   */
  @Test
  public void testConstructorWithEmptyRepeatDays() {
    try {
      new EventSeries(baseEvent, new HashSet<>(), 4);
      fail("Expected IllegalArgumentException for empty repeat days");
    } catch (IllegalArgumentException e) {
      assertEquals("Base event and repeat days cannot be null or empty", e.getMessage());
    }
  }

  /**
   * Tests that constructor fails if occurrences are negative.
   */
  @Test
  public void testConstructorWithNegativeOccurrences() {
    try {
      new EventSeries(baseEvent, repeatDays, -1);
      fail("Expected IllegalArgumentException for negative occurrences");
    } catch (IllegalArgumentException e) {
      assertEquals("Occurrences must be positive", e.getMessage());
    }
  }

  /**
   * Tests that constructor fails if end date is before start date.
   */
  @Test
  public void testConstructorWithEndDateBeforeStart() {
    try {
      new EventSeries(baseEvent, repeatDays, startTime.minusDays(1));
      fail("Expected IllegalArgumentException for end date before start date");
    } catch (IllegalArgumentException e) {
      assertEquals("End date cannot be before start date", e.getMessage());
    }
  }

  /**
   * Tests that constructor fails if modifying with an invalid property.
   */
  @Test
  public void testModifyWithInvalidProperty() {
    try {
      series.modifyAllEvents("invalid_property", "new value");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid property: invalid_property", e.getMessage());
    }
  }

  /**
   * Tests that series start date matches the base event's start.
   */
  @Test
  public void testGetSeriesStartDate() {
    assertEquals(startTime, series.getSeriesStartDate());
  }

  /**
   * Tests that series end date is correctly calculated.
   */
  @Test
  public void testGetSeriesEndDate() {
    EventSeries dateSeries = new EventSeries(baseEvent, repeatDays,
          startTime.plusDays(7));
    assertNotNull(dateSeries.getSeriesEndDate());
    assertTrue(dateSeries.getSeriesEndDate().isAfter(startTime));
  }
}
