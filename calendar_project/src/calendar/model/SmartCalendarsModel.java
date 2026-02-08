package calendar.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implements a multiple-calendars model for a calendar app with additional helper
 * method(s) to support a GUI version of the calendar app.
 */
public class SmartCalendarsModel extends CalendarsModel implements ISmartCalendarsModel {

  @Override
  public List<String> getCalendarNames() {
    Map<String, INamedCalendar> calendars = super.getCalendars();
    return new ArrayList<>(calendars.keySet());
  }
}
