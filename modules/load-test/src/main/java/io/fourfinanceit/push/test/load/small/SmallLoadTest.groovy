package io.fourfinanceit.push.test.load.small

import io.fourfinanceit.push.MobilePlatform
import io.fourfinanceit.push.test.load.PushServiceSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Component
class SmallLoadTest {

    @Autowired
    TaskScheduler scheduler

    @Autowired
    PushServiceSender senderToPushService

    private def sendPush(String message, MobilePlatform platform, String userId) {

        println '---------------------------------------------------------------------------'
        println '' + ZonedDateTime.now() + ' - ' + message

        String messagePrototypeKey = 'Hello'

        String cronExpression = null

        senderToPushService.sendPush(platform, messagePrototypeKey, userId, message, cronExpression)
    }

    private def schedulePush(MobilePlatform platform, String userId, ZonedDateTime start, int i) {

        String message = 'We send you ' + i

        Date date = Date.from(
                start
                        .plus(i * 1, ChronoUnit.MICROS)
                        .toInstant()
        )

        scheduler.schedule({ sendPush(message, platform, userId) }, date)
    }

    def sendSeries(MobilePlatform platform, String userId, int n) {
        ZonedDateTime start = ZonedDateTime.now()
        for (int i = 0; i < n; i++) {
            schedulePush(platform, userId, start, i + 1)
        }
        Thread.sleep(1000 + 200 * n)
    }
}
