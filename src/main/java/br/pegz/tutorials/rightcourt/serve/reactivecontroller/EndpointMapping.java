package br.pegz.tutorials.rightcourt.serve.reactivecontroller;

import br.pegz.tutorials.rightcourt.configuration.CourtConfiguration;
import br.pegz.tutorials.rightcourt.configuration.model.Probe;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EndpointMapping {



    public EndpointMapping(CourtConfiguration court, RabbitTemplate rabbitTemplate) {
        this.court = court;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Bean
    public RouterFunction<ServerResponse> deepHealthCheck() {
        return route(GET("/actuator/health/all")
            .and(accept(MediaType.APPLICATION_JSON)), this::handleAllProbes);
    }

    public Mono<ServerResponse> handleAllProbes(ServerRequest serverRequest) {
        Flux<Probe> probeFlux = Flux.concat(rabbitMQProbe(), leftPlayerProbe());
        return ServerResponse.ok().body(probeFlux, Probe.class);
    }


}
