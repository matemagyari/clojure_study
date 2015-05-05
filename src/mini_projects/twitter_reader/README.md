Tap Twitter Stream

The task is to create an application that, using Twitter's Streaming API, taps the real-time stream of tweets and filters for those which contain any of the words "virgin", "poker" or "gamesys".
The app should display both the stream of tweets and the up-to-date relative frequencies of occurrences of the aforementioned words.

Comments

* Twitter provides Streaming APIs in many different languages. For general information about the Streaming API check https://dev.twitter.com/streaming/overview.
  For libraries check https://dev.twitter.com/overview/api/twitter-libraries.
* Twitter's Streaming API requires OAuth authentication. You need to
   a) sign up for Twitter
   b) create a Twitter application on https://apps.twitter.com
   to get OAuth credentials. Both are simple processes, taking approximately 1 minute each.
* Tweets arrive in json format, usually each in one chunk. Sometimes though a tweet is fragmented over multiple chunks, each containing only a part of the
valid json document. You can create a buffer to deal with the situation, but in the first version I'd advise you to simply ignore these "bad" chunks.

Hints
* Your application will - regardless whether your code structure mirrors it - have 3 logical components. One taps the Twitter stream, the other
maintains the statistics and the last caters for the display. You might want to add a 4th component as well to provide controls to the user to
start and stop the stream-processing or for specifying customs search expressions.
