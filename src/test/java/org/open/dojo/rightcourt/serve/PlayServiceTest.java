package org.open.dojo.rightcourt.serve;

import org.open.dojo.rightcourt.persistence.Play;
import org.open.dojo.rightcourt.persistence.enums.Height;
import org.open.dojo.rightcourt.persistence.enums.Side;
import org.open.dojo.rightcourt.persistence.enums.Speed;
import org.open.dojo.rightcourt.score.ScoreNotifierService;
import org.open.dojo.rightcourt.serve.exception.PointException;
import org.open.dojo.rightcourt.serve.resource.CourtResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.open.dojo.rightcourt.persistence.enums.Height.*;
import static org.open.dojo.rightcourt.persistence.enums.Side.*;
import static org.open.dojo.rightcourt.persistence.enums.Speed.AVG;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PlayServiceTest {

    @Autowired
    PlayService playService;

    @MockBean
    CourtResource courtResource;

    @MockBean
    RabbitTemplate rabbitTemplate;

    @MockBean
    ScoreNotifierService scoreNotifierService;

    @DisplayName("When serving, receive play from Left that accounts Point for RIGHT SIDE")
    @Test
    void servePointForRight() throws Throwable {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        given(this.courtResource.sendPlayToOtherSide(any(Play.class)))
                .willReturn(Play.builder()
                        .incomingSide(LEFT)
                        .count(atomicInteger.getAndAdd(2))
                        .effect(true)
                        .innerSide(NET)
                        .height(MEDIUM)
                        .speed(AVG)
                        .build());
        playService.serve();
        verify(this.courtResource, atMost(1)).sendPlayToOtherSide(any());
        verify(this.scoreNotifierService, atLeastOnce()).notifyMyPoint(anyInt());
        verify(this.scoreNotifierService, never()).notifyFoePoint(anyInt());
    }

    @DisplayName("When serving, receive play from Left that accounts Point for LEFT SIDE")
    @Test
    void servePointForLeft() throws Throwable {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        given(this.courtResource.sendPlayToOtherSide(any(Play.class)))
                .willReturn(Play.builder()
                        .incomingSide(LEFT)
                        .count(atomicInteger.getAndAdd(2))
                        .effect(true)
                        .innerSide(LEFT)
                        .height(BEYOND_REACH)
                        .speed(AVG)
                        .build());
        playService.serve();
        verify(this.courtResource, atMost(1)).sendPlayToOtherSide(any());
        verify(this.scoreNotifierService, never()).notifyMyPoint(anyInt());
        verify(this.scoreNotifierService, atLeastOnce()).notifyFoePoint(anyInt());
    }

    private final static Play winForRight = Play.builder()
            .innerSide(NET)
            .incomingSide(LEFT)
            .count(6)
            .speed(Speed.FAST)
            .height(BURNT)
            .effect(false)
            .build();
    private final static Play winForLeft = Play.builder()
            .effect(true)
            .height(BEYOND_REACH)
            .incomingSide(LEFT)
            .speed(Speed.FAST)
            .count(1)
            .innerSide(RIGHT)
            .build();

    private static Stream<Play> getSuccessPlay() {
        return Stream.<Play>builder().add(Play.builder()
                .innerSide(LEFT)
                .incomingSide(LEFT)
                .speed(Speed.SLOW)
                .count(0)
                .height(LOW)
                .effect(true)
                .build()).build();
    }

    private static Stream<Play> getPointPlays() {
        return Stream.<Play>builder().add(winForLeft).add(winForRight).build();
    }

    @DisplayName("When returning play")
    @ParameterizedTest(name = "On returning play for {index}:[{arguments}]")
    @MethodSource("getSuccessPlay")
    void handleSuccessPlay(Play play) throws Throwable {
        playService.handlePlay(play);
    }

    @DisplayName("When Point for one of the sides")
    @ParameterizedTest(name = "On returning play for {index}:[{arguments}]")
    @MethodSource("getPointPlays")
    void handlePointsPlay(Play play) throws Throwable {
        Assertions.assertThrows(PointException.class, () -> playService.handlePlay(play), "Point for side:");
    }

    @DisplayName("Receive a stream of plays from different players and tables")
    @Test
    void checkRabbitMq() throws Throwable {

    }
}