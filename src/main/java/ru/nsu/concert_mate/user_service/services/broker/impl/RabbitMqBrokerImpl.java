package ru.nsu.concert_mate.user_service.services.broker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.services.broker.BrokerEvent;
import ru.nsu.concert_mate.user_service.services.broker.BrokerException;
import ru.nsu.concert_mate.user_service.services.broker.BrokerService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqBrokerImpl implements BrokerService {
    private final AmqpTemplate template;
    private final Queue queue;

    @Override
    public void sendEvent(BrokerEvent event) throws BrokerException {
        try {
            template.convertAndSend(queue.getName(), event);
            log.info("event {} successfully send", event);
        } catch (AmqpException e) {
            log.error("can't send event because {}", e.getLocalizedMessage());
            throw new BrokerException("Sending event failed", e);
        }
    }
}
