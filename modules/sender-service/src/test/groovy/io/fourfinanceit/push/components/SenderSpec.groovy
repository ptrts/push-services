package io.fourfinanceit.push.components

import io.fourfinanceit.push.MobilePlatform
import io.fourfinanceit.push.sender.api.PushNotificationJmsDto
import io.fourfinanceit.push.sender.service.components.MessagePrototypePushKeyRepository
import io.fourfinanceit.push.sender.service.components.Sender
import io.fourfinanceit.push.sender.service.components.exception.MessagePrototypeKeyException
import io.fourfinanceit.push.sender.service.components.swrve.SwrvePushApi
import spock.lang.Specification

class SenderSpec extends Specification {

    final String USER_ID = '11111111-1111-1111-1111-111111111111'
    final String MESSAGE = 'Hello, my friend! Are you there?'
    final MobilePlatform PLATFORM = MobilePlatform.ANDROID

    final String KNOWN_MESSAGE_PROTOTYPE_KEY = 'Hello'
    final String PUSH_KEY = 'Hello push key'

    final String UNKNOWN_MESSAGE_PROTOTYPE_KEY = 'Unknown message prototype key'

    PushNotificationJmsDto dto

    Sender sender

    SwrvePushApi swrvePushApi

    def setup() {

        dto = new PushNotificationJmsDto()

        dto.platform = PLATFORM
        dto.userId = USER_ID
        dto.message = MESSAGE

        swrvePushApi = Mock(SwrvePushApi)

        MessagePrototypePushKeyRepository messagePrototypePushKeyRepository = Stub(MessagePrototypePushKeyRepository) {

            get(KNOWN_MESSAGE_PROTOTYPE_KEY, PLATFORM) >> PUSH_KEY

            get(_ as String, _ as MobilePlatform) >> null
        }

        sender = new Sender(swrvePushApi, messagePrototypePushKeyRepository)
    }

    def "Should send with the push key, associated with the message prototype if there's one"() {

        given:

        dto.messagePrototypeKey = KNOWN_MESSAGE_PROTOTYPE_KEY

        when:

        sender.send(dto)

        then:

        1 * swrvePushApi.sendPush(PUSH_KEY, dto.userId, dto.message)
    }

    def "Should throw SendingException, if there's no Swrve transactional campaign associated with the message prototype and the platform"() {

        given:

        dto.messagePrototypeKey = UNKNOWN_MESSAGE_PROTOTYPE_KEY

        when:

        sender.send(dto)

        then:

        thrown MessagePrototypeKeyException
    }

    def "Should throw SendingException, if message prototype key is null"() {

        given:

        dto.messagePrototypeKey = null

        when:

        sender.send(dto)

        then:

        thrown MessagePrototypeKeyException
    }
}
