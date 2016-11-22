package io.fourfinanceit.push.receiver.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;

import com.ofg.config.BasicProfiles;
import com.ofg.infrastructure.environment.EnvironmentSetupVerifier;

@SpringBootApplication(exclude = {ArtemisAutoConfiguration.class, JmsAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);

        application.addListeners(new EnvironmentSetupVerifier(BasicProfiles.all()));
        application.run(args);
    }
}
