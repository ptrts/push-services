package io.fourfinanceit.push.sender.service.components;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fourfinanceit.push.sender.service.config.PushProperties;

@Service
public class AttemptsManager {

    @Autowired
    private PushProperties pushProperties;

    public boolean isTryMore(int sendingAttempts) {
        int maxAttempts = pushProperties.getRetry().getMaxAttempts();
        return sendingAttempts < maxAttempts;
    }

    public Date nextAttemptDate() {
        return Date.from(
                ZonedDateTime.now()
                        .plusSeconds(pushProperties.getRetry().getDelayInSeconds())
                        .toInstant()
        );
    }
}
