package calendar.controller;


import calendar.model.Calendar;
import calendar.model.CalendarsModel;
import calendar.model.Event;
import calendar.model.ICalendarModel;
import calendar.viewer.ICalendarViewer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Controller for the calendar app. Takes in user (or file) inputs and parses
 * them as commands.
 */
public class CalendarController implements IController {
  Scanner inputScanner;
  ICalendarModel model;
  ICalendarViewer viewer;

  /**
   * Public constructor.
   *
   * @param inputScanner scanner from which to parse commands
   * @param calendar     model
   * @param viewer       viewer
   */
  public CalendarController(Scanner inputScanner, ICalendarModel calendar,
                            ICalendarViewer viewer) {
    this.model = calendar;
    this.viewer = viewer;
    this.inputScanner = inputScanner;
  }

  /**
   * Starts the program - asks the user for input and begins to parse it.
   */
  public void begin() {
    try {
      String command = inputScanner.nextLine();
      if (command.equalsIgnoreCase("exit")) {
        return;
      }
      this.parseCommand(command);
    } catch (NoSuchElementException e) {
      throw new RuntimeException("No exit command!");
    }
  }

  protected void parseCommand(String command) {
    Scanner commandScanner = new Scanner(command.toLowerCase());
    if (!commandScanner.hasNext()) {
      throw new RuntimeException("Invalid (empty) command");
    }
    String action = commandScanner.next();
    switch (action) {
      case "exit":
        commandScanner.close();
        inputScanner.close();
        return;
      case "create":
        this.create(commandScanner);
        this.begin();
        commandScanner.close();
        break;
      case "edit":
        this.edit(commandScanner);
        commandScanner.close();
        this.begin();
        break;
      case "print":
        this.print(commandScanner);
        commandScanner.close();
        this.begin();
        break;
      case "show":
        this.show(commandScanner);
        commandScanner.close();
        this.begin();
        break;
      default:
        throw new RuntimeException("Invalid command:" + command);
    }
  }

