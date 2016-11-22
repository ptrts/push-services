package io.fourfinanceit.push.test.load.big

import io.fourfinanceit.push.test.load.LoadTestConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.config.TaskExecutorFactoryBean

import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableConfigurationProperties
@EnableAsync
@Import(LoadTestConfiguration)
class BigLoadTestConfiguration {

    @Bean
    @ConfigurationProperties('load-test.big')
    BigLoadTestProperties properties() {
        return new BigLoadTestProperties()
    }

    @Bean
    TaskExecutorFactoryBean sendQueueTaskExecutor(BigLoadTestProperties properties) {
        TaskExecutorFactoryBean factoryBean = new TaskExecutorFactoryBean();
        factoryBean.setPoolSize("" + properties.poolSize);
        factoryBean.setQueueCapacity(0);
        factoryBean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return factoryBean;
    }
}
