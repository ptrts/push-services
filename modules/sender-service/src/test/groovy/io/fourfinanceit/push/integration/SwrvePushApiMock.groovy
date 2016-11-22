package io.fourfinanceit.push.integration

import io.fourfinanceit.push.sender.service.components.swrve.SwrveException
import io.fourfinanceit.push.sender.service.components.swrve.SwrvePushApi
import io.fourfinanceit.push.sender.service.config.SwrveProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class SwrvePushApiMock extends SwrvePushApi {

    @Autowired
    private MessageTestDataRepository messageTestDataRepository

    @Autowired
    private SwrveProperties swrveProperties

    @Override
    void sendPush(String pushKey, String swrveUserId, String message) throws SwrveException {

        Thread.sleep(swrveProperties.averageProcessingMillis - 100)

        MessageTestData row = messageTestDataRepository.getMessageTestData(message)

        if (row.errorResponse != null || row.throwRuntimeException) {
            if (row.errorDone) {
                messageTestDataRepository.sent(message)
            } else {
                row.errorDone = true
                if (row.errorResponse != null) {
                    throw new SwrveException(row.errorResponse, new RuntimeException('Swrve service returned an error'))
                } else if (row.throwRuntimeException) {
                    throw new RuntimeException('Something bad happened')
                }
            }
        } else {
            messageTestDataRepository.sent(message)
        }
    }
}
