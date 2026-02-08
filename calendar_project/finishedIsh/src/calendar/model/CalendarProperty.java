package calendar.model;

public enum CalendarProperty {
  NAME, TIMEZONE;

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
