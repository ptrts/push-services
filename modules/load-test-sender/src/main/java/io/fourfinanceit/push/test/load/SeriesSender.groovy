package io.fourfinanceit.push.test.load

import io.fourfinanceit.push.MobilePlatform
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Component

import java.time.ZonedDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

@Component
class SeriesSender {

    @Autowired
    Sender pushServiceSender

    def sendPush(String message, MobilePlatform platform, String userId, String messagePrototypeKey, String cronExpression) {

        println '' + ZonedDateTime.now() + ' - ' + message

        pushServiceSender.sendPush(platform, messagePrototypeKey, userId, message, cronExpression)
    }

    @Async
    Future<Object> sendSeriesAsync(MobilePlatform platform, String userId, String messagePrototypeKey, String cronExpression, int seriesCode, int n) {
        for (int messageCode = 1; messageCode <= n; messageCode++) {
            sendPush("$seriesCode-$messageCode", platform, userId, messagePrototypeKey, cronExpression)
        }
        return new AsyncResult(null)
    }
}
