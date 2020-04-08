package com.redpill_linpro.query_service.web;

import com.redpill_linpro.query_service.parser.QueryParser;
import com.redpill_linpro.query_service.formatter.BindingFormatter;
import com.redpill_linpro.query_service.util.Vocabulary;
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

import static com.redpill_linpro.query_service.util.ApplicationProperties.*;

@Controller
public class WebSocketHandler extends TextWebSocketHandler {

    private static HashMap<WebSocketSession, Integer>
                    sessionMapper = new HashMap<>();
    private static Vocabulary voc = new Vocabulary(getAppProperty("vocabulary"));

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
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            String payload = (String) new JSONObject(message.getPayload()).get("query");
            QueryParser queryParser = new QueryParser(payload, voc);

            for (String query : queryParser.getSparqlQueries()) {
                List<String> bindings = new ArrayList<>(RepositoryHandler.sendQuery(query));

                if (query.contains("?rootLabel ?answerLabel")) {
                    List<String> tuples = BindingFormatter.compressList(bindings);
                    for (String tuple : tuples)
                        session.sendMessage(new TextMessage(tuple));
                }else
                    for (String bind : bindings)
                        session.sendMessage(new TextMessage(bind));

            }
        } catch(Exception e) {
            e.printStackTrace();
            session.sendMessage(new TextMessage("Invalid question. Try again"));
        }
    }

    private static void closeSession(WebSocketSession session){
        sessionMapper.remove(session);
    }
}

