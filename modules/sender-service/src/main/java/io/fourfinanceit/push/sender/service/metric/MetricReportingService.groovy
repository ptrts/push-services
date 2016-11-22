package io.fourfinanceit.push.sender.service.metric

import com.codahale.metrics.Counter
import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

@Service
@CompileStatic
class MetricReportingService {

    static final String METRIC_BASE_NAME = 'pushService'

    final MetricRegistry metricRegistry

    Meter sendingSuccessMeter
    Meter sendingFailMeter
    Timer sendingTimer
    Meter sendingAttemptFailedMeter
    Meter otherSendingErrorMeter
    Meter messagePrototypeKeySendingErrorMeter
    Meter swrveSendingErrorMeter
    Meter sendingGivenUpMeter
    Counter threadPoolSizeCounter
    Counter sendingThreadsCounter
    Counter waitingThreadsCounter

    @Autowired
    MetricReportingService(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry
    }

    @PostConstruct
    void init() {
        sendingSuccessMeter = createMeter('sendingSuccess')
        sendingFailMeter = createMeter('sendingFail')
        sendingTimer = createTimer('sendingTime')
        sendingAttemptFailedMeter = createMeter('sendingAttemptFailed')
        otherSendingErrorMeter = createMeter('otherSendingError')
        messagePrototypeKeySendingErrorMeter = createMeter('messagePrototypeKeySendingError')
        swrveSendingErrorMeter = createMeter('swrveSendingError')
        sendingGivenUpMeter = createMeter('sendingGivenUp')
        threadPoolSizeCounter = createCounter('threadPoolSize')
        sendingThreadsCounter = createCounter('sendingThreads')
        waitingThreadsCounter = createCounter('waitingThreads')
    }

    void sendingSuccess() {
        sendingSuccessMeter.mark()
    }

    void sendingFail() {
        sendingFailMeter.mark()
    }

    Timer.Context getSendingTimerContext() {
        sendingTimer.time()
    }

    void sendingGivenUp() {
        sendingGivenUpMeter.mark()
    }

    void swrveSendingError() {
        swrveSendingErrorMeter.mark()
    }

    void messagePrototypeKeySendingError() {
        messagePrototypeKeySendingErrorMeter.mark()
    }

    void otherSendingError() {
        otherSendingErrorMeter.mark()
    }

    void sendingAttemptFailed() {
        sendingAttemptFailedMeter.mark()
    }

    void threadPoolSizeInc() {
        threadPoolSizeCounter.inc()
    }

    void threadPoolSizeDec() {
        threadPoolSizeCounter.dec()
    }
    
    void sendingThreadsInc() {
        sendingThreadsCounter.inc()
    }

    void sendingThreadsDec() {
        sendingThreadsCounter.dec()
    }

    void waitingThreadsInc() {
        waitingThreadsCounter.inc()
    }

    void waitingThreadsDec() {
        waitingThreadsCounter.dec()
    }

    private Timer createTimer(String metric) {
        metricRegistry.timer(createMetricName(metric))
    }

    private Meter createMeter(String metric) {
        metricRegistry.meter(createMetricName(metric))
    }

    private Counter createCounter(String metric) {
        metricRegistry.counter(createMetricName(metric))
    }

    private static String createMetricName(String metric) {
        "$METRIC_BASE_NAME.$metric"
    }
}
