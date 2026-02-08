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
      expected += "editCalendar(MyCal, TIMEZONE, Russia/Moscow)\n";
      expected += "createCalendar(EastCoastCal, America/New_York)\n";
      expected += "copyEventsOnDay(2025-05-30, EastCoastCal, 2025-06-10)\n";
      expected += "getEvents(2025-05-30T00:00, 2025-05-30T23:59:59)\n";
      expected += "useCalendar(EastCoastCal)\n";
      expected += "getEvents(2025-06-10T00:00, 2025-06-10T23:59:59)\n";
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
