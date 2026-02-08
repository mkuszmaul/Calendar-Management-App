package calendar.viewer;

import calendar.controller.GUICalendarController;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerDateModel;
import javax.swing.JScrollPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * A dialog for event creation. Allows user to enter details for event and calls the controller
 * with them.
 */
public class EventCreationDialog extends JDialog {
  private JTextField subjectField;
  private JSpinner startDateSpinner;
  private JSpinner startTimeSpinner;
  private JSpinner endDateSpinner;
  private JSpinner endTimeSpinner;
  private JTextArea descriptionArea;
  private JTextField locationField;
  private JCheckBox isPublicCheckBox;
  private final LocalDate selectedDate;
  private final GUICalendarController controller;

  /**
   * Public constructor. takes in parent frame, date on which to start the event, and controller
   * to call with user input.
   * @param parent frame
   * @param date date on which event starts (by default)
   * @param controller controller to call given user input
   */
  public EventCreationDialog(Frame parent, LocalDate date, GUICalendarController controller) {
    super(parent, "Create New Event", true);
    this.selectedDate = date;
    this.controller = controller;
    setupUI();
    pack();
    setLocationRelativeTo(parent);
  }

  private void setupUI() {
    setLayout(new BorderLayout());
    JPanel mainPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    //subject
    gbc.gridx = 0;
    gbc.gridy = 0;
    mainPanel.add(new JLabel("Subject:"), gbc);
    gbc.gridx = 1;
    subjectField = new JTextField(20);
    mainPanel.add(subjectField, gbc);

    //start date
    gbc.gridx = 0;
    gbc.gridy = 1;
    mainPanel.add(new JLabel("Start Date:"), gbc);
    gbc.gridx = 1;
    startDateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(startDateSpinner,
          "yyyy-MM-dd");
    startDateSpinner.setEditor(dateEditor);
    startDateSpinner.setValue(Date.from(selectedDate.atStartOfDay(
          ZoneId.systemDefault()).toInstant()));
    mainPanel.add(startDateSpinner, gbc);

    //start time
    gbc.gridx = 0;
    gbc.gridy = 2;
    mainPanel.add(new JLabel("Start Time:"), gbc);
    gbc.gridx = 1;
    startTimeSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(startTimeSpinner,
          "HH:mm");
    startTimeSpinner.setEditor(timeEditor);
    startTimeSpinner.setValue(Date.from(LocalTime.now().atDate(LocalDate.now())
          .atZone(ZoneId.systemDefault()).toInstant()));
    mainPanel.add(startTimeSpinner, gbc);

    //end date
    gbc.gridx = 0;
    gbc.gridy = 3;
    mainPanel.add(new JLabel("End Date:"), gbc);
    gbc.gridx = 1;
    endDateSpinner = new JSpinner(new SpinnerDateModel());
    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner,
          "yyyy-MM-dd"));
    endDateSpinner.setValue(Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault())
          .toInstant()));
    mainPanel.add(endDateSpinner, gbc);

    //end time
    gbc.gridx = 0;
    gbc.gridy = 4;
    mainPanel.add(new JLabel("End Time:"), gbc);
    gbc.gridx = 1;
    endTimeSpinner = new JSpinner(new SpinnerDateModel());
    endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
    endTimeSpinner.setValue(Date.from(LocalTime.now().plusHours(1)
          .atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()));
    mainPanel.add(endTimeSpinner, gbc);

    //description
    gbc.gridx = 0;
    gbc.gridy = 5;
    mainPanel.add(new JLabel("Description:"), gbc);
    gbc.gridx = 1;
    descriptionArea = new JTextArea(3, 20);
    descriptionArea.setLineWrap(true);
    mainPanel.add(new JScrollPane(descriptionArea), gbc);

    //location
    gbc.gridx = 0;
    gbc.gridy = 6;
    mainPanel.add(new JLabel("Location:"), gbc);
    gbc.gridx = 1;
    locationField = new JTextField(20);
    mainPanel.add(locationField, gbc);

    //public/private
    gbc.gridx = 0;
    gbc.gridy = 7;
    mainPanel.add(new JLabel("Public Event:"), gbc);
    gbc.gridx = 1;
    isPublicCheckBox = new JCheckBox();
    mainPanel.add(isPublicCheckBox, gbc);

    add(mainPanel, BorderLayout.CENTER);

    //buttons
    JPanel buttonPanel = new JPanel();
    JButton createButton = new JButton("Create");
    JButton cancelButton = new JButton("Cancel");

    createButton.addActionListener(e -> {
      if (validateAndCreateEvent()) {
        dispose();
      }
    });
    cancelButton.addActionListener(e -> dispose());

    buttonPanel.add(createButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private boolean validateAndCreateEvent() {
    String subject = subjectField.getText().trim();
    if (subject.isEmpty()) {
      JOptionPane.showMessageDialog(this,
            "Please enter a subject for the event.");
      return false;
    }

    //convert Date objects to LocalDateTime
    Date startDate = (Date) startDateSpinner.getValue();
    Date startTime = (Date) startTimeSpinner.getValue();
    Date endDate = (Date) endDateSpinner.getValue();
    Date endTime = (Date) endTimeSpinner.getValue();

    LocalDateTime startDateTime = LocalDateTime.of(
          startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
          startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
    );

    LocalDateTime endDateTime = LocalDateTime.of(
          endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
          endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
    );

    if (endDateTime.isBefore(startDateTime)) {
      JOptionPane.showMessageDialog(this, "End time must be after " +
            "start time.");
      return false;
    }

    try {
      controller.createEvent(
            subject,
            startDateTime,
            endDateTime,
            descriptionArea.getText().trim(),
            locationField.getText().trim(),
            isPublicCheckBox.isSelected()
      );
      return true;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error creating event: "
            + e.getMessage());
      return false;
    }
  }
}