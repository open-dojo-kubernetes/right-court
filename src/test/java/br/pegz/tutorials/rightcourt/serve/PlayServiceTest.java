package br.pegz.tutorials.rightcourt.serve;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import br.pegz.tutorials.rightcourt.serve.resource.CourtResource;
import org.junit.Rule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PlayServiceTest {

    @Autowired
    PlayService playService;

    @MockBean
    CourtResource courtResource;

    @Rule
    ExpectedException expectedException = ExpectedException.none();

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
        expectedException.expect(PointException.class);
        expectedException.expectMessage("Point for side: RIGHT");
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
        playService.serve()
                .doOnEach(playSignal -> assertEquals(atomicInteger.get(), playSignal.get().getCount().intValue()));
        verify(this.courtResource, atMost(1)).sendPlayToOtherSide(any());
        expectedException.expect(PointException.class);
        expectedException.expectMessage("Point for side: RIGHT");
    }

    static Play validPlay = Play.builder()
            .innerSide(Side.LEFT)
            .incomingSide(Side.LEFT)
            .speed(Speed.SLOW)
            .build();



    void handlePlay() {


    }
}