package org.example.to_do.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.ServerEndpoint;
import org.example.to_do.Operations.*;
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
    private final LoginService loginService;
    private final UserExistsService DoesUserExistsService;
    private final GetUserInfoService getUserInfoService;
    private final GetTaskService getTaskService;
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public WebSocketHandler(GetTaskService getTaskService, GetUserInfoService getUserInfoService, LoginService loginService, RegistrationService RegService, UserExistsService DoesUserExistsService) {
        this.loginService = loginService;
        this.getUserInfoService = getUserInfoService;
        this.RegService = RegService;
        this.DoesUserExistsService = DoesUserExistsService;
        this.getTaskService = getTaskService;
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
            } else if (type.equals("login")) {

                //Get data for login
                JsonNode data = root.get("data");
                String email = data.get("email").asText();
                String password = data.get("password").asText();

                boolean correct = loginService.LoginUser(email, password);
                if (correct) {
                    //Get users information
                    dataMap.put("type", "login_success");
                    dataMap.put("message", getUserInfoService.GetUsersInfo(email));

                    //Send it
                    json = mapper.writeValueAsString(dataMap);
                    System.out.println(json);

                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(json));
                    }
                } else {
                    //Failed to log in
                    dataMap.put("type", "login_fail");

                    //Send it
                    json = mapper.writeValueAsString(dataMap);
                    System.out.println(json);

                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(json));
                    }
                }
            } else if (type.equals("get_random_task")) {
                String task = getTaskService.GetRandomTask();

                dataMap.put("type", "random_task_got");
                dataMap.put("message", task);

                json = mapper.writeValueAsString(dataMap);
                System.out.println(json);

                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
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
