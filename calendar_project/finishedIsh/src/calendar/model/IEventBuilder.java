package calendar.model;

import java.time.LocalDateTime;

/**
 * Represents a builder class for an event. Allows events to be built
 * with a variable number of fields initialized.
 */
public interface IEventBuilder {
  /**
   * Assigns subject to IEventBuilder.
   *
   * @param subject subject of event.
   * @return updated IEventBuilder.
   */
  public IEventBuilder subject(String subject);

  /**
   * Assigns start time to IEventBuilder.
   *
   * @param startDateTime start time of event.
   * @return updated IEventBuilder.
   */
  public IEventBuilder startDateTime(LocalDateTime startDateTime);

  /**
   * Assigns end time to IEventBuilder.
   *
   * @param endDateTime end time of event.
   * @return updated IEventBuilder.
   */
  public IEventBuilder endDateTime(LocalDateTime endDateTime);


  /**
   * Assigns description to IEventBuilder.
   *
   * @param description description of event
   * @return updated IEventBuilder.
   */
  public IEventBuilder description(String description);

  /**
   * Assigns location to IEventBuilder.
   *
   * @param location location of event
   * @return updated IEventBuilder.
   */
  public IEventBuilder location(String location);

  /**
   * Assigns public/private status to IEventBuilder.
   *
   * @param isPublic true if event is public.
   * @return updated IEventBuilder.
   */
  public IEventBuilder isPublic(boolean isPublic);

  /**
   * Creates IEvent from the fields that have been set so far.
   *
   * @return IEvent
   */
  public IEvent build();
}
