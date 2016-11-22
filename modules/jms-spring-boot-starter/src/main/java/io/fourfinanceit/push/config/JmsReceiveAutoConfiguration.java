package io.fourfinanceit.push.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import io.fourfinanceit.push.JmsProperties;

@Configuration
@ConditionalOnExpression("${jms.receive.enable:false}")
@Import(JmsBaseConfiguration.class)
public class JmsReceiveAutoConfiguration {
    
    @Bean
    @Qualifier("receive")
    ConnectionFactory receiveConnectionFactory(
            @Qualifier("broker") ConnectionFactory brokerConnectionFactory,
            JmsProperties jmsProperties
    ) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(brokerConnectionFactory);
        cachingConnectionFactory.setSessionCacheSize(jmsProperties.getReceive().getSessionFactory().getCacheSize());
        return cachingConnectionFactory;
    }

    @Bean(name = "receiveJmsTransactionManager")
    PlatformTransactionManager receiveJmsTransactionManager(@Qualifier("receive") ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }
}
