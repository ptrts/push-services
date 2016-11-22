package io.fourfinanceit.push.receiver.service.components;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Timer;
import io.fourfinanceit.push.JmsQueueObjectSender;
import io.fourfinanceit.push.receiver.api.PushNotificationDto;
import io.fourfinanceit.push.receiver.service.metric.MetricReportingService;
import io.fourfinanceit.push.sender.api.PushNotificationJmsDto;

@Service
public class Acceptor {

    private static final Logger log = LoggerFactory.getLogger(Acceptor.class);

    private final CronUtils cronUtils;

    private final JmsQueueObjectSender jmsObjectSender;

    private final MetricReportingService metricReportingService;
    
    @Autowired
    public Acceptor(CronUtils cronUtils, JmsQueueObjectSender jmsObjectSender, MetricReportingService metricReportingService) {
        this.cronUtils = cronUtils;
        this.jmsObjectSender = jmsObjectSender;
        this.metricReportingService = metricReportingService;
    }
    
    public void accept(PushNotificationDto dto) {

        log.debug("accept, start: {}", dto);

        Timer.Context timerContext = metricReportingService.getReceivingTimerContext();

        try {

            metricReportingService.pushReceived();
            
            if (dto.getCronExpression() == null) {
                metricReportingService.immediatePushReceived();
            } else {
                metricReportingService.scheduledPushReceived();
            }

            convertAndSend(dto);
            
            metricReportingService.pushSent();
            
        } catch (Exception e) {
            log.error("Exception while receiving", e);
            metricReportingService.notStoredExceptionWhileReceiving();
        } finally {
            log.debug("accept, end: {}", dto);
            timerContext.stop();
        }
    }

    private void convertAndSend(PushNotificationDto dto) {
        
        PushNotificationJmsDto jmsQueueDto = convert(dto);

        String cronExpression = dto.getCronExpression();

        long deliveryDelay = getDeliveryDelay(cronExpression);

        jmsObjectSender.convertAndSend(jmsQueueDto, deliveryDelay);
    }

    private long getDeliveryDelay(String cronExpression) {
        if (cronExpression == null) {
            return 0;
        } else {
            Date nextDate = cronUtils.getNextDateByCronSchedule(cronExpression);
            return nextDate.getTime() - System.currentTimeMillis();
        }
    }

    private PushNotificationJmsDto convert(PushNotificationDto dto) {

        PushNotificationJmsDto dto2 = new PushNotificationJmsDto();

        //@formatter:off
        dto2.setMessage(dto.getMessage());
        dto2.setMessagePrototypeKey(dto.getMessagePrototypeKey());
        dto2.setPlatform(dto.getPlatform());
        dto2.setUserId(dto.getPushKey());
        dto2.setAttemptsMade(0);
        //@formatter:on

        return dto2;
    }
}
