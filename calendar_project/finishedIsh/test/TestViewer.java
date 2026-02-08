import calendar.model.Calendar;
import calendar.model.Event;
import calendar.model.IEvent;
import calendar.viewer.CalendarViewer;
import calendar.viewer.ICalendarViewer;
import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestViewer {
  private IEvent event1;
  private IEvent event2;

  @Before
  public void setUp() {
    LocalDateTime startTime1 =
          LocalDateTime.of(2024, 3, 15, 10, 0);
    // 2024-03-15 10:00
    LocalDateTime endTime1 =
          LocalDateTime.of(2024, 3, 15, 11, 0);
    // 2024-03-15 11:00
    event1 = Event.getBuilder()
          .subject("Team Meeting")
          .startDateTime(startTime1)
          .endDateTime(endTime1)
          .build();

    LocalDateTime startTime2 =
          LocalDateTime.of(2024, 3, 15, 8, 0);
    // 2024-03-15 8am
    event2 = Event.getBuilder().subject("Work")
          .startDateTime(startTime2)
          .location("Fun Inc")
          .description("Worktime :(")
          .isPublic(false)
          .build();
  }

  @Test
  public void testPrint() {
    ICalendarViewer viewer = new CalendarViewer();
    assertEquals("- Team Meeting (public) from 2024-03-15T10:00 to 2024-03-15T11:00.\n",
          viewer.printListOfEvents(new ArrayList<IEvent>(Arrays.asList(event1))));
    assertEquals("- Team Meeting (public) from 2024-03-15T10:00 to 2024-03-15T11:00.\n"
                + "- Work: Worktime :( at Fun Inc from 2024-03-15T08:00 to 2024-03-15T17:00.\n",
          viewer.printListOfEvents(new ArrayList<IEvent>(Arrays.asList(event1, event2))));
  }

  @Test
  public void testBusyStatus() {
    ICalendarViewer viewer = new CalendarViewer();
    assertEquals("AVAILABLE at given time!\n", viewer.showStatusIsBusy(false));
    assertEquals("BUSY at given time!\n", viewer.showStatusIsBusy(true));
  }


}
