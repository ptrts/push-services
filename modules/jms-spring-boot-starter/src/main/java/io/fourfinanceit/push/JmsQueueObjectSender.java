package io.fourfinanceit.push;

import java.util.Date;
import javax.jms.Message;
import javax.jms.QueueSender;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

public class JmsQueueObjectSender {

    private final JmsTemplate jmsTemplate;

    public JmsQueueObjectSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void convertAndSend(Object o, Date date) {
        convertAndSend(o, date.getTime() - System.currentTimeMillis());
    }

    public void convertAndSend(Object o) {
        convertAndSend(o, 0);
    }

    public void convertAndSend(Object o, long deliveryDelay) {
        jmsTemplate.execute((session, producer) -> {

            MessageConverter messageConverter = jmsTemplate.getMessageConverter();

            Message jmsMessage = messageConverter.toMessage(o, session);

            QueueSender queueSender = (QueueSender) producer;
            queueSender.setDeliveryDelay(deliveryDelay);
            queueSender.send(jmsMessage);

            return null;
        });
    }
}
