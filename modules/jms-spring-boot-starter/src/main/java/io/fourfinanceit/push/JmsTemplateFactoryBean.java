package io.fourfinanceit.push;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class JmsTemplateFactoryBean implements FactoryBean<JmsTemplate> {

    private final ConnectionFactory connectionFactory;

    private final Queue queue;

    private MessageConverter messageConverter;

    private DestinationResolver destinationResolver;

    public JmsTemplateFactoryBean(ConnectionFactory connectionFactory, Queue queue) {
        this.connectionFactory = connectionFactory;
        this.queue = queue;
    }

    @Autowired
    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }
    
    @Autowired
    public void setDestinationResolver(DestinationResolver destinationResolver) {
        this.destinationResolver = destinationResolver;
    }

    @Override
    public JmsTemplate getObject() throws Exception {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestination(queue);
        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setDestinationResolver(destinationResolver);
        return jmsTemplate;
    }

    @Override
    public Class<?> getObjectType() {
        return JmsTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
