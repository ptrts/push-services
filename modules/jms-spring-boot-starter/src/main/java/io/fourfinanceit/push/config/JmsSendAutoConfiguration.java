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
@ConditionalOnExpression("${jms.send.enable:false}")
@Import(JmsBaseConfiguration.class)
public class JmsSendAutoConfiguration {

    @Bean
    @Qualifier("send")
    ConnectionFactory sendConnectionFactory(
            @Qualifier("broker") ConnectionFactory brokerConnectionFactory,
            JmsProperties jmsProperties
    ) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(brokerConnectionFactory);
        cachingConnectionFactory.setSessionCacheSize(jmsProperties.getSend().getSessionFactory().getCacheSize());
        return cachingConnectionFactory;
    }

    @Bean(name = "sendJmsTransactionManager")
    PlatformTransactionManager sendJmsTransactionManager(@Qualifier("send") ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }
}
