var stompClient = null;
var url = 'http://localhost:8866'
var basedir = '/dcs_con_test'

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
    var socket = new SockJS(basedir + '/dcs-concur-test');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showResult(JSON.parse(greeting.body).content);
        });
    });
}

// function disconnect() {
//     if (stompClient != null) {
//         stompClient.disconnect();
//     }
//     setConnected(false);
//     console.log("Disconnected");
// }
//
// function sendName() {
//     stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
// }

function emit() {
    $.ajax(url+basedir+'/test/contracts', {
        type:'POST',
        data: {
            round: $("#round").val(),
            concurrent: $("#concurrent").val()
        },
        success: function (json) {
            console.log(json);
            showResult(json);
        }
    })
}

function showResult(message) {
    $("#console").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    // $( "#connect" ).click(function() { connect(); });
    // $( "#disconnect" ).click(function() { disconnect(); });
    connect();
    $( "#emit" ).click(function() { emit(); });
});

