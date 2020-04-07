
var webSocket = new WebSocket('ws://localhost:8080/query')

webSocket.onopen = function(event) {
    connect("Connected");
}

webSocket.onclose = function(event) {
    disconnect("Disconnected");
}

function setConnected(connected) {
	$("#send").prop("disabled", !connected);
}

function connect(message) {
    webSocket.onmessage = function(data) {
        respond(data.data);
    }
    document.getElementById("connection-status").innerHTML = message
	setConnected(true);
}

function disconnect(message) {
	if (webSocket != null) {
		webSocket.close();
	}
	document.getElementById("connection-status").innerHTML = message;
	document.getElementById("connection-status").style.color = "red";
	setConnected(false);
}

function sendData() {
	var data = JSON.stringify({
		'query' : $("#query").val()
	})
	console.log(data)
	webSocket.send(data);
	$(".bindings").html("")
}

function respond(data){
    var table = $("<table></table>").addClass("foo");
    $("table").attr("cellspacing", 10);
            row = $("<tr></tr>");
            if (data.includes("#")) {
                var newData = data.split("#");
                for (var j = 0; j < 2; j++) {
                    var rowData = $("<td></td>").addClass("bar").text(newData[j]);
                    row.append(rowData);
                }
            } else {
                var rowData = $("<td></td>").addClass("bar").text(data);
                row.append(rowData);
            }
            table.append(row)
    		if ($("table").length) {
    		    $(".bindings tr:first").after(row);
    		}
    		else {
    		    $(".bindings").append(table);
    		}
}

function hover(element) {
    element.setAttribute("src", "sendButtonOnHover.png");
}

function unhover(element) {
    element.setAttribute("src", "sendButtonDefault.png");
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});

	$("#send").click(function() {
		sendData();
	});

	$("#query").keypress(function(e) {
	    var key = e.which;
	    if (key == 13) { // Keycode for enter-key
	        document.getElementById("send").click();
	    }
	})
});