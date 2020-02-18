
var webSocket;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#send").prop("disabled", !connected);
	$("#disconnect").prop("disabled", !connected);
}

function connect() {
	webSocket = new WebSocket('ws://localhost:8989/query');
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
	respond($("#query").val().replace(/\n/g, "<br />"));
}

function respond(text){
    $("#response").html(text);
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