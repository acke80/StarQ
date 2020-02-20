package com.redpill_linpro.query_service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Controller
public class WebSocketHandler extends TextWebSocketHandler {

    private static HashMap<WebSocketSession, GraphDBHandler>
                    sessionMapper = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("Hello!"));
        sessionMapper.put(session, new GraphDBHandler());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        sessionMapper.remove(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws JSONException, IOException {
        String payload = (String) new JSONObject(message.getPayload()).get("query");
        System.out.println(payload);
        session.sendMessage(message);
        sessionMapper.get(session).sendQuery(payload);
    }
}

