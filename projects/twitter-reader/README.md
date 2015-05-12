## Tap Twitter Stream ##
Twitter can be loved and hated, but one thing is obvious. It is an endless sourse of technical and business information.
Our current challenge is about how we could potentially build an application which uses Twitter to extract those invaluable business infos.

### The task ###
The task is to create a **web application** that, using **Twitter's API**, taps the **real-time stream of tweets** and filters for those which contains a user provided words. **Multiple users** could have different search criteria at the same time.
The app should display both **the stream of tweets** and the **statistics of hashTags** extracted from the tweets in a moving window fashion.

### The UI ###
There should be a simple **text field** where the user can define what tweets he is interested in. The default should be **Poker** which is sampled as the page is opened. There could be **multiple keywords**. When the user provides a new keyword the newly defined samples from Twitter should be displayed on the UI alongside with the hashTag statistics. 
Update the statistics as you update tweets.
Elements of the UI:
- Text field to be able to provide filter keyword
- Sampled Twitter data 
- hashTag statisctics. It is a simple list of hashTags and a number of occurances extracted from the tweets
The layout of the UX is up to you. Can be user friendly, funny, functional or just a dump page. Can use push or pull technologies. Use what ever you feel comfortable with and suitable for the purpose. Use your imagination!

### Delivery ###
As it should be a **running web application** I would recommend to pick a cloud provider where you could potentially **deploy your application** for DEMO purpose. The organisers definitelly don't want to run 100 different type of web applications locally. ;) It can be AWS, Heroku or any other cloud provider what you prefer.

### Hints ###
- Twitter provides Streaming APIs in many different languages. For general information about the Streaming API check https://dev.twitter.com/streaming/overview.
  For libraries check https://dev.twitter.com/overview/api/twitter-libraries.
- Twitter's Streaming API requires OAuth authentication. You need to
  - sign up for Twitter
  - create a Twitter application on https://apps.twitter.com
  to get OAuth credentials. Both are simple processes, taking approximately 1 minute each.
- Make sure that your credentials are not awailable for unauthorized person

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
