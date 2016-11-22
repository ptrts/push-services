package io.fourfinanceit.push.config

import io.fourfinanceit.push.sender.service.config.UrlProvider
import spock.lang.Specification

class UrlProviderSpec extends Specification {

    def "Should correctly build URL from service URL and path"(String serviceUrl, String path, String expectedUrl) {

        expect:

        new UrlProvider(serviceUrl, path).getUrl() == expectedUrl

        where:

        serviceUrl                     | path    | expectedUrl
        'http://localhost:8080'        | '/push' | 'http://localhost:8080/push'
        'http://localhost'             | '/push' | 'http://localhost/push'
        'https://eu-service.swrve.com' | '/push' | 'https://eu-service.swrve.com/push'
    }
}
