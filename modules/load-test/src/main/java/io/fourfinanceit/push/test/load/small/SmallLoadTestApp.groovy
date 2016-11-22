package io.fourfinanceit.push.test.load.small

import io.fourfinanceit.push.MobilePlatform
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import javax.annotation.PostConstruct

@SpringBootApplication
public class SmallLoadTestApp {

    @Autowired
    private SmallLoadTest smallLoadTest

    @PostConstruct
    private void postConstruct() {
        //smallLoadTest.sendSeries(MobilePlatform.ANDROID, '8850e8c8-52d3-4431-941c-54d5d80649ab', 100)
        smallLoadTest.sendSeries(MobilePlatform.IOS, '5c058a3c-5139-42cd-8187-b08d78b79f4a', 10)
    }

    public static void main(String[] args) {
        new SpringApplication(SmallLoadTestApp).run args
    }
}
