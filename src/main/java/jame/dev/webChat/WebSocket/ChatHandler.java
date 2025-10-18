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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public final class ChatHandler extends TextWebSocketHandler {
   private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
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
      broadcast(msg);
   }

   @Override
   public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
      sessions.remove(session);
      System.out.println("Connection closed for: " + session.getRemoteAddress() + ", code: " + status.getCode() + ", reason: " + status.getReason());
   }

   private void broadcast(Message msg) {
      sessions.forEach(s -> {
         if (s.isOpen()) {
            try {
               TextMessage textMessage = new TextMessage(mapper.writer().writeValueAsString(msg));
               System.out.println("Payload: " + textMessage.getPayload() + " send.");
               s.sendMessage(textMessage);
            } catch (IOException e) {
               System.out.println("Can't write the JSON Object");
            }
         }
      });

   }
}