  protected void create(Scanner commandScanner) {
    try {
      this.assertNextWordIs(commandScanner, "event");
      String subject = commandScanner.next();
      String next = commandScanner.next().toLowerCase();
      if (next.equals("on")) {
        String dateString = commandScanner.next();
        LocalDate date = LocalDate.parse(dateString);
        if (!commandScanner.hasNext()) {
          this.model.addEvent(Event.getBuilder().subject(subject)
                .startDateTime(date.atTime(8, 0))
                .endDateTime(date.atTime(17, 0))
                .build());
          return;
        }
        this.makeRepeatEventHelper(commandScanner, subject, date.atTime(8, 0),
              date.atTime(5, 0));
      } else if (next.equals("from")) {
        String startDateTimeString = commandScanner.next();
        commandScanner.next();
        String endDateTimeString = commandScanner.next();
        if (!commandScanner.hasNext()) {
          this.model.addEvent(Event.getBuilder().subject(subject)
                .startDateTime(LocalDateTime.parse(startDateTimeString))
                .endDateTime(LocalDateTime.parse(endDateTimeString))
                .build());
        } else {
          this.makeRepeatEventHelper(commandScanner, subject,
                LocalDateTime.parse(startDateTimeString),
                LocalDateTime.parse(endDateTimeString));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for create");
    }
  }

  private void makeRepeatEventHelper(Scanner commandScanner, String subject,
                                     LocalDateTime start, LocalDateTime end) {
    this.assertNextWordIs(commandScanner, "repeats");
    ArrayList<DayOfWeek> weekDays = this.convertStringToWeekDayList(commandScanner.next());
    //should be weekdays e.g. MRU (monday, thursday, sunday)
    switch (commandScanner.next().toLowerCase()) {
      case "for":
        this.model.makeEventSeriesNTimes(subject, weekDays,
              start,
              end,
              commandScanner.nextInt());
        break;
      case "until":
        this.model.makeEventSeriesUntilDate(subject, weekDays,
              start,
              end,
              LocalDateTime.parse(commandScanner.next()));
        break;
      default:
        throw new RuntimeException("Invalid command: bad arguments for creating repeat event");
    }
  }

  private ArrayList<DayOfWeek> convertStringToWeekDayList(String str) {
    ArrayList<DayOfWeek> weekDays = new ArrayList<>();
    for (char c : str.toCharArray()) {
      switch (c) {
        case 'u':
          weekDays.add(DayOfWeek.SUNDAY);
          break;
        case 'm':
          weekDays.add(DayOfWeek.MONDAY);
          break;
        case 't':
          weekDays.add(DayOfWeek.TUESDAY);
          break;
        case 'w':
          weekDays.add(DayOfWeek.WEDNESDAY);
          break;
        case 'r':
          weekDays.add(DayOfWeek.THURSDAY);
          break;
        case 'f':
          weekDays.add(DayOfWeek.FRIDAY);
          break;
        case 's':
          weekDays.add(DayOfWeek.SATURDAY);
          break;
        default:
          throw new RuntimeException("Invalid weekday");
      }
    }
    return weekDays;
  }

  private void show(Scanner commandScanner) {
    try {
      this.assertNextWordIs(commandScanner, "status"); //status
      this.assertNextWordIs(commandScanner, "on");  //on
      LocalDateTime dateTime = LocalDateTime.parse(commandScanner.next());
      this.viewer.showStatusIsBusy(this.model.isEventAt(dateTime));
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for show");
    }
  }

  private void print(Scanner commandScanner) {
    try {
      this.assertNextWordIs(commandScanner, "events");
      switch (commandScanner.next().toLowerCase()) {
        case "on":
          LocalDate date = LocalDate.parse(commandScanner.next());
          viewer.printListOfEvents(model.getEvents(date.atStartOfDay(),
                date.atTime(23, 59, 59)));
          break;
        case "from":
          LocalDateTime startDateTime = LocalDateTime.parse(commandScanner.next());
          commandScanner.next();
          LocalDateTime endDateTime = LocalDateTime.parse(commandScanner.next());
          viewer.printListOfEvents(model.getEvents(startDateTime,
                endDateTime));
          break;
        default:
          throw new RuntimeException("Invalid command: bad arguments for print");
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for print");
    }
  }

  protected void edit(Scanner commandScanner) {
    try {
      switch (commandScanner.next().toLowerCase()) {
        case "event":
          this.editEventHelper(commandScanner);
          break;
        case "events":
          this.editSeriesHelper(commandScanner, false);
          break;
        case "series":
          this.editSeriesHelper(commandScanner, true);
          break;
        default:
          throw new RuntimeException("Invalid command: bad arguments for edit");
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid command: bad arguments for edit");
    }
  }

  private void editEventHelper(Scanner commandScanner) {
    String property = commandScanner.next();
    String subject = commandScanner.next();
    this.assertNextWordIs(commandScanner, "from");  //from
    LocalDateTime startDateTime = LocalDateTime.parse(commandScanner.next());
    this.assertNextWordIs(commandScanner, "to");  //to
    LocalDateTime endDateTime = LocalDateTime.parse(commandScanner.next());
    this.assertNextWordIs(commandScanner, "with");  //with
    switch (property) {
      case "subject":
        String newSubject = commandScanner.next();
        this.model.editEventSubject(subject, startDateTime, endDateTime, newSubject);
        break;
      case "start":
        LocalDateTime newStartDateTime = LocalDateTime.parse(commandScanner.next());
        this.model.editEventStart(subject, startDateTime, endDateTime, newStartDateTime);
        break;
      case "end":
        LocalDateTime newEndDateTime = LocalDateTime.parse(commandScanner.next());
        this.model.editEventEnd(subject, startDateTime, endDateTime, newEndDateTime);
        break;
      case "description":
        String newDescription = commandScanner.next();
        this.model.editEventDescription(subject, startDateTime, endDateTime, newDescription);
        break;
      case "location":
        String newLocation = commandScanner.next();
        this.model.editEventLocation(subject, startDateTime, endDateTime, newLocation);
        break;
      case "status":
        String newStatus = commandScanner.next();
        this.model.editEventStatus(subject, startDateTime, endDateTime, newStatus);
        break;
      default:
        throw new RuntimeException("Invalid command: bad arguments for edit");
    }
  }

  private void editSeriesHelper(Scanner commandScanner, boolean editWholeSeries) {
    String property = commandScanner.next();
    String subject = commandScanner.next();
    this.assertNextWordIs(commandScanner, "from");  //from
    LocalDateTime startDateTime = LocalDateTime.parse(commandScanner.next());
    this.assertNextWordIs(commandScanner, "with");  //with
    switch (property) {
      case "subject":
        String newSubject = commandScanner.next();
        this.model.editSeriesSubject(subject, startDateTime, newSubject, editWholeSeries);
        break;
      case "start":
        LocalDateTime newStartDateTime = LocalDateTime.parse(commandScanner.next());
        this.model.editSeriesStart(subject, startDateTime, newStartDateTime, editWholeSeries);
        break;
      case "end":
        LocalDateTime newEndDateTime = LocalDateTime.parse(commandScanner.next());
        this.model.editSeriesEnd(subject, startDateTime, newEndDateTime, editWholeSeries);
        break;
      case "description":
        String newDescription = commandScanner.next();
        this.model.editSeriesDescription(subject, startDateTime, newDescription,
              editWholeSeries);
        break;
      case "location":
        String newLocation = commandScanner.next();
        this.model.editSeriesLocation(subject, startDateTime, newLocation, editWholeSeries);
        break;
      case "status":
        String newStatus = commandScanner.next();
        this.model.editSeriesStatus(subject, startDateTime, newStatus, editWholeSeries);
        break;
      default:
        throw new RuntimeException("Invalid command: bad arguments for edit");
    }
  }

  protected void assertNextWordIs(Scanner scanner, String word) {
    String expected = scanner.next();
    if (!expected.equals(word)) {
      throw new RuntimeException("Invalid command, expected " + expected + " but got " + word);
    }
    return;
  }
}
