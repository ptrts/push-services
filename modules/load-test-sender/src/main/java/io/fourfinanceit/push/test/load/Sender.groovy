package io.fourfinanceit.push.test.load

import io.fourfinanceit.push.MobilePlatform
import io.fourfinanceit.push.receiver.api.PushNotificationDto
import io.fourfinanceit.push.receiver.api.PushServiceMediaTypes
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate

class Sender {

    RestTemplate restTemplate

    String port

    Sender(RestTemplate restTemplate, String port) {
        this.restTemplate = restTemplate
        this.port = port
    }

    void sendPush(MobilePlatform platform, String messagePrototypeKey, String userId, String message, String cronExpression) {

        PushNotificationDto dto = new PushNotificationDto(platform, messagePrototypeKey, userId, message, cronExpression)
        
        try {
            restTemplate.exchange(

                    RequestEntity
                            .post(URI.create("http://localhost:$port/push"))
                            .contentType(MediaType.valueOf(PushServiceMediaTypes.JSON_V1))
                            .accept(MediaType.APPLICATION_JSON)
                            .body(dto),

                    Object.class
            )
        } catch (Exception ignored) {
            println '===== ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR'
            println "ERROR: $message"
        }
    }
}
