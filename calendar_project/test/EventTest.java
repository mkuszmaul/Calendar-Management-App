import calendar.model.Event;
import calendar.model.IEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotEquals;

/**
 * A JUnit test class for the IEvent.Event class.
 */
public class EventTest {

  private IEvent event;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  @Before
  public void setUp() {
    startTime = LocalDateTime.of(2024, 3, 15, 10, 0);
    // 2024-03-15 10:00
    endTime = LocalDateTime.of(2024, 3, 15, 11, 0);
    // 2024-03-15 11:00

    event = Event.getBuilder()
          .subject("Team Meeting")
          .startDateTime(startTime)
          .endDateTime(endTime)
          .build();

  }

  /**
   * Tests the creation of a basic event with only subject, start, and end time.
   */
  @Test
  public void testCreateBasicEvent() {
    assertEquals("Team Meeting", event.getSubject());
    assertEquals(startTime, event.getStartDateTime());
    assertEquals(endTime, event.getEndDateTime());
    assertTrue(event.isPublic());
    assertNull(event.getDescription());
    assertNull(event.getLocation());
  }

  /**
   * Tests the creation of a full event with all optional fields set.
   */
  @Test
  public void testCreateFullEvent() {
    IEvent event = Event.getBuilder()
          .subject("Team Meeting")
          .startDateTime(startTime)
          .endDateTime(endTime)
          .description("Weekly sync")
          .location("Conference Room A")
          .isPublic(false)
          .build();

    assertEquals("Team Meeting", event.getSubject());
    assertEquals(startTime, event.getStartDateTime());
    assertEquals(endTime, event.getEndDateTime());
    assertEquals("Weekly sync", event.getDescription());
    assertEquals("Conference Room A", event.getLocation());
    assertFalse(event.isPublic());
  }

  /**
   * Tests an all-day event.
   */
  @Test
  public void testAllDayEvent() {
    IEvent allDayEvent = Event.getBuilder()
          .subject("All Day Meeting")
          .startDateTime(startTime)
          .build();

    // Test that all-day event times are set correctly
    assertEquals(8, allDayEvent.getStartDateTime().getHour());
    assertEquals(0, allDayEvent.getStartDateTime().getMinute());
    assertEquals(17, allDayEvent.getEndDateTime().getHour());
    assertEquals(0, allDayEvent.getEndDateTime().getMinute());
    assertEquals("All Day Meeting", allDayEvent.getSubject());
  }

  /**
   * Tests equality of events if they have the same subject, start/end time.
   */
  @Test
  public void testEventEquality() {
    IEvent event1 = Event.getBuilder()
          .subject("Team Meeting")
          .startDateTime(startTime)
          .endDateTime(endTime)
          .description("Description 1")
          .location("Location 1")
          .build();

    IEvent event2 = Event.getBuilder()
          .subject("Team Meeting")
          .startDateTime(startTime)
          .endDateTime(endTime)
          .description("Different Description")
          .location("Different Location")
          .build();

    IEvent event3 = Event.getBuilder()
          .subject("Different Meeting")
          .startDateTime(startTime)
          .endDateTime(endTime)
          .build();

    assertEquals(event1, event2);
    assertNotEquals(event1, event3);
    assertEquals(event1.hashCode(), event2.hashCode());
  }

  /**
   * Tests that Event builder throws exceptions when:
   * subject/start time is missing.
   * End time is before start time.
   */
  @Test
  public void testInvalidEventCreation() {
    try {
      Event.getBuilder()
            .startDateTime(startTime)
            .endDateTime(endTime)
            .build();
      fail("Expected IllegalStateException for missing subject");
    } catch (IllegalStateException e) {
      assertEquals("Subject is required", e.getMessage());
    }

    try {
      Event.getBuilder()
            .subject("Test Event")
            .endDateTime(endTime)
            .build();
      fail("Expected IllegalStateException for missing start time");
    } catch (IllegalStateException e) {
      assertEquals("Start date/time is required", e.getMessage());
    }

    try {
      Event.getBuilder()
            .subject("Test Event")
            .startDateTime(endTime)
            .endDateTime(startTime)
            .build();
      fail("Expected IllegalStateException for invalid time range");
    } catch (IllegalStateException e) {
      assertEquals("End date/time cannot be before start date/time", e.getMessage());
    }
  }
}