package jame.dev.webChat.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public record MsgResponse(
        @JsonProperty("type") String type,
        @JsonProperty("data") Message msg
) { }
