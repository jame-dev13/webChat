package jame.dev.webChat.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jame.dev.webChat.models.MsgResponse;
import jame.dev.webChat.models.SummaryResponse;
import jame.dev.webChat.models.Type;

public class ResponseFactory {

   private static final ObjectMapper mapper = new ObjectMapper();

   public static Object parsePayload(String payload) throws JsonProcessingException {
      JsonNode json = mapper.readTree(payload);
      Type type = Type.valueOf(json.get("type").asText());

      return switch (type){
         case MSG -> mapper.treeToValue(json, MsgResponse.class);
         case SUMMARY -> mapper.treeToValue(json, SummaryResponse.class);
         default -> throw new IllegalArgumentException("Response type unknow." + type);
      };
   }
}
