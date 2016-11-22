package io.fourfinanceit.push.test.load

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
public class Shutdown implements ApplicationListener<ApplicationReadyEvent> {
    
    @Autowired
    private ApplicationContext applicationContext
    
    @Override
    void onApplicationEvent(ApplicationReadyEvent event) {
        SpringApplication.exit(applicationContext);
    }
}
