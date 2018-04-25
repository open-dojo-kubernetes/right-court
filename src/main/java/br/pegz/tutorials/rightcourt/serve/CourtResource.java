package br.pegz.tutorials.rightcourt.serve;

import br.pegz.tutorials.rightcourt.configuration.CourtConfiguration;
import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class CourtResource {

    private final RestTemplate restTemplate;
    private CourtConfiguration courtConfiguration;

    @Autowired
    public CourtResource(RestTemplate restTemplate, CourtConfiguration courtConfiguration) {
        this.restTemplate = restTemplate;
        this.courtConfiguration = courtConfiguration;
    }

    public Play sendPlayToOtherSide(Play myPlay) throws PointException {
        log.info("Responding play with {}", myPlay);
        Assert.isTrue(courtConfiguration.checkLeftCourtStatus(), "Left Court not available, please retry later");
        ResponseEntity<Play> playResponseEntity = restTemplate.postForEntity(CourtConfiguration.LEFT_PLAY, myPlay, Play.class);
        if(playResponseEntity.getStatusCode().is5xxServerError()) {
            throw new PointException(myPlay.getIncomingSide());
        } else {
            return playResponseEntity.getBody();
        }
    }

}
