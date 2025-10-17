package jame.dev.webChat.WebSocket;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatHandler extends TextWebSocketHandler {
   private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

   @Override
   public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      sessions.add(session);
      System.out.println("Connection established: " + session.getRemoteAddress());
   }

   @Override
   protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
      String payload = message.getPayload();
      for (WebSocketSession webSocketSession : sessions) {
         if (webSocketSession.isOpen()) {
            webSocketSession.sendMessage(new TextMessage(payload));
            System.out.println("Message: " +message+ " send to: " + session.getRemoteAddress() + " _: " + session.getUri());
         }
      }
   }

   @Override
   public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
      sessions.remove(session);
      System.out.printf("Connection closed: " + session.getRemoteAddress());
   }
}