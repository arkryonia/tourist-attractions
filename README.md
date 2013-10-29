Tourist Attractions
===================

Tourist Attractions is a location-aware tour guide to city highlights. It 
presents the most notable sights nearby and shows detailed information on 
them. The application demonstrates the use of the Location API (JSR-179) 
and HERE Maps API for Java ME. The application has been updated to Nokia 
Asha software platform while still preserving compatibility with Series 40 
phones.

This example application demonstrates:
* Showing a map using HERE Maps API for Java ME.
* Showing the user's location on the map.
* Scrollable views using the Gesture API and the Frame Animator API.

This example application is hosted in GitHub:
* https://github.com/nokia-developer/tourist-attractions

For more information on the implementation, visit the wiki pages:
* http://developer.nokia.com/Resources/Library/Java/#!code-examples/location-services-touristattractions.html

What's new
----------
Version 1.6: UI fixes to the map UI.

1. Usage
-------------------------------------------------------------------------------

The application separates UI rendering and updating content to separate threads 
to make sure the events are handled smoothly. The rendering and location threads
are paused when the screensaver starts, there is an incoming call, or so on, to 
prevent excess power consumption.

2. Prerequisites
-------------------------------------------------------------------------------

* Java ME basics
* Java ME threads and timers


3. Project structure and implementation
-------------------------------------------------------------------------------

3.1 Folders
-----------

| Folder | Description |
| ----- | ----------- |
| root | The root folder contains the project file, resource files, the license information and this README file. |
| nbproject | Contains NetBeans project files. |
| res | Contains application graphics. |
| res\guides | Contains the default guides. |
| src | Contains the Java source code files. |
| web_guides | Contains guides that are on the web server. |  


3.2 Important files and classes
-------------------------------

| File | Description |
| ----- | ----------- |
| src\..\ViewMaster.java | The main views use ViewMaster canvas. |
| src\..\LocationFinder.java | Wraps Location API so that it is not necessary to run the application. |
| src\..\TouristMap.java | Implementation for HERE Maps API for Java ME. |
| src\..\Network.java | Network component handles all connections to the internet. |

3.3 Used APIs
-------------

* HERE Maps API for Java ME
* Location API (JSR-179)
* Location API extension LocationUtil
* Gesture API
* Frame Animator API
* Web Services API (JSR-172)


4. Compatibility
-------------------------------------------------------------------------------

Nokia Asha software platform 1.0 and Series 40 6th Edition or newer touch
phones.

Tested to work on Nokia Asha 501, Nokia Asha 311, Nokia Asha 308,
Nokia Asha 306, Nokia Asha 303, Nokia Asha 201 and Nokia X3-02.

Developed with Nokia Asha SDK 1.0 and Netbeans 7.0.1.

4.1 Required capabilities
-------------------------

CLDC 1.1, MIDP 2.0, and Web Services API (JSR-172).

4.2 Known issues
----------------

None.

5. Building, installing, and running the application
-------------------------------------------------------------------------------

5.1 Preparations
----------------

Before opening the project, make sure Nokia SDK 2.0 for Java or newer is installed and 
added to NetBeans. 

5.2 Building
------------

The project can be easily opened in NetBeans by selecting **Open Project** 
from the File menu and selecting the application. Before building the 
application, get your own app_id and token by registering at
https://api.developer.nokia.com/ovi-api/ui/registration and add them to the
initMap method in Main class. Building is done by selecting **Build main 
project**.

The project can also be built and run with Nokia IDE.

5.3 Nokia Asha and Series 40 phones
-----------------------------------

You can istall the application on a phone by transferring the JAR file 
via Nokia Suite or over Bluetooth.


6. License
-------------------------------------------------------------------------------

See the license text file delivered with this project. The license file is also
available online at
https://github.com/nokia-developer/tourist-attractions/blob/master/LICENCE.TXT


7. Related documentation
-------------------------------------------------------------------------------

Nokia SDK for Java
* http://www.developer.nokia.com/Develop/Java/Tools/

HERE Maps API for Java ME 
* http://www.developer.nokia.com/Resources/Library/HERE_Maps_Java_ME/


8. Version history
-------------------------------------------------------------------------------

* 1.6 UI fixes to map UI.
* 1.5 Removed IAP, since it is not supported anymore. Added a new Maps plugin.
* 1.4 Added pinch gesture support to map view; bug fixes.
* 1.3 Changed the IAP process.
* 1.2 Support for Series 40 full touch devices added.
* 1.1 First release published at developer.nokia.com.
* 1.0 Initial release in Nokia Developer projects.

