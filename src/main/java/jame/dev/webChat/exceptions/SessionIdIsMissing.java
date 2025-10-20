package jame.dev.webChat.exceptions;

public class SessionIdIsMissing extends RuntimeException {
   public SessionIdIsMissing(String message, Throwable e) {
      super(message, e);
   }
   public SessionIdIsMissing(String message) {
      super(message);
   }
}
