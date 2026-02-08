package calendar.viewer;

import calendar.model.IEvent;

import java.util.ArrayList;

/**
 * A basic text viewer for the calendar app. Prints out text to the user
 * when prompted.
 */
public class CalendarViewer implements ICalendarViewer {

  @Override
  public String showStatusIsBusy(boolean isBusy) {
    String result = "";
    if (isBusy) {
      result += ("BUSY at given time!\n");
    } else {
      result += ("AVAILABLE at given time!\n");
    }
    return output(result);
  }

  @Override
  public String printListOfEvents(ArrayList<IEvent> listOfEvents) {
    String result = "";
    for (IEvent event : listOfEvents) {
      result += ("- " + event.toString() + "\n");
    }
    return output(result);
  }


  private String output(String output) {
    System.out.print(output);
    return output;
  }
}
