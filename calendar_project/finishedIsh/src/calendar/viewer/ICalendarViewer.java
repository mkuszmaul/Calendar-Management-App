package calendar.viewer;

import calendar.model.IEvent;

import java.util.ArrayList;

/**
 * Represents a viewer for the calendar app. Shows the user busy status
 * and a list of events when prompted.
 */
public interface ICalendarViewer {
  /**
   * Shows the status BUSY if given boolean is true, and AVAILABLE
   * if given boolean is false. Shows user's availability at
   * a particular time.
   *
   * @param isBusy true if user is busy
   */
  public String showStatusIsBusy(boolean isBusy);

  /**
   * Prints out given list of events.
   *
   * @param listOfEvents arraylist of events
   */
  public String printListOfEvents(ArrayList<IEvent> listOfEvents);
}
