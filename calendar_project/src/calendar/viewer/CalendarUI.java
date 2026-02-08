package calendar.viewer;

import calendar.controller.GUICalendarController;
import calendar.controller.GUICalendarControllerImpl;
import calendar.model.ISmartCalendarsModel;
import calendar.model.SmartCalendarsModel;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * A GUI for the calendar app. Handles displaying all output to user and calling controller
 * with user input.
 */
public class CalendarUI {
  private JFrame frame;
  private JPanel calendarPanel;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;
  private GUICalendarController controller;
  private YearMonth currentMonth;
  private String selectedCalendar;

  /**
   * Public constructor. Initializes fields and begins displaying the app.
   * @param controller controller to call with user input
   */
  public CalendarUI(GUICalendarController controller) {
    this.controller = controller;
    frame = new JFrame("Calendar App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 600);
    frame.setLayout(new BorderLayout());

    currentMonth = YearMonth.now();
    selectedCalendar = "Work";

    //create main content panel with calendar and schedule
    JPanel mainContentPanel = new JPanel(new GridLayout(1, 2));

    //left panel for calendar
    JPanel calendarContainer = new JPanel(new BorderLayout());
    JPanel topPanel = new JPanel();
    JButton prevButton = new JButton("<");
    JButton nextButton = new JButton(">");
    monthLabel = new JLabel();
    calendarDropdown = new JComboBox<>(controller.getCalendarNames().toArray(new String[0]));

    //add create calendar button
    JButton createCalendarButton = new JButton("+");
    createCalendarButton.setToolTipText("Create New Calendar");

    topPanel.add(prevButton);
    topPanel.add(monthLabel);
    topPanel.add(nextButton);
    topPanel.add(calendarDropdown);
    topPanel.add(createCalendarButton);
    calendarContainer.add(topPanel, BorderLayout.NORTH);

    calendarPanel = new JPanel();
    calendarContainer.add(calendarPanel, BorderLayout.CENTER);

    //right panel for schedule view
    ScheduleViewPanel scheduleViewPanel = new ScheduleViewPanel(controller);

    mainContentPanel.add(calendarContainer);
    mainContentPanel.add(scheduleViewPanel);

    frame.add(mainContentPanel, BorderLayout.CENTER);

    prevButton.addActionListener(e -> changeMonth(-1));
    nextButton.addActionListener(e -> changeMonth(1));
    calendarDropdown.addActionListener(e -> changeCalendar());
    createCalendarButton.addActionListener(e -> showCreateCalendarDialog());

    updateCalendar();
    frame.setVisible(true);
  }

  private void updateCalendar() {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7));
    monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
    calendarPanel.setBackground(Color.WHITE);

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));

      //check if there are events on this day
      if (controller.hasEventsOnDate(date)) {
        dayButton.setBackground(Color.YELLOW);
      }

      dayButton.addActionListener(e -> showEvents(date));
      calendarPanel.add(dayButton);
    }

    frame.revalidate();
    frame.repaint();
  }

  private void changeMonth(int offset) {
    currentMonth = currentMonth.plusMonths(offset);
    updateCalendar();
  }

  private void changeCalendar() {
    selectedCalendar = (String) calendarDropdown.getSelectedItem();
    controller.switchCalendar(selectedCalendar);
    updateCalendar();
  }

  private void showEvents(LocalDate date) {
    EventCreationDialog dialog = new EventCreationDialog(frame, date, controller);
    dialog.setVisible(true);
    updateCalendar();
  }

  private void showCreateCalendarDialog() {
    CalendarCreationDialog dialog = new CalendarCreationDialog(frame, controller);
    dialog.setVisible(true);

    //if calendar was created, update the dropdown and switch to it
    if (dialog.wasCalendarCreated()) {
      String newCalendarName = dialog.getCreatedCalendarName();
      //update dropdown with new calendar
      calendarDropdown.addItem(newCalendarName);
      //switch to new calendar
      calendarDropdown.setSelectedItem(newCalendarName);
      changeCalendar();
    }
  }

  /**
   * Begins GUI mode. Creates instance of self with controller and model and begins displaying
   * the app.
   */
  public static void beginUI() {
    SwingUtilities.invokeLater(() -> {
      ISmartCalendarsModel model = new SmartCalendarsModel();
      GUICalendarController controller = new GUICalendarControllerImpl(model);
      new CalendarUI(controller);
    });
  }
}