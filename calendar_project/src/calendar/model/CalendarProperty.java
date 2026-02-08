package calendar.model;

/**
 * An enumeration of modifiable properties of a calendar. Currently
 * limited to name and timezone, designed to allow for possible future updates.
 */
public enum CalendarProperty {
  NAME, TIMEZONE;

  /**
   * Converts string to calendar property.
   * @param string string to be converted to property
   * @return property, error if given invalid
   */
  public static CalendarProperty fromString(String string) {
    if (string.equalsIgnoreCase("name")) {
      return CalendarProperty.NAME;
    } else if (string.equalsIgnoreCase("timezone")) {
      return CalendarProperty.TIMEZONE;
    } else {
      throw new IllegalArgumentException("Invalid calendar property: " + string);
    }
  }
}
