package io.fourfinanceit.push.test.load.big

import io.fourfinanceit.push.MobilePlatform
import io.fourfinanceit.push.test.load.PushServiceSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

import java.time.ZonedDateTime

@Component
class BigLoadTest {

    @Autowired
    PushServiceSender senderToPushService

    def sendPush(String message, MobilePlatform platform, String userId, String messagePrototypeKey, String cronExpression) {

        println '' + ZonedDateTime.now() + ' - ' + message

        senderToPushService.sendPush(platform, messagePrototypeKey, userId, message, cronExpression)
    }

    @Async
    def sendSeriesAsync(MobilePlatform platform, String userId, String messagePrototypeKey, String cronExpression, int seriesIndex, int n) {
        for (int messageIndex = 0; messageIndex < n; messageIndex++) {
            sendPush("We send you ${seriesIndex + 1}-${messageIndex + 1}", platform, userId, messagePrototypeKey, cronExpression)
        }
    }
}
