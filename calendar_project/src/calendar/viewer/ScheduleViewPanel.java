package calendar.viewer;

import calendar.controller.GUICalendarController;
import calendar.model.IEvent;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerDateModel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Font;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Date;

/**
 * Represents the schedule view portion of the display in GUI mode. Displays a list of up to 10
 * events starting from the given date.
 */
public class ScheduleViewPanel extends JPanel {
  private final GUICalendarController controller;
  private JPanel eventListPanel;
  private JSpinner dateSpinner;
  private LocalDate currentDate;
  private static final int MAX_EVENTS = 10;

  /**
   * Constructor. Takes in controller and initializes panel.
   * @param controller controller to call with requests for list of events
   */
  public ScheduleViewPanel(GUICalendarController controller) {
    this.controller = controller;
    this.currentDate = LocalDate.now();
    setupUI();
  }

  private void setupUI() {
    setLayout(new BorderLayout());

    //top panel with date selection
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.add(new JLabel("Schedule from:"));

    dateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner,
          "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    dateSpinner.setValue(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    dateSpinner.addChangeListener(e -> updateSchedule());
    topPanel.add(dateSpinner);

    add(topPanel, BorderLayout.NORTH);

    // Event list panel
    eventListPanel = new JPanel();
    eventListPanel.setLayout(new BoxLayout(eventListPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(eventListPanel);
    add(scrollPane, BorderLayout.CENTER);

    updateSchedule();
  }

  private void updateSchedule() {
    eventListPanel.removeAll();
    Date selectedDate = (Date) dateSpinner.getValue();
    currentDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    List<IEvent> events = controller.getEventsFromDate(currentDate);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    if (events.isEmpty()) {
      JLabel noEventsLabel = new JLabel("No events scheduled");
      noEventsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      eventListPanel.add(noEventsLabel);
    } else {
      int count = 0;
      for (IEvent event : events) {
        if (count >= MAX_EVENTS) {
          break;
        }

        JPanel eventPanel = new JPanel(new BorderLayout());
        eventPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        //event subject and time
        JLabel subjectLabel = new JLabel(event.getSubject());
        subjectLabel.setFont(subjectLabel.getFont().deriveFont(Font.BOLD));
        JLabel timeLabel = new JLabel(String.format("%s - %s",
              event.getStartDateTime().format(formatter),
              event.getEndDateTime().format(formatter)));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(subjectLabel, BorderLayout.WEST);
        topPanel.add(timeLabel, BorderLayout.EAST);
        eventPanel.add(topPanel, BorderLayout.NORTH);

        // Event details
        if (!event.getDescription().isEmpty() || !event.getLocation().isEmpty()) {
          JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
          if (!event.getDescription().isEmpty()) {
            detailsPanel.add(new JLabel("Description: " + event.getDescription()));
          }
          if (!event.getLocation().isEmpty()) {
            detailsPanel.add(new JLabel("Location: " + event.getLocation()));
          }
          eventPanel.add(detailsPanel, BorderLayout.CENTER);
        }

        eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventListPanel.add(eventPanel);
        eventListPanel.add(Box.createVerticalStrut(5));

        count++;
      }
    }

    eventListPanel.add(Box.createVerticalGlue());
    eventListPanel.revalidate();
    eventListPanel.repaint();
  }
}