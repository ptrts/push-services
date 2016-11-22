package io.fourfinanceit.push.util

import io.fourfinanceit.push.sender.service.util.SwrveRequestBodyBuilder
import spock.lang.Specification

class SwrveRequestBodyBuilderSpec extends Specification {

    def "Should build key=value sequence, delimited with '&' and with URL encoded values"() {

        when:

        SwrveRequestBodyBuilder builder = new SwrveRequestBodyBuilder()
                .add('one', 'one=one&one=one&')
                .add('two', 'two, two')
                .add('three', 'three')

        String result = builder.build()

        then:

        result == 'one=one%3Done%26one%3Done%26&two=two%2C+two&three=three'
    }
}
