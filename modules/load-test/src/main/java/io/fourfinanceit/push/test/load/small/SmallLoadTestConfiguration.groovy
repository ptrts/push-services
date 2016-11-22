package io.fourfinanceit.push.test.load.small

import io.fourfinanceit.push.test.load.LoadTestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@Import(LoadTestConfiguration)
class SmallLoadTestConfiguration {

    @Bean
    TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler()
        scheduler.poolSize = 100
        return scheduler
    }
}
