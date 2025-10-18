package jame.dev.webChat.WebSocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import jame.dev.webChat.models.Message;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class ChatHandler extends TextWebSocketHandler {
   private final Set<WebSocketSession> sessions = new HashSet<>();
   private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
   private final ObjectMapper mapper = new ObjectMapper();

   @Override
   public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
      sessions.add(session);
      System.out.println("Connection established: " + session.getRemoteAddress());
   }

   @Override
   protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
      String payload = message.getPayload();
      Message msg = mapper.readValue(payload, Message.class);
      if (msg.to().isBlank()) {
         broadcast(msg);
         return;
      }
      registryUserSession(msg.to(), session);
//      var userWsSession = userSessions.get(msg.to());
//      Optional.ofNullable(userWsSession)
//              .ifPresent(ws -> {
//                 System.out.printf("msg to: " + msg.to());
//                 if (!ws.isOpen()) {
//                    System.out.println("ws not open.");
//                    return;
//                 }
//                 try {
//                    ws.sendMessage(new TextMessage(mapper.writer().writeValueAsString(msg)));
//                    System.out.println(": " + msg.msg());
//                 } catch (IOException e) {
//                    throw new RuntimeException(e);
//                 }
//              });
      userSessions.forEach((to, ws) -> {
         System.out.println("msg to: " + to);
         if (!ws.isOpen()) {
            System.out.println("ws not open.");
            return;
         }
         try {
            ws.sendMessage(new TextMessage(mapper.writer().writeValueAsString(msg)));
            System.out.println(": " + msg.msg());
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      });
   }

   @Override
   public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
      sessions.remove(session);
      System.out.println("Connection closed for: " + session.getRemoteAddress() + ", code: " + status.getCode() + ", reason: " + status.getReason());
   }

   private void broadcast(Message msg) {
      System.out.println("Global Messages: ");
      sessions.forEach(s -> {
         if (s.isOpen()) {
            try {
               TextMessage textMessage = new TextMessage(mapper.writer().writeValueAsString(msg));
               System.out.println(msg.from() + ": " + msg.msg());
               s.sendMessage(textMessage);
            } catch (IOException e) {
               System.out.println("Can't write the JSON Object");
            }
         }
      });

   }

   private void registryUserSession(String to, WebSocketSession session) {
      System.out.println("User registry");
      userSessions.putIfAbsent(to, session);
   }
}