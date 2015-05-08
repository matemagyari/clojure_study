## Tap Twitter Stream ##
Twitter can be loved and hated, but one thing is obvious. It is an endless sourse of technical and business information.
Our current challenge is about how we could potentially build an application which uses Twitter to extract those invaluable business infos.

### The task ###
The task is to create a web application that, using Twitter's API, taps the real-time stream of tweets and filters for those which contains a user provided words.
The app should display both the stream of tweets and the up-to-date relative frequencies of occurrences of the aforementioned words.

### The UI ###
There should be a simple text field where the user can define what tweets he is interested in. The default should be **Gamesys** which is sampled as the page is opened. There could be multiple keywords. As soon as the user provides a new one the newly defined samples from Twitter should be displayed on the UI alongside with the word statistics. 
Update the statistics as you update tweets.
The UX is up to you. Can be user friendly or just a dump page. Can use push or pull technologies. Use what ever you feel comfortable with and suitable for the purpose. Use your imagination!

### Delivery ###
As it should be a running web application I would recommend to pick a cloud provider where you could potentially deploy your application for DEMO purpose. The organisers definitelly don't want to run 100 different type of web applications locally. ;) It can be AWS, Heroku or any other cloud provider what you prefer.

### Hints ###
- Twitter provides Streaming APIs in many different languages. For general information about the Streaming API check https://dev.twitter.com/streaming/overview.
  For libraries check https://dev.twitter.com/overview/api/twitter-libraries.
- Twitter's Streaming API requires OAuth authentication. You need to
  - sign up for Twitter
  - create a Twitter application on https://apps.twitter.com
  to get OAuth credentials. Both are simple processes, taking approximately 1 minute each.
- Tweets arrive in json format, usually each in one chunk. Sometimes though a tweet is fragmented over multiple chunks, each containing only a part of the valid json document. You can create a buffer to deal with the situation, but in the first version I'd advise you to simply ignore these "bad" chunks.

### Potential rating ideas ###
- Best UX
- Most User friendly UX
- Performant solution
- Scalable solution
- Promissing technology
- Longest solution
- Shortest Solution
- Original Idea
- Exotic solution
- I just like it most!
- Definitely not for me!
