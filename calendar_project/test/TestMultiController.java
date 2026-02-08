import calendar.controller.IController;
import calendar.controller.MultiCalendarsController;
import calendar.model.MockCalendar;
import calendar.viewer.MockViewer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the MultiCalendarsController class. Tests the controller's ability to parse correct
 * commands and feed them to the model and viewer, and gracefully error on incorrect
 * commands with an explanatory message.
 */
public class TestMultiController {
  @Test
  public void testGoodController() throws FileNotFoundException {
    try {
      String expected = "";
      File file = new File(
            "calendar_project/res/commandsMultiCalendars.txt");
      MockCalendar model = new MockCalendar();
      IController controller =
            new MultiCalendarsController(new Scanner(file), model, new MockViewer());
      controller.begin();
      expected += "createCalendar(MyCal, Europe/London)\n";
      expected += "editCalendar(MyCal, NAME, SuperCoolCal)\n";
      expected += "editCalendar(SuperCoolCal, NAME, MyCal)\n";
      expected += "useCalendar(MyCal)\n";
      expected += "addEvent(party (public) from 2025-05-30T08:00 to 2025-05-30T17:00.)\n";
      expected += "addEvent(doctorappointment (public) from 2025-05-30T11:00 " +
            "to 2025-05-30T12:00.)\n";
      expected += "editEventStatus(doctorappointment, 2025-05-30T11:00, " +
            "2025-05-30T12:00, false)\n";
      expected += "makeEventSeriesUntilDate(work, [MONDAY, TUESDAY, WEDNESDAY, " +
            "THURSDAY, FRIDAY], 2025-01-01T09:00, 2025-01-01T17:00, 2025-12-30T23:59)\n";
      expected += "makeEventSeriesNTimes(dnd, [SUNDAY], 2025-01-01T16:00, 2025-01-01T20:00, 12)\n";
      expected += "editCalendar(MyCal, TIMEZONE, Europe/Moscow)\n";
      expected += "createCalendar(EastCoastCal, America/New_York)\n";
      expected += "copyEventsOnDay(2025-05-30, EastCoastCal, 2025-06-10)\n";
      expected += "getEvents(2025-05-30T00:00, 2025-05-30T23:59:59)\n";
      expected += "useCalendar(EastCoastCal)\n";
      expected += "makeEventSeriesUntilDate(class, [MONDAY, TUESDAY, WEDNESDAY, THURSDAY], " +
            "2025-05-01T08:00, 2025-05-01T17:00, 2025-07-01T23:59)\n";
      expected += "makeEventSeriesNTimes(tacotuesday, [TUESDAY], " +
            "2025-01-01T08:00, 2025-01-01T17:00, 100)\n";
      expected += "editSeriesLocation(tacotuesday, 2025-01-01T08:00, kitchen, true)\n";
      expected += "editEventDescription" +
            "(tacotuesday, 2025-05-27T08:00, 2025-05-27T17:00, fun!!)\n";
      expected += "editSeriesSubject(class, 2025-06-01T08:00, juneclasses, false)\n";
      expected += "getEvents(2025-05-20T08:00, 2025-06-10T23:00)\n";
      expected += "isEventAt(2025-05-10T12:00)\n";
      expected += "isEventAt(2025-05-10T01:00)\n";
      assertEquals(expected, model.toString());
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testBadController() throws FileNotFoundException {
    String expected = "";
    File file = new File(
          "calendar_project/res/commandsMultiCalendarsOneInvalid.txt");
    MockCalendar model = new MockCalendar();
    try {
      IController controller =
            new MultiCalendarsController(new Scanner(file), model, new MockViewer());
      controller.begin();
      fail("Should have thrown an exception");
    } catch (RuntimeException e) {
      assertEquals("Invalid command: bad arguments for edit", e.getMessage());
      expected += "createCalendar(MyCal, Europe/London)\n";
      expected += "editCalendar(MyCal, NAME, SuperCoolCal)\n";
      expected += "editCalendar(SuperCoolCal, NAME, MyCal)\n";
      expected += "useCalendar(MyCal)\n";
      expected += "addEvent(party (public) from 2025-05-30T08:00 to 2025-05-30T17:00.)\n";
      expected += "createCalendar(EastCoastCal, America/New_York)\n";
      assertEquals(expected, model.toString());
    }
  }

  @Test
  public void testBadWeekdays() throws FileNotFoundException {
    String expected = "";
    File file = new File(
          "calendar_project/res/badWeekdayMultiCommands.txt");
    MockCalendar model = new MockCalendar();
    try {
      IController controller =
            new MultiCalendarsController(new Scanner(file), model, new MockViewer());
      controller.begin();
      fail("Should have thrown an exception");
    } catch (RuntimeException e) {
      assertEquals("Invalid command: bad arguments for create", e.getMessage());
      expected += "createCalendar(MyCal, Europe/London)\n";
      expected += "editCalendar(MyCal, NAME, SuperCoolCal)\n";
      expected += "editCalendar(SuperCoolCal, NAME, MyCal)\n";
      expected += "useCalendar(MyCal)\n";
      assertEquals(expected, model.toString());
    }
  }
}
