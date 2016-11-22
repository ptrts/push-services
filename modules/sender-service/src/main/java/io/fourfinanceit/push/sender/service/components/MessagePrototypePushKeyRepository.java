package io.fourfinanceit.push.sender.service.components;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fourfinanceit.push.MobilePlatform;
import io.fourfinanceit.push.sender.service.config.SwrveProperties;

@Service
public class MessagePrototypePushKeyRepository {

    @Autowired
    private SwrveProperties swrveProperties;

    public String get(String messagePrototypeKey, MobilePlatform platform) {

        Map<String, Map<MobilePlatform, String>> messagePrototypePushKeys = swrveProperties
                .getMessagePrototypePushKeys();

        Map<MobilePlatform, String> campaignsByPlatforms = messagePrototypePushKeys.get(messagePrototypeKey);
        if (campaignsByPlatforms == null) {
            return null;
        } else {
            return campaignsByPlatforms.get(platform);
        }
    }
}
