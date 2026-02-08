package calendar.controller;

import calendar.model.CalendarsModel;
import calendar.model.ICalendarsModel;
import calendar.viewer.CalendarViewer;
import calendar.viewer.ICalendarViewer;

import java.io.File;
import java.util.Scanner;


/**
 * Runs the program, a calendar app on which the user can create and edit
 * events. Initializes a controller, model, and viewer and starts the controller.
 */
public class CalendarApp {


  /**
   * Initializes the controller, model, and viewer and starts the controller.
   * Sets the mode.
   * ed
   *
   * @param args arguments from the user (e.g. --mode headless)
   */
  public static void main(String[] args) {
    Scanner mode = new Scanner(System.in);
    ICalendarViewer viewer = new CalendarViewer();
    ICalendarsModel model = new CalendarsModel();
    if (args.length >= 2 && args[0].equals("--mode")
          && args[1].equals("headless")) {
      try {
        File file = new File(args[2]);
        mode = new Scanner(file);
      } catch (Exception e) {
        throw new RuntimeException("Error reading mode", e);
      }
    }
    new MultiCalendarsController(mode, model, viewer).begin();
  }
}
