import calendar.controller.GUICalendarController;
import calendar.controller.GUICalendarControllerImpl;
import calendar.model.ISmartCalendarsModel;
import calendar.model.MockSmartCalendars;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Tests the GUI calendar controller's ability to set up a model
 * and call model methods given user input.
 */
public class TestGUIController {
  @Test
  public void testController() {
    String expected = "";
    ISmartCalendarsModel model = new MockSmartCalendars();
    GUICalendarController controller = new GUICalendarControllerImpl(model);
    expected += "createCalendar(Default Calendar, America/New_York)\n";
    expected += "useCalendar(Default Calendar)\n";
    controller.getCalendarNames();
    expected += "getCalendarNames()\n";
    LocalDateTime start = LocalDateTime.of(2025, 6, 15, 17, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 15, 21, 0);
    controller.createEvent("Board game night", start, end, "Let's play" +
          " some games!", "Jeff's house", true);
    expected += "addEvent(Board game night: Let's play some games! at Jeff's house (public) " +
          "from 2025-06-15T17:00 to 2025-06-15T21:00.)\n";
    controller.hasEventsOnDate(start.toLocalDate());
    expected += "getEvents(2025-06-15T00:00, 2025-06-15T23:59)\n";
    controller.switchCalendar("Default Calendar");
    expected += "useCalendar(Default Calendar)\n";

    assertEquals(expected, model.toString());
  }
}
