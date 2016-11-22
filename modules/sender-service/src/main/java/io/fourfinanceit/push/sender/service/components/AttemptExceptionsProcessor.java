package io.fourfinanceit.push.sender.service.components;

import static org.slf4j.LoggerFactory.*;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.fourfinanceit.push.JmsQueueObjectSender;
import io.fourfinanceit.push.sender.api.PushNotificationJmsDto;
import io.fourfinanceit.push.sender.service.components.exception.MessagePrototypeKeyException;
import io.fourfinanceit.push.sender.service.components.swrve.SwrveException;
import io.fourfinanceit.push.sender.service.metric.MetricReportingService;
import io.fourfinanceit.push.swrve.api.SwrveResponseDto;

@Component
public class AttemptExceptionsProcessor {

    private static final Logger log = getLogger(AttemptExceptionsProcessor.class);

    private final List<Integer> HTTP_STATUS_CODES_TO_RETRY = Arrays.asList(400, 429, 500, 502, 503, 504);

    @Autowired
    private AttemptsManager attemptsManager;

    @Autowired
    private MetricReportingService metricReportingService;

    @Autowired
    @Qualifier("mainQueue")
    private JmsQueueObjectSender mainJmsSender;

    @Autowired
    @Qualifier("failedMessagesQueue")
    private JmsQueueObjectSender failedMessagesJmsSender;

    private void retryInternal(PushNotificationJmsDto pushNotification) {
        
        log.debug("retryInternal: {}", pushNotification);
        
        mainJmsSender.convertAndSend(pushNotification, attemptsManager.nextAttemptDate());
    }

    private void giveUpInternal(PushNotificationJmsDto pushNotification) {

        metricReportingService.sendingGivenUp();
        
        log.debug("giveUpInternal: {}", pushNotification);

        failedMessagesJmsSender.convertAndSend(pushNotification);
    }

    private void attemptFailed(PushNotificationJmsDto pushNotification) {

        metricReportingService.sendingAttemptFailed();

        boolean tryMore = attemptsManager.isTryMore(pushNotification.getAttemptsMade());

        if (tryMore) {
            retryInternal(pushNotification);
        } else {
            giveUpInternal(pushNotification);
        }
    }

    private void fail(PushNotificationJmsDto pushNotification) {

        metricReportingService.sendingFail();

        log.debug("fail: {}", pushNotification);
        
        failedMessagesJmsSender.convertAndSend(pushNotification);
    }

    private void logException(PushNotificationJmsDto pushNotification, Throwable e) {
        log.error("Sending push notification error: {}", pushNotification, e);
    }

    public void sendException(PushNotificationJmsDto pushNotification, SwrveException e) {

        metricReportingService.swrveSendingError();

        logException(pushNotification, e);

        SwrveResponseDto swrveResponseDto = e.getSwrveResponse();

        if (HTTP_STATUS_CODES_TO_RETRY.contains(swrveResponseDto.getCode())) {
            attemptFailed(pushNotification);
        } else {
            fail(pushNotification);
        }
    }

    public void sendException(PushNotificationJmsDto pushNotification, MessagePrototypeKeyException e) {

        metricReportingService.messagePrototypeKeySendingError();

        logException(pushNotification, e);

        fail(pushNotification);
    }

    public void sendException(PushNotificationJmsDto pushNotification, Throwable e) {

        metricReportingService.otherSendingError();

        logException(pushNotification, e);

        fail(pushNotification);
    }
}
