package jame.dev.webChat.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Set;

@JsonSerialize
@JsonDeserialize
public record Summary(
        @JsonProperty("size") int size,
        @JsonProperty("subjects") Set<String> names
) {
}
