package io.fourfinanceit.push.components

import com.ofg.infrastructure.discovery.web.HttpMockServer
import io.fourfinanceit.push.sender.service.config.SwrveProperties
import io.fourfinanceit.push.sender.service.config.UrlProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestUrlProviderConfiguration {

    @Bean
    @Primary
    UrlProvider urlProvider(HttpMockServer httpMockServer, SwrveProperties swrveProperties) {
        return new UrlProvider("http://localhost:" + httpMockServer.port(), swrveProperties.path);
    }
}
