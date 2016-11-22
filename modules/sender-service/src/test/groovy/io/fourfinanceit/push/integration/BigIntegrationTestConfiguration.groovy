package io.fourfinanceit.push.integration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.config.TaskExecutorFactoryBean

import java.util.concurrent.ThreadPoolExecutor

@Configuration
@ComponentScan('io.fourfinanceit.push.integration.load.big')
class BigIntegrationTestConfiguration {

    @Bean
    TaskExecutorFactoryBean controllerAsyncTaskExecutor() {
        TaskExecutorFactoryBean factoryBean = new TaskExecutorFactoryBean();
        factoryBean.setPoolSize("1000");
        factoryBean.setQueueCapacity(0);
        factoryBean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return factoryBean;
    }
}
