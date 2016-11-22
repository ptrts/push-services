package io.fourfinanceit.push;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class JmsQueueObjectSenderFactoryBean implements FactoryBean<JmsQueueObjectSender> {

    @Autowired
    private MessageConverter messageConverter;

    @Autowired
    private DestinationResolver destinationResolver;

    private final ConnectionFactory connectionFactory;

    private final Queue queue;

    public JmsQueueObjectSenderFactoryBean(ConnectionFactory connectionFactory, Queue queue) {
        this.connectionFactory = connectionFactory;
        this.queue = queue;
    }

    private JmsTemplate getJmsTemplate() throws Exception {
        JmsTemplateFactoryBean jmsTemplateFactoryBean = new JmsTemplateFactoryBean(connectionFactory, queue);
        jmsTemplateFactoryBean.setMessageConverter(messageConverter);
        jmsTemplateFactoryBean.setDestinationResolver(destinationResolver);
        return jmsTemplateFactoryBean.getObject();
    }
    
    @Override
    public JmsQueueObjectSender getObject() throws Exception {
        JmsTemplate jmsTemplate = getJmsTemplate();
        return new JmsQueueObjectSender(jmsTemplate);
    }

    @Override
    public Class<?> getObjectType() {
        return JmsQueueObjectSender.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
