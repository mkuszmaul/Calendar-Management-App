To run this program, run the jar file Calendar_Project.jar with one of the following three arguments:
  
- --mode headless path-of-script-file (Program will run commands from script file, then end)
  
- --mode interactive (Program will take commands directly from the terminal, line by line.)

- No arguments: Program will open a GUI from which you may create events and calendars.

For instructions on the GUI mode, see USEME.md.

Sameera largely worked on the viewer, while Maggie largely worked on the model and controller.

Changelog:
- Added CalendarUI, CalendarCreationDialog, EventCreationDialog, and ScheduleViewPanel classes
  to the viewer. These classes work together to form a GUI, take input from the user,
  and send it the controller.
- Added GUICalendarController interface and GUICalendarControllerImpl class to the controller. This
  interface and implementation takes input given by the viewer and calls the appropriate method
  in the model.
- Added ISmartCalendarsModel and SmartCalendarsModel to the model. This interface and implementation
  extends the previous CalendarsModel and adds a helper method for retrieving lists of calendar
  names.
- Modified CalendarApp so that when called with no arguments, the main method calls CalendarUI to
  initiate the GUI mode of the app.
- Added mock class MockSmartCalendars in the model for testing. Added testing classes
  TestGUIController and TestSmartCalendars.

Instructions for headless and interactive mode:

Headless and interactive mode allow the user to create and edit events, event series,
and calendars. Included are several text files with example commands. 
The text file commandsMultiCalendars.txt is the one that will run without errors on the current,
multi-calendar version of the app.

The other text files exist mainly for testing. The text files badWeekdayMultiCommands.txt and 
commandsMultiCalendarsOneInvalid.txt are files that will produce errors on the current version of
the app. The file workingCommands.txt will run without errors on the previous, single-calendar 
version of the app, and the files commandsWithOneInvalid.txt and commandsWithNoExit.txt will
deliberately produce errors on the previous version of the app.

Working features: All features specified by the assignment are included and working. The user can
create and edit calendars, entering the arguments for these commands in either order. The user must
select a calendar with the use command before they are able to create and edit events.

Once a calendar is selected, the user can create, modify, and query events. The user can create
events either alone or as a series set to repeat on weekdays, and the series can be created either
until a certain date or set to repeat a specific number of times.

When creating events, the user must specify a subject and date. If no time is selected, the event
will automatically take place from 8am to 5pm on that date. The user may also create events from one
date-time to another, and an event may span multiple days.

Once an event is created, the user can edit its properties, including its subject, description,
location, start time, end time, and public status. Events are public by default, but can be edited
to be private. To edit an event’s public status, edit its status to “true” to make an event public
and “false” to make an event not public.

Users can print all events on a given date or between two given times. Users can also query whether
there are any events occurring at a given time.

Once events are created, they can be printed or copied from one calendar to a particular date or
time on another. Users can copy particular events, or all the events on a given date or between two
given times. When copied, events will automatically be converted to the target calendar’s timezone.


