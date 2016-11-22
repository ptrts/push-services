package io.fourfinanceit.push.components

import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy
import io.fourfinanceit.base.MicroserviceMvcWiremockSpec
import io.fourfinanceit.push.sender.service.components.swrve.SwrvePushApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration(classes = TestUrlProviderConfiguration)
class SwrvePushApiSpec extends MicroserviceMvcWiremockSpec {

    @Autowired
    SwrvePushApi swrvePushApi

    @Ignore
    def "Should send correct HTTP request to Swrve service"() {

        given:

        UrlMatchingStrategy urlMatchingStrategy = urlEqualTo('/push')

        stubInteraction(

                post(urlMatchingStrategy)
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)),

                aResponse()
                        .withStatus(HttpStatus.ACCEPTED.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody('{"code":202,"message":"OK"}')
        )

        when:

        String pushKey = 'push-key'
        String userId = '11111111-1111-1111-1111-111111111111'
        String message = 'Hello, my friend! Are you there?'

        swrvePushApi.sendPush(pushKey, userId, message)

        then:

        wireMock.verifyThat(postRequestedFor(urlMatchingStrategy))
    }
}
