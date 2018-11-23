package org.open.dojo.rightcourt.score;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ScoreNotifierServiceTest {

    @Autowired
    ScoreNotifierService scoreNotifierService;
    @MockBean
    RabbitTemplate rabbitTemplate;

    @Test
    void whenNotifyFoePoint_Success() {
        scoreNotifierService.notifyFoePoint(1);
        Mockito.verify(rabbitTemplate).convertAndSend(anyString(), eq("scores-java"),
                eq("{\"pointWinner\":\"LEFT\",\"playsCount\":1}"));
    }

    @Test
    void whenNotifyFoePoint_Error() {
        scoreNotifierService.notifyFoePoint(1);
        Mockito.doThrow(AmqpException.class).when(rabbitTemplate).convertAndSend(anyString(), eq("scores-java"),
                eq("{\"pointWinner\":\"LEFT\",\"playsCount\":1}"));
        Mockito.verify(rabbitTemplate).convertAndSend(anyString(), eq("scores-java"),
                eq("{\"pointWinner\":\"LEFT\",\"playsCount\":1}"));
    }

    @Test
    void whenNotifyMyPoint_Error() {
        scoreNotifierService.notifyMyPoint(1);
        Mockito.doThrow(AmqpException.class).when(rabbitTemplate).convertAndSend(anyString(), eq("scores-java"),
                eq("{\"pointWinner\":\"RIGHT\",\"playsCount\":1}"));
        Mockito.verify(rabbitTemplate).convertAndSend(anyString(), eq("scores-java"),
                eq("{\"pointWinner\":\"RIGHT\",\"playsCount\":1}"));
    }

    @Test
    void whenNotifyMyPoint_Success() {
        scoreNotifierService.notifyMyPoint(1);
        Mockito.verify(rabbitTemplate).convertAndSend(anyString(), eq("scores-java"),
                eq("{\"pointWinner\":\"RIGHT\",\"playsCount\":1}"));
    }
}