# Journey
**Journey** is a tool that encapsulates your entire travel experience. Before the trip, pin down your travel route, collect ideas for each leg and develop your personal intinerary. During the trip, reference your plans to make travel as easy as possible. After the trip, add photos and comments representative of your journey and share with friends. 

## User Stories

The following **required** functionality is completed:

* [ ] User can log into the app using a Google or Facebook account
* [ ] User can create a Journey with one or more destinations by selecting start and stop dates and typing in the names of desired cities
* [ ] Once journey is created, user is navigated to a *map-view* of the trip. Map view contains pins for each leg of the trip
* [ ] User can *flip* over the map view to see a calendar view of the trip. (Looks like google calendar month view)
* [ ] User can *zoom in* on the map view to allowe for more granular planning. This view shows one full day with all activites that have been added (Looks like a google calendar day view)
* [ ] Any changes made to one view are automatically shown in all others.
* [ ] Users can easily navigate from any one view to another
* [ ] User can add a leg to a trip by specifying dates.
* [ ] User can easily change the number of days allotted for each leg
* [ ] User can easily remove individual activities or entire legs of a trip.
* [ ] User can navigate to a general recommendation screen for each leg.
* [ ] User can add a new activity to the general recommendation page
* [ ] User can add activity to itinerary by clicking on it and selecting with day (or time) the activity will occur

The following **optional** features are implemented:

* [ ] User can register for a new account
* [ ] User can login to the app suing credentials specified during registration
* [ ] User can *flip-over* the *map-view* to see a zoomed in view of the map. Contains destinations for a single day in the journey.
* [ ] Each journey can have tags associated with it
* [ ] Each activity in the recommendation section can have the following fields:
  * [ ] Category = {Food, Drinks, Landmark, Park, Beach/Pool, Show, Other}
  * [ ] Location
  * [ ] Duration
  * [ ] Notes
* [ ] User can add photos/comments to each leg of the trip
* [ ] User can take a photo directly from the app

The following **bonus** features are implemented:

* [ ] User can add other users to the trip, allowing them to collaborate on the planning
* [ ] Listeners can specify if the journey length of flexible
  * [ ] If so, changing leg length will move the return date forward or backward
  * [ ] If not, a header will show how many days have not yet been allocated. Trying to add more days than are remaining is either forbidden or will turn something red to show that an “error” exists.
* [ ] User can search for other people's journeys and create a new journey based on a pre-existing one
* [ ] User can browse recommendations for the location by specifying the activity type that they are interested in adding
* [ ] User can download their trip to their phone and enter offline mode. This mode has all details saved but does not access the internet.
* [ ] User can share their experience at any time in popular social media. 
  * [ ] They can share before their journey (their route)
  * [ ] They can share during or after their journey (things they did, photos they've taken)


## Open-source libraries used

- [Parcel](https://github.com/johncarl81/parceler) - Parcels made easy
