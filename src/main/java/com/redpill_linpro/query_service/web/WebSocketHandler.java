package com.redpill_linpro.query_service.web;

import com.redpill_linpro.query_service.RepositoryHandler;
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

    private static HashMap<WebSocketSession, Integer>
                    sessionMapper = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("Hello!"));
        sessionMapper.put(session, 0);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        closeSession(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = (String) new JSONObject(message.getPayload()).get("query");

        List<String> bindings = new ArrayList<>(RepositoryHandler.sendQuery(payload));
        for(String bind : bindings)
            session.sendMessage(new TextMessage(bind));

    }

    private static void closeSession(WebSocketSession session){
        sessionMapper.remove(session);
    }
}
