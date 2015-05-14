 // Page elements
    var input = document.getElementById('input')
    var startBtn = document.getElementById('startBtn')
    var stopBtn = document.getElementById('stopBtn')
    var tweets = document.getElementById('tweets')
    var stats = document.getElementById('stats')

    var tweetBufferSize = 10
    var freshTweets = []

function addTweet(tweet) {
    if (freshTweets.length == tweetBufferSize) {
      for (var i = 0; i < freshTweets.length-1; i++) {
        freshTweets[i] = freshTweets[i+1]
      }
      freshTweets[tweetBufferSize-1] = tweet
    } else {
      freshTweets.push(tweet)
    }
}

function displayTweets(tweets) {
    var table = document.getElementById("tweetTable");
    var tableBody = document.createElement('TBODY');
    table.removeChild(table.lastChild)
    table.appendChild(tableBody);

    for (var i=tweets.length-1; i>=0; i--){
       var tr = document.createElement('TR');
       tableBody.appendChild(tr);

       var tdText = document.createElement('TD');
       tdText.width='1000';
       tdText.appendChild(document.createTextNode(tweets[i].text));
       tr.appendChild(tdText);

       var tdUser = document.createElement('TD');
       tdUser.width='250';
       tdUser.appendChild(document.createTextNode(tweets[i].user));
       tr.appendChild(tdUser);
    }
}

function displayStats(stats) {
    var table = document.getElementById("statsTable");
    var tableBody = document.createElement('TBODY');
    table.removeChild(table.lastChild)
    table.appendChild(tableBody);

    for (var word in stats) {
       if (stats.hasOwnProperty(word)) {
        var tr = document.createElement('TR');
        tableBody.appendChild(tr);

        var tdWord = document.createElement('TD');
        tdWord.width='100';
        tdWord.appendChild(document.createTextNode(word));
        tr.appendChild(tdWord);

        var tdOccurrence = document.createElement('TD');
        tdOccurrence.width='150';
        tdOccurrence.appendChild(document.createTextNode(stats[word]));
        tr.appendChild(tdOccurrence);
        }
    }
}

function output(style, text){
    messages.innerHTML += "<br/><span class='" + style + "'>" + text + "</span>"
}
    // Our websocket
    var socket = createSocket()

        // Send
        startBtn.onclick = function(e) {
            if (socket == undefined) {
                output("error", 'Not connected');
                return;
            }
            var text = document.getElementById("input").value;
            socket.send(text);
            output("sent", ">>> " + text);
        }
/*
        // Close
        stopBtn.onclick = function(e) {
            if (socket == undefined) {
                output('error', 'Not connected');
                return;
            }
            socket.close(1000, "Close button clicked");
        }

*/


function createSocket() {

    var uri = "ws://localhost:8080"
    var socket = new WebSocket(uri)

    socket.onerror = function(error) {
        output("error", error)
    }

    socket.onopen = function(event) {
        output("opened", "Connected to " + event.currentTarget.url)
    }

    socket.onmessage = function(event) {
        var message = JSON.parse(event.data);
        var stats = message.stats
        var tweet = message.tweet
        addTweet(tweet)
        displayTweets(freshTweets)
        displayStats(stats)
    }

    socket.onclose = function(event) {
        output("closed", "Disconnected: " + event.code + " " + event.reason)
        socket = undefined
    }

    return socket
}


