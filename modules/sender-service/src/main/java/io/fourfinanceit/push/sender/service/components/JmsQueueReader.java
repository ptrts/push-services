package io.fourfinanceit.push.sender.service.components;

import static org.slf4j.LoggerFactory.*;

import java.util.concurrent.Semaphore;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import io.fourfinanceit.push.sender.api.PushNotificationJmsDto;
import io.fourfinanceit.push.sender.service.config.SwrveProperties;
import io.fourfinanceit.push.sender.service.metric.MetricReportingService;

@Service
public class JmsQueueReader {

    private static final Logger log = getLogger(JmsQueueReader.class);

    private final AttemptMaker attemptMaker;

    private final MetricReportingService metricReportingService;

    private final SwrveProperties swrveProperties;

    private Semaphore semaphore;
    
    @Autowired
    public JmsQueueReader(
            AttemptMaker attemptMaker,
            MetricReportingService metricReportingService,
            SwrveProperties swrveProperties) {
        this.attemptMaker = attemptMaker;
        this.metricReportingService = metricReportingService;
        this.swrveProperties = swrveProperties;
    }
    
    @PostConstruct
    private void postConstruct() {
        semaphore = new Semaphore(swrveProperties.getThreadsPortion(), true);
    }
    
    @SuppressWarnings("unused")
    @JmsListener(destination = "mainQueue")
    private void receiveMessage(PushNotificationJmsDto jmsDto) {

        try {
            log.debug("receiveMessage, start: {}", jmsDto);
            metricReportingService.threadPoolSizeInc();
            
            try {
                metricReportingService.waitingThreadsInc();
                semaphore.acquire();
            } catch (InterruptedException e) {
                // the app is shutting down
                throw Throwables.propagate(e);
            } finally {
                metricReportingService.waitingThreadsDec();
            }
            
            try {
                log.debug("receiveMessage, start sending: {}", jmsDto);
                metricReportingService.sendingThreadsInc();
                attemptMaker.doAttempt(jmsDto);
            } finally {
                metricReportingService.sendingThreadsDec();
            }
        } finally {
            log.debug("receiveMessage, end", jmsDto);
            metricReportingService.threadPoolSizeDec();
        }
    }

    @Scheduled(fixedDelayString = "${swrve.delayMillis}")
    private void letPortionGo() {
        semaphore.release(swrveProperties.getThreadsPortion());
    }
}
