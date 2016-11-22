package io.fourfinanceit.push.sender.service.components;

import static org.slf4j.LoggerFactory.*;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fourfinanceit.push.MobilePlatform;
import io.fourfinanceit.push.sender.api.PushNotificationJmsDto;
import io.fourfinanceit.push.sender.service.components.exception.MessagePrototypeKeyException;
import io.fourfinanceit.push.sender.service.components.swrve.SwrveException;
import io.fourfinanceit.push.sender.service.components.swrve.SwrvePushApi;

@Service
public class Sender {

    private static final Logger log = getLogger(Sender.class);

    private final SwrvePushApi swrvePushApi;

    private final MessagePrototypePushKeyRepository messagePrototypePushKeyRepository;

    @Autowired
    public Sender(SwrvePushApi swrvePushApi, MessagePrototypePushKeyRepository messagePrototypePushKeyRepository) {
        this.swrvePushApi = swrvePushApi;
        this.messagePrototypePushKeyRepository = messagePrototypePushKeyRepository;
    }

    private String getPushKey(MobilePlatform platform, String messagePrototypeKey) throws MessagePrototypeKeyException {

        if (messagePrototypeKey == null) {
            throw new MessagePrototypeKeyException("Message prototype key is null");
        }

        String pushKey = messagePrototypePushKeyRepository.get(messagePrototypeKey, platform);

        if (pushKey == null) {
            String errorMsg = String.format(
                    "No transactional campaign configured " +
                            "for message prototype key %s " +
                            "and platform %s",
                    messagePrototypeKey, platform);
            throw new MessagePrototypeKeyException(errorMsg);
        } else {
            return pushKey;
        }
    }

    public void send(PushNotificationJmsDto dto) throws MessagePrototypeKeyException, SwrveException {

        log.debug("send, start: {}", dto);

        MobilePlatform mobilePlatform = dto.getPlatform();
        String messagePrototypeKey = dto.getMessagePrototypeKey();
        String userId = dto.getUserId();
        String message = dto.getMessage();

        try {
            String pushKey = getPushKey(mobilePlatform, messagePrototypeKey);

            log.debug("send, pushKey = {}", pushKey);

            swrvePushApi.sendPush(pushKey, userId, message);
        } finally {
            log.debug("send, end: {}", dto);
        }
    }
}
