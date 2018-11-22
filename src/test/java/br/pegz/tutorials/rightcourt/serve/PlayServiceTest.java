package br.pegz.tutorials.rightcourt.serve;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.score.ScoreNotifierService;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import br.pegz.tutorials.rightcourt.serve.resource.CourtResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PlayServiceTest {

    @Autowired
    PlayService playService;

    @MockBean
    CourtResource courtResource;
    @MockBean
    ScoreNotifierService scoreNotifierService;

    @DisplayName("When serving, receive play from Left that accounts Point for RIGHT SIDE")
    @Test
    void servePointForRight() throws Throwable {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        given(this.courtResource.sendPlayToOtherSide(any(Play.class)))
                .willReturn(Play.builder()
                        .incomingSide(Side.LEFT)
                        .count(atomicInteger.getAndAdd(2))
                        .effect(true)
                        .innerSide(Side.NET)
                        .height(Height.MEDIUM)
                        .speed(Speed.AVG)
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
                        .incomingSide(Side.LEFT)
                        .count(atomicInteger.getAndAdd(2))
                        .effect(true)
                        .innerSide(Side.LEFT)
                        .height(Height.BEYOND_REACH)
                        .speed(Speed.AVG)
                        .build());
        playService.serve();
        verify(this.courtResource, atMost(1)).sendPlayToOtherSide(any());
        verify(this.scoreNotifierService, never()).notifyMyPoint(anyInt());
        verify(this.scoreNotifierService, atLeastOnce()).notifyFoePoint(anyInt());
    }

    private final static Play winForRight = Play.builder()
            .innerSide(Side.NET)
            .incomingSide(Side.LEFT)
            .count(6)
            .speed(Speed.FAST)
            .height(Height.BURNT)
            .effect(false)
            .build();
    private final static Play winForLeft = Play.builder()
            .effect(true)
            .height(Height.BEYOND_REACH)
            .incomingSide(Side.LEFT)
            .speed(Speed.FAST)
            .count(1)
            .innerSide(Side.RIGHT)
            .build();

    private static Stream<Play> getSuccessPlay() {
        return Stream.<Play>builder().add(Play.builder()
                .innerSide(Side.LEFT)
                .incomingSide(Side.LEFT)
                .speed(Speed.SLOW)
                .count(0)
                .height(Height.LOW)
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
        assertThrows(PointException.class, () -> playService.handlePlay(play), "Point for side:");
    }
}