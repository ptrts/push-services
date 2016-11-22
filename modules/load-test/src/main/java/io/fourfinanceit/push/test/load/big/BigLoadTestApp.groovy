package io.fourfinanceit.push.test.load.big

import io.fourfinanceit.push.MobilePlatform
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import javax.annotation.PostConstruct

@SpringBootApplication
public class BigLoadTestApp {

    @Autowired
    private BigLoadTest bigLoadTest
    
    @Autowired
    private BigLoadTestProperties properties

    @PostConstruct
    private void postConstruct() {

        for (int i = 0; i < properties.threads; i++) {
            //bigLoadTest.sendSeriesAsync(MobilePlatform.ANDROID, '8850e8c8-52d3-4431-941c-54d5d80649ab', 100)
            bigLoadTest.sendSeriesAsync(MobilePlatform.IOS, properties.userId, 'Hello', null, i, properties.messagesPerThread)
        }
    }

    public static void main(String[] args) {
        new SpringApplication(BigLoadTestApp).run args
    }
}
