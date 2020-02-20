package com.redpill_linpro.query_service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;


@Controller
public class WebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws JSONException, IOException {

        String payload = (String) new JSONObject(message.getPayload()).get("query");
        System.out.println(payload);
        session.sendMessage(new TextMessage("Query received : " + payload));
    }
}

