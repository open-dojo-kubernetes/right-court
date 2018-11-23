package org.open.dojo.rightcourt.serve.resource;

import org.open.dojo.rightcourt.configuration.CourtConfiguration;
import org.open.dojo.rightcourt.persistence.Play;
import org.open.dojo.rightcourt.serve.exception.PointErrorCodes;
import org.open.dojo.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class RestCourtResource implements CourtResource {

    private final RestTemplate restTemplate;
    private CourtConfiguration courtConfiguration;

    @Autowired
    public RestCourtResource(RestTemplate restTemplate, CourtConfiguration courtConfiguration) {
        this.restTemplate = restTemplate;
        this.courtConfiguration = courtConfiguration;
    }

    public Play sendPlayToOtherSide(Play myPlay) throws PointException {
        log.info("Responding play with {}", myPlay);
        Assert.isTrue(courtConfiguration.checkLeftCourtStatus(), "Left Court not available, please retry later");
        ResponseEntity<Play> playResponseEntity = restTemplate.postForEntity(CourtConfiguration.LEFT_PLAY, myPlay, Play.class);
        if (playResponseEntity.getStatusCode().is5xxServerError()) {
            final PointException pointException = new PointException(PointErrorCodes.of(myPlay.getIncomingSide()));
            log.error("Notifying my point", pointException);
            throw pointException;
        } else {
            return playResponseEntity.getBody();
        }
    }
}
