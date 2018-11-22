package br.pegz.tutorials.rightcourt.serve;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.persistence.enums.Height;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import br.pegz.tutorials.rightcourt.persistence.enums.Speed;
import br.pegz.tutorials.rightcourt.score.ScoreNotifierService;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import br.pegz.tutorials.rightcourt.serve.resource.RestCourtResource;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class PlayService {

    private RestCourtResource restCourtResource;
    private Random random = new SecureRandom();
    private ScoreNotifierService scoreNotifierService;

    public PlayService(RestCourtResource restCourtResource, ScoreNotifierService scoreNotifierService) {
        this.restCourtResource = restCourtResource;
        this.scoreNotifierService = scoreNotifierService;
    }

    public Flux<Play> serve() throws PointException {
        List<Play> plays = Lists.newArrayList(getServePlay());
        Play incomingPlay = restCourtResource.sendPlayToOtherSide(getServePlay());
        while (canContinuePlay(incomingPlay)) {
            log.info("Received play: {}", incomingPlay);
            plays.add(incomingPlay);
            Play myPlay = handlePlay(incomingPlay);
            plays.add(myPlay);
            incomingPlay = restCourtResource.sendPlayToOtherSide(myPlay);
        }
        notifyPoint(incomingPlay);
        return Flux.fromStream(plays.stream());
    }

    private boolean canContinuePlay(Play incomingPlay) {
        return !isLeftPoint(incomingPlay) && !isRightPoint(incomingPlay);
    }

    private void notifyPoint(Play incomingPlay) {
        if (isLeftPoint(incomingPlay)) {
            scoreNotifierService.notifyFoePoint(incomingPlay.getCount());
        } else if (isRightPoint(incomingPlay)) {
            scoreNotifierService.notifyMyPoint(incomingPlay.getCount());
        }
    }

    private boolean isLeftPoint(Play incomingPlay) {
        return !isRightPoint(incomingPlay) && Speed.OMFG == incomingPlay.getSpeed() || Height.BEYOND_REACH == incomingPlay.getHeight();
    }

    private boolean isRightPoint(Play incomingPlay) {
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
        if(!isRightPoint(incomingPlay) && !isLeftPoint(incomingPlay)) {
            return getRespondSpeed(incomingPlay);
        } else if (isLeftPoint(incomingPlay)) {
            scoreNotifierService.notifyFoePoint(incomingPlay.getCount());
            throw new PointException(Side.LEFT);
        } else if (isRightPoint(incomingPlay)) {
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
}
