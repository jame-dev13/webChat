package jame.dev.webChat.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public record Message(
        @JsonProperty("from")
        String from,
        @JsonProperty("msg")
        String msg
) {}