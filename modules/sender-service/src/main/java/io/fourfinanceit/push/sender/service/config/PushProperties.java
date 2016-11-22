package io.fourfinanceit.push.sender.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("push")
public class PushProperties {

    private String failedMessagesQueueName;
    
    private Retry retry = new Retry();
    
    public class Retry {
        
        private int maxAttempts;
        
        private int delayInSeconds;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public int getDelayInSeconds() {
            return delayInSeconds;
        }

        public void setDelayInSeconds(int delayInSeconds) {
            this.delayInSeconds = delayInSeconds;
        }
    }

    public String getFailedMessagesQueueName() {
        return failedMessagesQueueName;
    }

    public void setFailedMessagesQueueName(String failedMessagesQueueName) {
        this.failedMessagesQueueName = failedMessagesQueueName;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }
}
