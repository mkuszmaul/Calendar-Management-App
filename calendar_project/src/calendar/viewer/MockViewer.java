package calendar.viewer;

import calendar.model.IEvent;

import java.util.ArrayList;

/**
 * Mock implementation of the viewer class for testing. Contains shell methods
 * with minimal implementation.
 */
public class MockViewer implements ICalendarViewer {
  @Override
  public String showStatusIsBusy(boolean isBusy) {
    //shell method for testing
    return "";
  }

  @Override
  public String printListOfEvents(ArrayList<IEvent> listOfEvents) {
    //shell method for testing
    return "";
  }
}
