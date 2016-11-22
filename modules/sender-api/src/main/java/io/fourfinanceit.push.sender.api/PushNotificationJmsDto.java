package io.fourfinanceit.push.sender.api;

import java.io.Serializable;

import io.fourfinanceit.push.MobilePlatform;

public class PushNotificationJmsDto implements Serializable {

    private MobilePlatform platform;

    private String messagePrototypeKey;

    private String userId;

    private String message;

    private int attemptsMade = 0;

    public MobilePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(MobilePlatform platform) {
        this.platform = platform;
    }

    public String getMessagePrototypeKey() {
        return messagePrototypeKey;
    }

    public void setMessagePrototypeKey(String messagePrototypeKey) {
        this.messagePrototypeKey = messagePrototypeKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAttemptsMade() {
        return attemptsMade;
    }

    public void setAttemptsMade(int attemptsMade) {
        this.attemptsMade = attemptsMade;
    }

    @Override
    public String toString() {
        return "PushNotificationJmsDto{" + 
                "platform=" + platform +
                ", messagePrototypeKey=" + messagePrototypeKey + 
                ", userId=" + userId + 
                ", message=" + message + 
                ", attemptsMade=" + attemptsMade +
                "}";
    }
}
