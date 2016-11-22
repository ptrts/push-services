package io.fourfinanceit.push.integration

import io.fourfinanceit.push.swrve.api.SwrveResponseDto
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap

@Service
class MessageTestDataRepository {

    private Map<String, MessageTestData> map = new ConcurrentHashMap<>(10000)

    private int notSentCounter = 0

    Closure onAllSent = null

    synchronized
    public void create(String id, SwrveResponseDto errorResponse, boolean throwRuntimeException, boolean willBeSent) {
        if (map.containsKey(id)) {
            throw new IllegalArgumentException('A message with id ' + id + ' has already been created')
        }
        map.put(id, new MessageTestData(id, errorResponse, throwRuntimeException, willBeSent))
        if (willBeSent) {
            notSentCounter++
        }
    }

    synchronized public void sent(String message) {
        MessageTestData row = map.get(message)
        if (row == null) {
            throw new RuntimeException('Trying to send a message that has not been created')
        }
        row.actualSendDates << new Date()

        if (row.actualSendDates.size() == 1) {
            if (row.shouldBeSent) {
                notSentCounter--
            }
            if (notSentCounter == 0) {
                onAllSent?.call()
            }
        }
    }

    synchronized public MessageTestData getMessageTestData(String message) {
        MessageTestData row = map.get(message)
        if (row == null) {
            throw new RuntimeException('The message has not been created')
        }
        return (MessageTestData) row
    }

    synchronized public Collection<MessageTestData> getAll() {
        return Collections.unmodifiableCollection(map.values())
    }
}
