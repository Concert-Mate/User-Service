package ru.nsu.concerts_mate.users_service.services.broker.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;
import ru.nsu.concerts_mate.users_service.services.broker.BrokerEvent;
import ru.nsu.concerts_mate.users_service.services.broker.BrokerException;
import ru.nsu.concerts_mate.users_service.services.broker.BrokerService;

@Service
@RequiredArgsConstructor
public class RabbitMqBrokerImpl implements BrokerService {
    private final AmqpTemplate template;
    private final Queue queue;

    @Override
    public void sendEvent(BrokerEvent event) throws BrokerException {
        try {
            template.convertAndSend(queue.getName(), event);
        } catch (AmqpException e) {
            throw new BrokerException("Sending event failed", e);
        }
    }
}
