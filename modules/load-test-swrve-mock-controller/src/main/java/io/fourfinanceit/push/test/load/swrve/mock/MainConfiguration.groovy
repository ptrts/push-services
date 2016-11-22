package io.fourfinanceit.push.test.load.swrve.mock

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties
class MainConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate()
    }
}
