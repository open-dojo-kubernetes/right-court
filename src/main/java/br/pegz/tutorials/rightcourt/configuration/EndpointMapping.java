package br.pegz.tutorials.rightcourt.configuration;

import br.pegz.tutorials.rightcourt.configuration.model.Probe;
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

    private final ProbeConfiguration probeConfiguration;

    public EndpointMapping(ProbeConfiguration probeConfiguration) {
        this.probeConfiguration = probeConfiguration;
    }

    @Bean
    public RouterFunction<ServerResponse> deepHealthCheck() {
        return route(GET("/actuator/health/all")
            .and(accept(MediaType.APPLICATION_JSON)), this::handleAllProbes);
    }

    private Mono<ServerResponse> handleAllProbes(ServerRequest serverRequest) {
        Flux<Probe> probeFlux = Flux.concat(probeConfiguration.rabbitMQProbe(), probeConfiguration.leftPlayerProbe());
        return ServerResponse.ok().body(probeFlux, Probe.class);
    }
}
