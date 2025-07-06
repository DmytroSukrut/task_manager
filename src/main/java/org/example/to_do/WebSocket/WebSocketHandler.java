package org.example.to_do.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.ServerEndpoint;
import org.example.to_do.Operations.RegistrationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@ServerEndpoint("/ws")
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RegistrationService RegService;

    public WebSocketHandler(RegistrationService RegService) {
        this.RegService = RegService;
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();

            JsonNode root = mapper.readTree(payload);
            String type = root.get("type").asText();

            if (type.equals("register")) {
                JsonNode data = root.get("data");
                String name = data.get("name").asText();
                String email = data.get("email").asText();
                String password = data.get("password").asText();
                RegService.RegUser(name, email, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
