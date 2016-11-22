package io.fourfinanceit.push.sender.service.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Timer;
import io.fourfinanceit.push.sender.api.PushNotificationJmsDto;
import io.fourfinanceit.push.sender.service.components.exception.MessagePrototypeKeyException;
import io.fourfinanceit.push.sender.service.components.swrve.SwrveException;
import io.fourfinanceit.push.sender.service.metric.MetricReportingService;

@Service
public class AttemptMaker {

    private static final Logger log = LoggerFactory.getLogger(AttemptMaker.class);

    private final Sender sender;

    private final AttemptExceptionsProcessor exceptionsProcessor;

    private final MetricReportingService metricReportingService;

    @Autowired
    public AttemptMaker(Sender sender, AttemptExceptionsProcessor exceptionsProcessor,
                        MetricReportingService metricReportingService) {
        this.exceptionsProcessor = exceptionsProcessor;
        this.metricReportingService = metricReportingService;
        this.sender = sender;
    }

    public void doAttempt(PushNotificationJmsDto dto) {

        Timer.Context timerContext = metricReportingService.getSendingTimerContext();

        try {

            log.debug("doAttempt, start: {}", dto);

            dto.setAttemptsMade(dto.getAttemptsMade() + 1);

            sender.send(dto);

            metricReportingService.sendingSuccess();

        } catch (SwrveException e) {

            exceptionsProcessor.sendException(dto, e);

        } catch (MessagePrototypeKeyException e) {

            exceptionsProcessor.sendException(dto, e);

        } catch (RuntimeException e) {

            exceptionsProcessor.sendException(dto, e);

        } finally {
            timerContext.stop();
            log.debug("doAttempt, end: {}", dto);
        }
    }
}
