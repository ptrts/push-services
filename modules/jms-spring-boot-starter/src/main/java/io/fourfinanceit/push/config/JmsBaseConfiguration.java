package io.fourfinanceit.push.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;

import io.fourfinanceit.push.JmsProperties;

@Configuration
@EnableConfigurationProperties({JmsProperties.class})
public class JmsBaseConfiguration {
    
    @Bean
    @Qualifier("broker")
    ConnectionFactory brokerConnectionFactory(JmsProperties jmsProperties) {
        
        JmsProperties.Broker broker = jmsProperties.getBroker();
        
        ActiveMQQueueConnectionFactory connectionFactory = new ActiveMQQueueConnectionFactory(
                broker.getUrl(), broker.getUser(), broker.getPassword()
        );

        connectionFactory.setProducerMaxRate(1000000);
        connectionFactory.setConsumerMaxRate(1000000);
        
        return connectionFactory;
    }
    
    @Bean(name = "mainQueue")
    Queue mainQueue(JmsProperties jmsProperties) {
        return ActiveMQJMSClient.createQueue(jmsProperties.getQueue().getName());
    }

    @Bean
    MessageConverter jacksonMessageConverter() {

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        return converter;
    }
    
    @Bean
    DestinationResolver destinationResolver(BeanFactory beanFactory) {
        return new BeanFactoryDestinationResolver(beanFactory);
    }
}
