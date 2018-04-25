package br.pegz.tutorials.rightcourt.serve;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.score.ScoreNotifierService;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import reactor.core.publisher.Flux;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class PlayService {

    private CourtResource courtResource;
    private Random random = new SecureRandom();
    private ScoreNotifierService scoreNotifierService;

    public PlayService(CourtResource courtResource, ScoreNotifierService scoreNotifierService) {
        this.courtResource = courtResource;
        this.scoreNotifierService = scoreNotifierService;
    }

    public Flux<Play> serve() throws PointException {
        List<Play> plays = Lists.newArrayList(getServePlay());
        Play incomingPlay = courtResource.sendPlayToOtherSide(getServePlay());
        while(isPlayable(incomingPlay) && isReceivable(incomingPlay)) {
            log.info("Received play: {}", incomingPlay);
            plays.add(incomingPlay);
            Play nextPlay = getRespondSpeed(incomingPlay);
            if(isPlayable(nextPlay)) {
                try {
                    incomingPlay = courtResource.sendPlayToOtherSide(nextPlay);
                } catch (RestClientException ex) {
                    log.error("Could not reach other side", ex);
                    log.debug("Next play would be {}", nextPlay);
                    break;
                }
            } else {
                scoreNotifierService.notifyFoePoint(incomingPlay.getCount() + 1 );
            }
            plays.add(nextPlay);
        }
        if (!isPlayable(incomingPlay)) {
            scoreNotifierService.notifyFoePoint(incomingPlay.getCount());
        } else if (!isReceivable(incomingPlay)) {
            scoreNotifierService.notifyMyPoint(incomingPlay.getCount());
        }
        return Flux.fromStream(plays.stream());
    }

    private boolean isPlayable(Play incomingPlay) {
        return Speed.OMFG != incomingPlay.getSpeed() || Height.BEYOND_REACH != incomingPlay.getHeight();
    }

    private boolean isReceivable(Play incomingPlay) {
        return Side.NET == incomingPlay.getInnerSide() || Side.OUTSIDE == incomingPlay.getInnerSide();
    }

    private Play getRespondSpeed(Play incomingPlay) {
        Speed responseSpeed = Speed.values()[random.nextInt(Speed.values().length)];
        Height responseHeight = Height.values()[random.nextInt(Height.values().length)];
        Side responseInnerSide = Side.values()[random.nextInt(Side.values().length)];
        return Play.builder()
                .count(incomingPlay.getCount()+1)
                .effect(!incomingPlay.getEffect())
                .speed(responseSpeed)
                .height(responseHeight)
                .incomingSide(Side.RIGHT)
                .innerSide(responseInnerSide)
                .build();
    }


    public Play handlePlay(Play incomingPlay) throws PointException {
        if(isReceivable(incomingPlay) && isPlayable(incomingPlay)) {
            return getRespondSpeed(incomingPlay);
        } else if (!isPlayable(incomingPlay)) {
            scoreNotifierService.notifyFoePoint(incomingPlay.getCount());
            throw new PointException(Side.LEFT);
        } else if (!isReceivable(incomingPlay)) {
            scoreNotifierService.notifyMyPoint(incomingPlay.getCount());
            throw new PointException(Side.RIGHT);
        }
        return null;
    }

    private Play getServePlay() {
        return Play.builder()
                .count(0)
                .effect(true)
                .height(Height.LOW)
                .speed(Speed.FAST)
                .incomingSide(Side.RIGHT)
                .innerSide(Side.RIGHT)
                .build();
    }

    private Play getDefaultPlay(Integer count) {
        count++;
        return Play.builder()
                .count(count)
                .effect(false)
                .height(Height.MEDIUM)
                .speed(Speed.AVG)
                .incomingSide(Side.RIGHT)
                .innerSide(Side.LEFT)
                .build();
    }
}
