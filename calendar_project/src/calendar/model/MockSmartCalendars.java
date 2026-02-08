package calendar.model;

import java.util.List;

/**
 * A mock of SmartCalendarsModel for testing. Extends another mock model to
 * contain shell methods and a log for all model methods.
 */
public class MockSmartCalendars extends MockCalendar implements ISmartCalendarsModel {
  @Override
  public List<String> getCalendarNames() {
    this.log += "getCalendarNames()\n";
    return List.of();
  }
}
