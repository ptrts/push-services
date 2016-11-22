package io.fourfinanceit.push.test.load

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class LoadTestConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate()
    }

    @Bean
    PushServiceSender pushServiceSender(RestTemplate restTemplate, @Value('${port:8095}') String port) {
        return new PushServiceSender(restTemplate, port)
    }
}
