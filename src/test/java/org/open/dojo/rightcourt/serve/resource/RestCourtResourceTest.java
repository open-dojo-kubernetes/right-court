package org.open.dojo.rightcourt.serve.resource;

import org.open.dojo.rightcourt.configuration.CourtConfiguration;
import org.open.dojo.rightcourt.persistence.Play;
import org.open.dojo.rightcourt.persistence.enums.Height;
import org.open.dojo.rightcourt.persistence.enums.Side;
import org.open.dojo.rightcourt.persistence.enums.Speed;
import org.open.dojo.rightcourt.serve.exception.PointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class RestCourtResourceTest {

    @Autowired
    RestCourtResource restCourtResource;
    @MockBean
    RestTemplate restTemplate;
    @MockBean
    CourtConfiguration courtConfiguration;

    @DisplayName("When sending the built play to the other side of the court, send successfully")
    @Test
    void whenPlayNotifyPlay() throws PointException {
        given(this.courtConfiguration.checkLeftCourtStatus())
                .willReturn(true);
        given(this.restTemplate.postForEntity(anyString(), any(Play.class), eq(Play.class)))
                .willReturn(
                        ResponseEntity.ok(
                                Play.builder()
                                        .innerSide(Side.LEFT)
                                        .incomingSide(Side.LEFT)
                                        .speed(Speed.SLOW)
                                        .count(2)
                                        .height(Height.LOW)
                                        .effect(true)
                                        .build()));
        Play incomingSide = this.restCourtResource.sendPlayToOtherSide(Play.builder()
                .speed(Speed.SLOW)
                .incomingSide(Side.RIGHT)
                .innerSide(Side.RIGHT)
                .height(Height.HIGH)
                .effect(false)
                .count(1)
                .build());
        verify(courtConfiguration, atLeastOnce()).checkLeftCourtStatus();
        verify(restTemplate, atLeastOnce()).postForEntity(anyString(), any(Play.class), eq(Play.class));
        assertEquals(2, incomingSide.getCount().intValue());
    }

    @DisplayName("When my point notify with Exception")
    @Test
    void whenMyPointNotifyError() throws PointException {
        given(this.courtConfiguration.checkLeftCourtStatus())
                .willReturn(true);
        given(this.restTemplate.postForEntity(anyString(), any(Play.class), eq(Play.class)))
                .willReturn(ResponseEntity.status(511).build());
        assertThrows(PointException.class,
                () -> this.restCourtResource.sendPlayToOtherSide(
                        Play.builder()
                                .speed(Speed.SLOW)
                                .incomingSide(Side.RIGHT)
                                .innerSide(Side.RIGHT)
                                .height(Height.HIGH)
                                .effect(false)
                                .count(1)
                                .build()), "Point for side: RIGHT");
    }
}