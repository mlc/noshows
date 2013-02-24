Meetup Attendance Taker
=======================

This is an small app to allow Meetup Organizers to take attendance at their Meetups. It may also be interesting as a sample of accessing the [Meetup API][] in an an Android app.

Building
--------

 * Make sure you have the [Android SDK][] (including the files to target API 17) and [Apache Maven][] (version 3.x) installed.
 * [Register][consumer] an OAuth Consumer on Meetup's website, and note the key and secret you are given.
 * Copy `secrets.properties.template` to `secrets.properties` and fill in the key you were given in the step above.
 * Run `mvn package`.

[Meetup API]: http://www.meetup.com/meetup_api/
[Android SDK]: https://developer.android.com/sdk/index.html
[Apache Maven]: https://maven.apache.org/
[consumer]: http://www.meetup.com/meetup_api/oauth_consumers/