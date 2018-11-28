var stompClient = null;
var runTimes = [];
var receivedMessages = 0;
var trialNumber = 1;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (greeting) {
            var now = Date.now(),
                times = greeting.body.split('/'),
                pubProxTime = parseInt(times[1]) - parseInt(times[0]),
                proxUITime = parseInt(now) - parseInt(times[1]),
                runtime = pubProxTime + '/' + proxUITime;
            showGreeting(runtime);
            console.log("received message");
            receivedMessages++;
        });
        stompClient.send("/app/connect");
    });
}

function sendName () {
    var interval,
        i = 0;

    interval = setInterval(function(){
        i++;
        if (i > 1000) {
            i = 0;
            window.clearInterval(interval);
        } else {
            stompClient.send("/app/send", {}, JSON.stringify({'name': $("#name").val() + ': #' + i}));
            console.log('Sent message ' + 'Hello World!' + ' #' + i );
        }
    }, 10)
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGreeting(message) {
    // $("#greetings").append("<tr><td>" + message + "</td></tr>");
    //console.log("Time :" + message);
    runTimes.push(message);
}

function analyzeRuntime (){
    var pubToProxArray = [],
        proxToUIArray = [],
        meanpubToProx,
        meanproxToUI,
        medianpubtoProx,
        medianproxToUI,
        modepubtoProx,
        modeproxToUI,
        maxpubtoProx,
        maxproxToUI,
        minpubtoProx,
        minproxToUI,
        stdevpubtoProx,
        stdevproxToUI;

    function TimeRow(number, pubToProx, proxToUI){
        this.MessageNum = 'Message ' + number;
        this.PubToProx = parseInt(pubToProx);
        this.ProxToUI = parseInt(proxToUI);
    }

    var parsedTimes = runTimes.map((item, index)=> {
        var timeArray = item.split('/');
        return new TimeRow(index + 1, timeArray[0], timeArray[1]);
    });
    
    console.table(parsedTimes);

    parsedTimes.forEach((item) => {
        pubToProxArray.push(item.PubToProx);
        proxToUIArray.push(item.ProxToUI);
    });

    meanpubToProx = math.mean(...pubToProxArray);
    medianpubtoProx = math.median(...pubToProxArray);
    modepubtoProx = math.mode(...pubToProxArray);
    maxpubtoProx = math.max(...pubToProxArray);
    minpubtoProx = math.min(...pubToProxArray);
    stdevpubtoProx = math.std(...pubToProxArray);

    console.log("Received messages from server: ", receivedMessages);

    console.log(`Statistical analyses Solace --> Proxy (ms) - 
            \nMissing: ${1000 - parsedTimes.length}
            \nMin: ${minpubtoProx} \nMax: ${maxpubtoProx} 
            \nMode: ${modepubtoProx} \nMedian: ${medianpubtoProx} 
            \nMean: ${meanpubToProx} \nSTDev: ${stdevpubtoProx}`);

    meanproxToUI = math.mean(...proxToUIArray);
    medianproxToUI = math.median(...proxToUIArray);
    modeproxToUI = math.mode(...proxToUIArray);
    maxproxToUI = math.max(...proxToUIArray);
    minproxToUI = math.min(...proxToUIArray);
    stdevproxToUI = math.std(...proxToUIArray);

    console.log(`Statistical analyses Proxy --> UI (ms) - 
            \nMin: ${minproxToUI} \nMax: ${maxproxToUI}
            \nMode: ${modeproxToUI} \nMedian: ${medianproxToUI} 
            \nMean: ${meanproxToUI} \nSTDev: ${stdevproxToUI}`);

    let stats = [
        trialNumber,
        (1000 - parsedTimes.length),
        meanpubToProx,
        medianpubtoProx,
        modepubtoProx[0],
        maxpubtoProx,
        minpubtoProx,
        stdevpubtoProx,
        meanproxToUI,
        medianproxToUI,
        modeproxToUI[0],
        maxproxToUI,
        minproxToUI,
        stdevproxToUI
    ];

    stompClient.send("/app/stats", {}, stats.toString());

    runTimes = [];
    receivedMessages = 0;
    trialNumber++;
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#analyzeTimes" ).click(function() { analyzeRuntime(); });
});