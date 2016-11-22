package io.fourfinanceit.push.sender.service.config;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.TaskExecutorFactoryBean;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties({PushProperties.class,SwrveProperties.class})
@EnableScheduling
@EnableJms
public class MainConfig {

    @Bean
    UrlProvider urlProvider(SwrveProperties swrveProperties) {
        return new UrlProvider(swrveProperties.getUrl(), swrveProperties.getPath());
    }

    @Bean
    TaskExecutorFactoryBean sendQueueTaskExecutor(SwrveProperties swrveProperties) {
        TaskExecutorFactoryBean factoryBean = new TaskExecutorFactoryBean();
        factoryBean.setPoolSize("" + swrveProperties.getSendingThreads());
        factoryBean.setQueueCapacity(0);
        factoryBean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return factoryBean;
    }
    
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
