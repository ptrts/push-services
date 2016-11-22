package io.fourfinanceit.push.receiver.service.metric

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

    Meter immediatePushReceivedMeter
    Meter scheduledPushReceivedMeter
    Timer receivingTimer
    Meter notStoredExceptionWhileReceivingMeter
    Meter pushReceivedMeter
    Meter pushSentMeter

    @Autowired
    MetricReportingService(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry
    }

    @PostConstruct
    void init() {
        scheduledPushReceivedMeter = createMeter('scheduledPushReceived')
        immediatePushReceivedMeter = createMeter('immediatePushReceived')
        receivingTimer = createTimer('receivingTime')
        notStoredExceptionWhileReceivingMeter = createMeter('notStoredExceptionWhileReceiving')
        pushReceivedMeter = createMeter('pushReceived')
        pushSentMeter = createMeter('pushSent')
    }

    void scheduledPushReceived() {
        scheduledPushReceivedMeter.mark()
    }

    void immediatePushReceived() {
        immediatePushReceivedMeter.mark()
    }

    Timer.Context getReceivingTimerContext() {
        receivingTimer.time()
    }

    void notStoredExceptionWhileReceiving() {
        notStoredExceptionWhileReceivingMeter.mark()
    }

    void pushReceived() {
        pushReceivedMeter.mark()
    }

    void pushSent() {
        pushSentMeter.mark()
    }

    private Timer createTimer(String metric) {
        metricRegistry.timer(createMetricName(metric))
    }

    private Meter createMeter(String metric) {
        metricRegistry.meter(createMetricName(metric))
    }

    private static String createMetricName(String metric) {
        "$METRIC_BASE_NAME.$metric"
    }
}
