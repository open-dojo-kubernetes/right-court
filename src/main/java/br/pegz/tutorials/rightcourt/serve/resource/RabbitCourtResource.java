package br.pegz.tutorials.rightcourt.serve.resource;

import br.pegz.tutorials.rightcourt.persistence.Play;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@Primary
public class RabbitCourtResource implements CourtResource {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitCourtResource(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Play sendPlayToOtherSide(Play myPlay) {
        if (rabbitTemplate.isRunning()) {
            rabbitTemplate.convertAndSend("left-play-queue", myPlay);
        }
        return receivePlayFromCourt();
    }

    private Play receivePlayFromCourt() {
        return rabbitTemplate.receiveAndConvert("right-play-queue", ParameterizedTypeReference.forType(Play.class));
    }
}
