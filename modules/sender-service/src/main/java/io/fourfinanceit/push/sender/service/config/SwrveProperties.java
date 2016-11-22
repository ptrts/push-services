package io.fourfinanceit.push.sender.service.config;

import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.fourfinanceit.push.MobilePlatform;

@ConfigurationProperties("swrve")
public class SwrveProperties {

    private boolean test;

    private String url;

    private String path;

    private int averageProcessingMillis;

    private int delayMillis;

    private Map<String, Map<MobilePlatform, String>> messagePrototypePushKeys = new TreeMap<>();
    
    private int threadsPortion;
    
    private int sendingThreads;

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAverageProcessingMillis() {
        return averageProcessingMillis;
    }

    public void setAverageProcessingMillis(int averageProcessingMillis) {
        this.averageProcessingMillis = averageProcessingMillis;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public Map<String, Map<MobilePlatform, String>> getMessagePrototypePushKeys() {
        return messagePrototypePushKeys;
    }

    public void setMessagePrototypePushKeys(
            Map<String, Map<MobilePlatform, String>> messagePrototypePushKeys) {
        this.messagePrototypePushKeys = messagePrototypePushKeys;
    }

    public int getThreadsPortion() {
        return threadsPortion;
    }

    public void setThreadsPortion(int threadsPortion) {
        this.threadsPortion = threadsPortion;
    }

    public int getSendingThreads() {
        return sendingThreads;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        Double ceil = Math.ceil(1.0 * averageProcessingMillis / delayMillis * threadsPortion);
        sendingThreads = ceil.intValue();
    }
}
