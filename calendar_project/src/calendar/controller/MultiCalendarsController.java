package calendar.controller;

import calendar.model.CalendarProperty;
import calendar.model.ICalendarsModel;
import calendar.viewer.ICalendarViewer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * A controller for a calendar app that can support multiple calendars with
 * different timezones. Reuses most of its code from CalendarController, with
 * an additional field and methods for handling operations on multiple calendars.
 */
public class MultiCalendarsController extends CalendarController {
  ICalendarsModel multiModel; //a model that supports multiple calendars.
  //When the super class is initialized in the constructor, its model points
  //to the same reference as multiModel points to, so that mutation of either
  //will change both. This allows the super class to continue to function
  //with the older version of the calendar if desired, while this subclass
  //is able to support the newer version while reusing much of the superclass's code.

  /**
   * Public constructor.
   * @param inputScanner input to scan
   * @param model model to delegate to
   * @param viewer viewer to delegate to
   */
  public MultiCalendarsController(Scanner inputScanner, ICalendarsModel model,
                                  ICalendarViewer viewer) {
    super(inputScanner, model, viewer);
    this.multiModel = model;
  }

  @Override
  protected void parseCommand(String command) {
    Scanner commandScanner = new Scanner(command);
    if (!commandScanner.hasNext()) {
      throw new RuntimeException("Invalid (empty) command");
    }
    String action = commandScanner.next().toLowerCase();
    switch (action) {
      case "create":
        this.create(commandScanner);
        this.begin();
        break;
      case "edit":
        this.edit(commandScanner);
        this.begin();
        break;
      case "use":
        this.use(commandScanner);
        this.begin();
        break;
      case "copy":
        this.copy(commandScanner);
        this.begin();
        break;
      default:
        super.parseCommand(command);
    }
  }

  @Override
  protected void create(Scanner commandScanner) {
    try {
      String createObject = commandScanner.next();
      if (createObject.equals("calendar")) {
        String firstArg = commandScanner.next(); //should be "--name"
        String calendarName;
        ZoneId zoneId;
        if (firstArg.equals("--name")) {
          calendarName = commandScanner.next();
          this.assertNextWordIs(commandScanner, "--timezone"); //should be "--timezone"
          zoneId = ZoneId.of(commandScanner.next());
        } else if (firstArg.equals("--timezone")) {
          zoneId = ZoneId.of(commandScanner.next());
          this.assertNextWordIs(commandScanner, "--name");
          calendarName = commandScanner.next();
        } else {
          throw new RuntimeException();
        }
        multiModel.createCalendar(calendarName, TimeZone.getTimeZone(zoneId));
      } else {
        super.create(new Scanner((createObject + commandScanner.nextLine()).toLowerCase()));
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage()); //"Invalid command: bad arguments for create");
    }
  }

  @Override
  protected void edit(Scanner commandScanner) {
    try {
      String editObject = commandScanner.next();
      if (editObject.equalsIgnoreCase("calendar")) {
        String firstArg = commandScanner.next();
        String calendarName;
        CalendarProperty property;
        Object value;
        if (firstArg.equalsIgnoreCase("--name")) {
          calendarName = commandScanner.next();
          assertNextWordIs(commandScanner, "--property");
          property = CalendarProperty.fromString(commandScanner.next());
          value = commandScanner.next();
        } else if (firstArg.equalsIgnoreCase("--property")) {
          property = CalendarProperty.fromString(commandScanner.next().toLowerCase());
          if (property.equals(CalendarProperty.TIMEZONE)) {
            value = TimeZone.getTimeZone(commandScanner.next());
          } else {
            value = commandScanner.next();
          }
          assertNextWordIs(commandScanner, "--name");
          calendarName = commandScanner.next();
        } else {
          throw new RuntimeException();
        }
        multiModel.editCalendar(calendarName, property, value);
      } else {
        super.edit(new Scanner((editObject + commandScanner.nextLine()).toLowerCase()));
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for edit");
    }
  }

  private void use(Scanner commandScanner) {
    try {
      assertNextWordIs(commandScanner, "calendar"); //should be "calendar"
      assertNextWordIs(commandScanner, "--name"); //should be "--name"
      String calendarName = commandScanner.next();
      this.multiModel.useCalendar(calendarName);
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for use");
    }
  }

  private void copy(Scanner commandScanner) {
    try {
      switch (commandScanner.next()) {
        case "event":
          String eventName = commandScanner.next();
          assertNextWordIs(commandScanner, "on"); //should be "on"
          LocalDateTime originalStart = LocalDateTime.parse(commandScanner.next());
          assertNextWordIs(commandScanner, "--target"); //should be "--target"
          String newCalendarName = commandScanner.next();
          LocalDateTime newStart = LocalDateTime.parse(commandScanner.next());
          this.multiModel.copyEventCalendar(eventName, originalStart,
                newCalendarName, newStart);
          break;
        case "events":
          this.copyEventsHelper(commandScanner);
          break;
        default:
          throw new RuntimeException("Invalid command: bad arguments for copy");
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for copy");
    }
  }

  private void copyEventsHelper(Scanner commandScanner) {
    try {
      if (commandScanner.next().equals("on")) {
        LocalDate originalDate = LocalDate.parse(commandScanner.next());
        assertNextWordIs(commandScanner, "--target"); //should be "--target"
        String newCalendarName = commandScanner.next();
        assertNextWordIs(commandScanner, "to"); //should be "to"
        LocalDate toDate = LocalDate.parse(commandScanner.next());
        this.multiModel.copyEventsOnDay(originalDate, newCalendarName, toDate);
      } else if (commandScanner.next().equals("between")) {
        LocalDate startDate = LocalDate.parse(commandScanner.next());
        assertNextWordIs(commandScanner, "and"); //should be "and"
        LocalDate endDate = LocalDate.parse(commandScanner.next());
        assertNextWordIs(commandScanner, "--target"); //should be "--target"
        String newCalendarName = commandScanner.next();
        assertNextWordIs(commandScanner, "to"); //should be "to"
        LocalDate toDate = LocalDate.parse(commandScanner.next());
        this.multiModel.copyEventsBetween(startDate, endDate, newCalendarName, toDate);
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for copy");
    }
  }
}
