package jame.dev.webChat.WebSocketConfig;

import jame.dev.webChat.WebSocket.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

   private final ChatHandler chatHandler;

   public WebSocketConfig(final ChatHandler chatHandler) {
      this.chatHandler = chatHandler;
   }

   @Override
   public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
      registry.addHandler(chatHandler, "/chat", "/chat-with")
              .setAllowedOrigins("http://localhost:5173");
   }
}
