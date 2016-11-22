package io.fourfinanceit.push.receiver.service.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fourfinanceit.push.JmsProperties;
import io.fourfinanceit.push.JmsQueueObjectSenderFactoryBean;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class JmsConfig {

    @Bean
    JmsQueueObjectSenderFactoryBean jmsQueueObjectSender(
            @Qualifier("send") ConnectionFactory connectionFactory,
            @Qualifier("mainQueue") Queue queue
    ) {
        return new JmsQueueObjectSenderFactoryBean(connectionFactory, queue);
    }
}
