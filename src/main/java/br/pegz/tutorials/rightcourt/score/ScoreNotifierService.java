package br.pegz.tutorials.rightcourt.score;

import br.pegz.tutorials.rightcourt.configuration.AMQPConfig;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScoreNotifierService {

    private final RabbitTemplate rabbitTemplate;

    public ScoreNotifierService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyFoePoint(Integer count) {
        try {
            log.info("Notifying point for LEFT in the play #{}", count);
            rabbitTemplate.convertAndSend(AMQPConfig.TOPIC_EXCHANGE_NAME, "scores-java", getNotifyScore(Side.LEFT, count));
        } catch (AmqpException ex) {
            log.error("Rabbit is out of reach, point is nilled");
            log.info("Point is nilled, PRACTICE MODE!!!, but was {}", getNotifyScore(Side.LEFT, count));
        }
    }

    public void notifyMyPoint(Integer count) {
        try {
            log.info("Notifying point for RIGHT in the play #{}", count);
            rabbitTemplate.convertAndSend(AMQPConfig.TOPIC_EXCHANGE_NAME, "scores-java", getNotifyScore(Side.RIGHT, count));
        } catch (AmqpException ex) {
            log.error("Rabbit is out of reach, point is nilled", ex);
            log.info("Point is nilled, PRACTICE MODE!!!, but was {}", getNotifyScore(Side.RIGHT, count));
        }
    }

    private String getNotifyScore(Side winningSide, Integer count) {
        return String.format("{\"pointWinner\":\"%s\",\"playsCount\":%d}", winningSide, count);
    }
}
