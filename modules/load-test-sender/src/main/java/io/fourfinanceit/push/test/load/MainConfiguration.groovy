package io.fourfinanceit.push.test.load

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.config.TaskExecutorFactoryBean
import org.springframework.web.client.RestTemplate

import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableConfigurationProperties
@EnableAsync
class MainConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate()
    }

    @Bean
    Sender pushServiceSender(RestTemplate restTemplate, @Value('${load-test.sender.port}') String port) {
        return new Sender(restTemplate, port)
    }
    
    @Bean
    @ConfigurationProperties('load-test.sender')
    LoadTestSenderProperties properties() {
        return new LoadTestSenderProperties()
    }

    @Bean
    TaskExecutorFactoryBean sendQueueTaskExecutor(LoadTestSenderProperties properties) {
        TaskExecutorFactoryBean factoryBean = new TaskExecutorFactoryBean()
        factoryBean.setPoolSize("" + properties.poolSize)
        factoryBean.setQueueCapacity(0)
        factoryBean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
        return factoryBean
    }
}
