import calendar.controller.CalendarController;
import calendar.controller.IController;
import calendar.model.MockCalendar;
import calendar.viewer.MockViewer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A testing class for the controller implementation.
 */
public class TestController {
  @Test
  public void testGoodController() throws FileNotFoundException {
    try {
      String expected = "";
      File file = new File(
            "calendar_project/res/workingCommands.txt");
      MockCalendar model = new MockCalendar();
      IController controller =
            new CalendarController(new Scanner(file), model, new MockViewer());
      controller.begin();

      expected += "makeEventSeriesNTimes(first, [MONDAY, WEDNESDAY], 2025-05-05T10:00, " +
            "2025-05-05T11:00, 6)\n";
      expected += "editSeriesSubject(first, 2025-05-12T10:00, second, false)\n";
      expected += "editSeriesSubject(first, 2025-05-12T10:00, third, true)\n";
      expected += "editSeriesEnd(third, 2025-05-07T10:00, 2025-05-05T11:30, false)\n";
      expected += "addEvent(party (public) from 2025-06-04T23:59 to 2025-06-05T03:30.)\n";
      expected += "editEventLocation(party, 2025-06-04T23:59, 2025-06-05T03:30, physical)\n";
      expected += "editEventStatus(party, 2025-06-04T23:59, 2025-06-05T03:30, true)\n";
      expected += "editEventStart(party, 2025-06-04T23:59, 2025-06-05T03:30, 2025-06-04T23:30)\n";
      expected += "editEventEnd(party, 2025-06-04T23:30, 2025-06-05T03:30, 2025-06-05T23:30)\n";
      expected += "editEventDescription(party, 2025-06-04T23:30, 2025-06-05T23:30, fun!!)\n";
      expected += "getEvents(2025-06-04T00:00, 2025-06-04T23:59:59)\n";
      expected += "getEvents(2025-05-05T10:00, 2025-06-05T23:30)\n";
      expected += "isEventAt(2025-05-05T10:30)\n";
      expected += "isEventAt(2025-05-05T09:30)\n";
      assertEquals(expected, model.toString());

    } catch (FileNotFoundException e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testNoExitInFile() throws FileNotFoundException {
    MockCalendar model = new MockCalendar();
    String expected = "";
    try {
      File file = new File(
            "calendar_project/res/commandsWithNoExit.txt");
      IController controller = new CalendarController(new Scanner(file), model,
            new MockViewer());
      controller.begin();
      fail("Should have thrown an exception");
    } catch (RuntimeException e) {
      assertEquals("No exit command!", e.getMessage());
    }
    expected += "makeEventSeriesNTimes(first, [MONDAY, WEDNESDAY], 2025-05-05T10:00, " +
          "2025-05-05T11:00, 6)\n";
    expected += "editSeriesSubject(first, 2025-05-12T10:00, second, false)\n";
    expected += "editSeriesSubject(first, 2025-05-12T10:00, third, true)\n";
    expected += "editSeriesEnd(third, 2025-05-07T10:00, 2025-05-05T11:30, false)\n";
    expected += "addEvent(party (public) from 2025-06-04T23:59 to 2025-06-05T03:30.)\n";
    expected += "editEventLocation(party, 2025-06-04T23:59, 2025-06-05T03:30, physical)\n";
    expected += "editEventStatus(party, 2025-06-04T23:59, 2025-06-05T03:30, true)\n";
    expected += "editEventStart(party, 2025-06-04T23:59, 2025-06-05T03:30, 2025-06-04T23:30)\n";
    expected += "editEventEnd(party, 2025-06-04T23:30, 2025-06-05T03:30, 2025-06-05T23:30)\n";
    expected += "editEventDescription(party, 2025-06-04T23:30, 2025-06-05T23:30, fun!!)\n";
    expected += "getEvents(2025-06-04T00:00, 2025-06-04T23:59:59)\n";
    expected += "getEvents(2025-05-05T10:00, 2025-06-05T23:30)\n";
    expected += "isEventAt(2025-05-05T10:30)\n";
    expected += "isEventAt(2025-05-05T09:30)\n";
    assertEquals(expected, model.toString());
  }

  @Test
  public void testBadCommandInFile() throws FileNotFoundException {
    MockCalendar model = new MockCalendar();
    String expected = "";
    try {
      File file = new File(
            "calendar_project/res/commandsWithOneInvalid.txt");
      IController controller = new CalendarController(new Scanner(file), model,
            new MockViewer());
      controller.begin();
      fail("Should have thrown an exception");
    } catch (RuntimeException e) {
      assertEquals("Invalid command: bad arguments for edit", e.getMessage());
    }

    expected += "makeEventSeriesNTimes(first, [MONDAY, WEDNESDAY], 2025-05-05T10:00, " +
          "2025-05-05T11:00, 6)\n";
    expected += "editSeriesSubject(first, 2025-05-12T10:00, second, false)\n";
    expected += "editSeriesSubject(first, 2025-05-12T10:00, third, true)\n";
    expected += "editSeriesEnd(third, 2025-05-07T10:00, 2025-05-05T11:30, false)\n";
    expected += "addEvent(party (public) from 2025-06-04T23:59 to 2025-06-05T03:30.)\n";
    expected += "editEventLocation(party, 2025-06-04T23:59, 2025-06-05T03:30, physical)\n";
    expected += "editEventStatus(party, 2025-06-04T23:59, 2025-06-05T03:30, true)\n";
    expected += "editEventStart(party, 2025-06-04T23:59, 2025-06-05T03:30, 2025-06-04T23:30)\n";
    assertEquals(expected, model.toString());
  }

}
