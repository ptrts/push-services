package io.fourfinanceit.push.sender.service.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.PlatformTransactionManager;

import io.fourfinanceit.push.JmsQueueObjectSenderFactoryBean;
import io.fourfinanceit.push.JmsTemplateFactoryBean;

@Configuration
public class JmsConfig {

    @Bean(name = "failedMessagesQueue")
    Queue failedMessagesQueue(PushProperties pushProperties) {
        return ActiveMQJMSClient.createQueue(pushProperties.getFailedMessagesQueueName());
    }

    @Bean
    @Qualifier("receive")
    JmsTemplateFactoryBean receiveJmsTemplate(
            @Qualifier("receive") ConnectionFactory connectionFactory,
            @Qualifier("mainQueue") Queue queue
    ) {
        return new JmsTemplateFactoryBean(connectionFactory, queue);
    }

    @Bean
    @Qualifier("mainQueue")
    JmsQueueObjectSenderFactoryBean sendJmsQueueObjectSender(
            @Qualifier("send") ConnectionFactory connectionFactory,
            @Qualifier("mainQueue") Queue queue
    ) {
        return new JmsQueueObjectSenderFactoryBean(connectionFactory, queue);
    }
    
    @Bean
    @Qualifier("failedMessagesQueue")
    JmsQueueObjectSenderFactoryBean failedMessagesJmsQueueObjectSender(
            @Qualifier("send") ConnectionFactory connectionFactory,
            @Qualifier("failedMessagesQueue") Queue queue
    ) {
        return new JmsQueueObjectSenderFactoryBean(connectionFactory, queue);
    }
    
    @Bean(name = "chainedJmsTransactionManager")
    PlatformTransactionManager chainedJmsTransactionManager(
            @Qualifier("receiveJmsTransactionManager") PlatformTransactionManager receiveJmsTransactionManager,
            @Qualifier("sendJmsTransactionManager") PlatformTransactionManager sendJmsTransactionManager
    ) {
        return new ChainedTransactionManager(receiveJmsTransactionManager, sendJmsTransactionManager);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            @Qualifier("receive") ConnectionFactory connectionFactory,
            @Qualifier("chainedJmsTransactionManager") PlatformTransactionManager transactionManager,
            DestinationResolver destinationResolver,
            MessageConverter messageConverter,
            SwrveProperties swrveProperties
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setTransactionManager(transactionManager);
        factory.setDestinationResolver(destinationResolver);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrency("" + swrveProperties.getSendingThreads());
        return factory;
    }
}
