package org.open.dojo.rightcourt.persistence;

import org.open.dojo.rightcourt.persistence.enums.Height;
import org.open.dojo.rightcourt.persistence.enums.Side;
import org.open.dojo.rightcourt.persistence.enums.Speed;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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

