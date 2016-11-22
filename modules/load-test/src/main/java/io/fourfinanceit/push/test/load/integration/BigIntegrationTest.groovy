package io.fourfinanceit.push.test.load.integration

import io.fourfinanceit.push.MobilePlatform
import io.fourfinanceit.push.receiver.api.PushNotificationDto
import io.fourfinanceit.push.swrve.api.SwrveResponseDto
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import java.time.temporal.ChronoUnit
import java.util.concurrent.CountDownLatch

import static org.slf4j.LoggerFactory.getLogger

class BigIntegrationTest {

    static final Logger log = getLogger(BigIntegrationTest.class);

    static final int DO_ERROR_EVERY = 5

    @Autowired
    MessageTestDataRepository testDataRepository

    PushNotificationDto dtoPrototype

    def setup() {
        dtoPrototype = new PushNotificationDto(MobilePlatform.ANDROID, 'Hello', 'user-id')
    }

    def cleanup() {
    }

    def "Immediate pushes should work"() {

        given:

        int messagesPerSeries = 2000
        int seriesNumber = 4
        int pauseMillis = 10000

        when:

        for (int i = 0; i < seriesNumber; i++) {

            int number = i + 1

            log.debug('============================================================================')
            log.debug("Series #$number...")
            log.debug('============================================================================')

            series("series-$number", messagesPerSeries, 1, ChronoUnit.MILLIS)

            log.debug('============================================================================')
            log.debug("Waiting after series #$number...")
            log.debug('============================================================================')

            if (i == seriesNumber - 1) {
                CountDownLatch latch = new CountDownLatch(1)
                testDataRepository.onAllSent = {
                    latch.countDown()
                }
                latch.await()
            } else {
                pause(pauseMillis, ChronoUnit.MILLIS)
            }
        }

        then:

        validate()
    }

    def "Scheduled pushes should work"() {

        given:

        // Every minute
        String cronExpression = '0 0/1 * 1/1 * ? *'
        int messagesPerSeries = 2000

        when:

        series("the-series", messagesPerSeries, 1, ChronoUnit.MILLIS, cronExpression)

        log.debug('============================================================================')
        log.debug("Waiting after the series...")
        log.debug('============================================================================')

        CountDownLatch latch = new CountDownLatch(1)
        testDataRepository.onAllSent = {
            latch.countDown()
        }
        latch.await()

        then:

        validate()
    }

    private void validate() {

        StringBuilder sb = new StringBuilder()

        testDataRepository.getAll().each {

            if (it.shouldBeSent) {
                if (it.actualSendDates.empty) {
                    sb << "\nMessage ${it.id}: is not sent"
                } else if (it.actualSendDates.size() > 1) {
                    sb << "\nMessage ${it.id}: is sent more than once (${it.actualSendDates.size()})"
                }
            } else {
                if (!it.actualSendDates.empty) {
                    sb << "\nMessage ${it.id}: is sent, while it shouldn't be"
                }
            }
        }

        Assert.that(sb.length() == 0, sb.toString())
    }

    private void series(Object seriesId, int number, int delayAmount, ChronoUnit delayUnit) {
        series(seriesId, number, delayAmount, delayUnit, null)
    }

    private void series(Object seriesId, int number, int delayAmount, ChronoUnit delayUnit, String cronExpression) {
        for (int i = 0; i < number; i++) {
            if (i > 0 && delayAmount > 0) {
                pause(delayAmount, delayUnit)
            }
            String messageId = '' + seriesId + '-' + i

            String messagePrototypeKey = 'Hello'
            SwrveResponseDto errorResponse = null
            boolean throwRuntimeException = false
            boolean shouldBeSent = true

            // Determine which kind of error to provoke

            //noinspection GroovyEmptyStatementBody,GroovyConstantIfStatement
            if (false) {
            } else if ((i + 5 * DO_ERROR_EVERY) % (5 * DO_ERROR_EVERY) == 0) {
                // 0, 25, 50
                throwRuntimeException = true
                shouldBeSent = false
            } else if ((i + 4 * DO_ERROR_EVERY) % (5 * DO_ERROR_EVERY) == 0) {
                // 5, 30, 55
                messagePrototypeKey = null
                shouldBeSent = false
            } else if ((i + 3 * DO_ERROR_EVERY) % (5 * DO_ERROR_EVERY) == 0) {
                // 10, 35, 60
                messagePrototypeKey = 'Wrong message prototype key'
                shouldBeSent = false
            } else if ((i + 2 * DO_ERROR_EVERY) % (5 * DO_ERROR_EVERY) == 0) {
                // 15, 40, 65
                // An error the service won't retry after
                errorResponse = new SwrveResponseDto(400, 'Rejected, User Has Changed Token')
                shouldBeSent = false
            } else if ((i + 1 * DO_ERROR_EVERY) % (5 * DO_ERROR_EVERY) == 0) {
                // 20, 45, 70
                // An error the service will retry after
                errorResponse = new SwrveResponseDto(500, 'Internal Server Error')
                shouldBeSent = true
            }

            PushNotificationDto dto = (PushNotificationDto) dtoPrototype.clone()

            dto.messagePrototypeKey = messagePrototypeKey

            if (cronExpression != null) {
                dto.cronExpression = cronExpression
            }

            testDataRepository.create(messageId, errorResponse, throwRuntimeException, shouldBeSent)

            dto.message = messageId

            //pushController.push(dto)
        }
    }

    private static pause(int delayAmount, ChronoUnit delayUnit) {
        Thread.sleep(delayAmount * delayUnit.duration.toMillis())
    }
}
