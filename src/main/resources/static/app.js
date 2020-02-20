
var webSocket;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#send").prop("disabled", !connected);
	$("#disconnect").prop("disabled", !connected);
}

function connect() {
	webSocket = new WebSocket('ws://localhost:8080/query');
    webSocket.onmessage = function(data) {
    		respond(data.data);
    	}
	setConnected(true);
}

function disconnect() {
	if (webSocket != null) {
		webSocket.close();
	}
	setConnected(false);
}

function sendData() {
	var data = JSON.stringify({
		'query' : $("#query").val()
	})
	webSocket.send(data);
	//respond($("#query").val());
}

function respond(data){
    $("#response").html(data).replace(/\n/g, "<br />");
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	$("#connect").click(function() {
		connect();
	});
	$("#disconnect").click(function() {
		disconnect();
	});
	$("#send").click(function() {
		sendData();
	});
});