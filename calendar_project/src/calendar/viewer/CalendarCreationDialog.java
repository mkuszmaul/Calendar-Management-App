package calendar.viewer;

import calendar.controller.GUICalendarController;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.TimeZone;

/**
 * Dialog for creating new calendars.
 * Similar to EventCreationDialog but for calendar creation.
 */
public class CalendarCreationDialog extends JDialog {
  private JTextField nameField;
  private JComboBox<String> timezoneCombo;
  private final GUICalendarController controller;
  private boolean calendarCreated = false;

  /**
   * Public constructor. Takes in frame and controller to call when given user input.
   * @param parent frame to work with
   * @param controller controller to call with user input
   */
  public CalendarCreationDialog(Frame parent, GUICalendarController controller) {
    super(parent, "Create New Calendar", true);
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

    //calendar Name
    gbc.gridx = 0;
    gbc.gridy = 0;
    mainPanel.add(new JLabel("Calendar Name:"), gbc);
    gbc.gridx = 1;
    nameField = new JTextField(20);
    mainPanel.add(nameField, gbc);

    //timezone
    gbc.gridx = 0;
    gbc.gridy = 1;
    mainPanel.add(new JLabel("Timezone:"), gbc);
    gbc.gridx = 1;
    timezoneCombo = new JComboBox<>(TimeZone.getAvailableIDs());
    timezoneCombo.setSelectedItem(TimeZone.getDefault().getID());
    mainPanel.add(timezoneCombo, gbc);

    add(mainPanel, BorderLayout.CENTER);

    //buttons
    JPanel buttonPanel = new JPanel();
    JButton createButton = new JButton("Create");
    JButton cancelButton = new JButton("Cancel");

    createButton.addActionListener(e -> {
      if (validateAndCreateCalendar()) {
        calendarCreated = true;
        dispose();
      }
    });
    cancelButton.addActionListener(e -> dispose());

    buttonPanel.add(createButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private boolean validateAndCreateCalendar() {
    String name = nameField.getText().trim();
    if (name.isEmpty()) {
      JOptionPane.showMessageDialog(this,
            "Please enter a name for the calendar.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      return false;
    }

    String timezoneId = (String) timezoneCombo.getSelectedItem();
    TimeZone timezone = TimeZone.getTimeZone(timezoneId);

    try {
      controller.createCalendar(name, timezone);
      JOptionPane.showMessageDialog(this,
            "Calendar '" + name + "' created successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
      return true;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
            "Error creating calendar: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  /**
   * Check if a calendar was successfully created.
   * @return true if calendar was created, false if cancelled
   */
  public boolean wasCalendarCreated() {
    return calendarCreated;
  }

  /**
   * Get the name of the created calendar.
   * @return the calendar name, or null if not created
   */
  public String getCreatedCalendarName() {
    return calendarCreated ? nameField.getText().trim() : null;
  }
}