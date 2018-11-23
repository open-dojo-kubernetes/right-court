package org.open.dojo.rightcourt.configuration;

import org.open.dojo.rightcourt.configuration.model.Probe;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class ProbeConfiguration {

    private final CourtConfiguration court;

    private final RabbitTemplate rabbitTemplate;


    @Value("${spring.rabbitmq.host}")
    public String rabbitMQHost = "localhost";


    @Value("${spring.rabbitmq.port}")
    public int rabbitMQPort = 5672;

    public ProbeConfiguration(CourtConfiguration court, RabbitTemplate rabbitTemplate) {
        this.court = court;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<Probe> rabbitMQProbe() {
        return Mono.just(Probe.builder()
                .description("Rabbit MQ Integration")
                .status(rabbitTemplate.isRunning())
                .endpoint(String.format("mq://%s:%s",rabbitMQHost,rabbitMQPort))
                .build());
    }

    public Mono<Probe> leftPlayerProbe() {
        return Mono.just(Probe.builder()
                .description("Left Player Integration Micro-Service Integration")
                .status(court.checkLeftCourtStatus())
                .endpoint(CourtConfiguration.LEFT_COURT_BASE)
                .build());
    }

}
