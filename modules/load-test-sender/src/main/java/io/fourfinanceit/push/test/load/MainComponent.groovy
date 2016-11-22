package io.fourfinanceit.push.test.load

import io.fourfinanceit.push.MobilePlatform
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component

import java.util.concurrent.Future

@Component
class MainComponent implements SmartLifecycle {

    @Autowired
    private SeriesSender bigLoadTest

    @Autowired
    private LoadTestSenderProperties properties

    @Override
    boolean isAutoStartup() {
        return true
    }

    @Override
    void stop(Runnable callback) {
        stop()
        callback.run()
    }

    @Override
    void start() {

        List<Future<Object>> futures = new ArrayList<>(properties.threads)
        
        for (int messageCode = 1; messageCode <= properties.threads; messageCode++) {
            Future<Object> future = bigLoadTest.sendSeriesAsync(MobilePlatform.IOS, properties.userId, 'Hello', null, messageCode, properties.messagesPerThread)
            futures.add(future)
        }
        
        for (Future<Object> future : futures) {
            future.get()
        }
    }

    @Override
    void stop() {
    }

    @Override
    boolean isRunning() {
        return false
    }

    @Override
    int getPhase() {
        return 0
    }
}
