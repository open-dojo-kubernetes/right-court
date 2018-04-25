package br.pegz.tutorials.rightcourt.score;

import br.pegz.tutorials.rightcourt.configuration.AMQPConfig;
import br.pegz.tutorials.rightcourt.persistence.enums.Side;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.net.ConnectException;

@Slf4j
@Service
public class ScoreNotifierService {

    private final RabbitTemplate rabbitTemplate;

    public ScoreNotifierService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyFoePoint(Integer count) {
        try {
            rabbitTemplate.convertAndSend(AMQPConfig.topicExchangeName, "score-notify-" + count, getNotifyScore(Side.LEFT, count));
        } catch (Exception ex) {
            log.error("Rabbit is out of reach, point is nulled");
            log.debug("Point is nulled, PRACTICE MODE!!!, but was {}", getNotifyScore(Side.LEFT, count));
        }
    }

    public void notifyMyPoint(Integer count) {
        try {
            rabbitTemplate.convertAndSend(AMQPConfig.topicExchangeName, "score-notify-" + count, getNotifyScore(Side.RIGHT, count));
        } catch (Exception ex) {
            log.error("Rabbit is out of reach, point is nulled", ex);
            log.debug("Point is nulled, PRACTICE MODE!!!, but was {}", getNotifyScore(Side.RIGHT, count));
        }
    }

    private String getNotifyScore(Side winningSide, Integer count) {
        return String.format("{\"pointWinner\":\"%s\",\"playsCount\":%d}", winningSide, count);
    }
}
