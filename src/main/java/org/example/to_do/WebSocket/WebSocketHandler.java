package org.example.to_do.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.ServerEndpoint;
import org.example.to_do.Operations.RegistrationService;
import org.example.to_do.Operations.UserExistsService;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/ws")
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RegistrationService RegService;
    private final UserExistsService DoesUserExistsService;
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public WebSocketHandler(RegistrationService RegService, UserExistsService DoesUserExistsService) {
        this.RegService = RegService;
        this.DoesUserExistsService = DoesUserExistsService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();

            JsonNode root = mapper.readTree(payload);
            String type = root.get("type").asText();
            Map<String, Object> dataMap = new HashMap<>();
            String json;

            if (type.equals("register")) {

                //Create an account
                JsonNode data = root.get("data");
                String name = data.get("name").asText();
                String email = data.get("email").asText();
                String password = data.get("password").asText();

                //Check if account exists
                if(DoesUserExistsService.DoesUserExists(email)){
                    dataMap.put("type", "user_exists");
                    dataMap.put("message", email);
                    json = mapper.writeValueAsString(dataMap);
                    System.out.println(json);
                    session.sendMessage(new TextMessage(json));
                } else {
                    RegService.RegUser(name, email, password);

                    //Send answer
                    dataMap.put("type", "register_success");
                    dataMap.put("message", name);

                    json = mapper.writeValueAsString(dataMap);
                    System.out.println(json);

                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(json));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

}
