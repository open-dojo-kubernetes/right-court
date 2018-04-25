package br.pegz.tutorials.rightcourt.persistence;

import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

@Data
@Builder
@ToString
@JsonDeserialize(builder = Play.PlayBuilder.class)
public final class Play {
    private Side incomingSide;
    private Boolean effect;
    private Side innerSide;
    private Integer count;
    private Speed speed;
    private Height height;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class PlayBuilder {
    }
}

