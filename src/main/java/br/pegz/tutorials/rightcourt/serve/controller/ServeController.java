package br.pegz.tutorials.rightcourt.serve.controller;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.serve.PlayService;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/right")
public class ServeController {

    private PlayService playService;

    public ServeController(PlayService playService) {
        this.playService = playService;
    }

    @PostMapping("/serve")
    public Flux<Play> startPlay() throws PointException {
        log.info("Starting Serve");
        return playService.serve();
    }

    @PostMapping("/play")
    public Mono<Play> respondPlay(@RequestBody Play play) throws PointException {
        log.info("Receiving play from {}", play);
        return Mono.just(playService.handlePlay(play));
    }
}
