package jame.dev.webChat.WebSocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import jame.dev.webChat.exceptions.SessionIdIsMissing;
import jame.dev.webChat.factories.ResponseFactory;
import jame.dev.webChat.models.Message;
import jame.dev.webChat.models.MsgResponse;
import jame.dev.webChat.models.SummaryResponse;
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
   private final Set<String> idSessions = new HashSet<>();
   private final ObjectMapper mapper = new ObjectMapper();

   @Override
   public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
      sessions.add(session);
      idSessions.add(session.getId());
      System.out.println("Connection established: " + session.getRemoteAddress());
   }

   @Override
   protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
      String payload = message.getPayload();
      Object response = ResponseFactory.parsePayload(payload);
      if (response instanceof MsgResponse msgResponse) {
         if(msgResponse.msg().to().isBlank() || msgResponse.msg().to().equalsIgnoreCase("ALL")){
            broadcast(msgResponse.msg());
            return;
         }
         registryUserSession(msgResponse.msg().to(), session);
         msgTo(msgResponse.msg());
      }
      else if (response instanceof SummaryResponse sr) {
         session.sendMessage(new TextMessage(mapper.writer().writeValueAsString(sr)));
      }
   }

   @Override
   public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
      sessions.remove(session);
      idSessions.remove(session.getId());
      System.out.println("Connection closed for: " + session.getRemoteAddress() + ", code: " + status.getCode() + ", reason: " + status.getReason());
   }

   private void broadcast(Message msg) {
      System.out.println("Global Messages: ");
      sessions.forEach(s -> {
         if (!idSessions.contains(s.getId())) {
            throw new SessionIdIsMissing("The id of the session is missing.");
         }
         if (s.isOpen()) {
            try {
               TextMessage textMessage = new TextMessage(mapper.writer().writeValueAsString(msg));
               System.out.println(msg.from() + ": " + msg.msg());
               s.sendMessage(textMessage);
            } catch (IOException e) {
               System.out.println("Cannot write the JSON Object");
            }
         }
      });
   }

   private void msgTo(Message msg) {
      userSessions.forEach((to, ws) -> {
         System.out.println("msg to: " + to);
         if (!idSessions.contains(ws.getId())) {
            throw new SessionIdIsMissing("No session id present.");
         }
         if (!ws.isOpen()) {
            System.out.println("ws not open.");
            return;
         }
         try {
            ws.sendMessage(new TextMessage(mapper.writer().writeValueAsString(msg)));
            System.out.println(msg.from() +": " + msg.msg());
         } catch (IOException e) {
            System.out.println("Cannot write JSON object.");
         }
      });
   }

   private void registryUserSession(String to, WebSocketSession session) {
      System.out.println("- - - User registry - - -");
      userSessions.putIfAbsent(to, session);
   }
}