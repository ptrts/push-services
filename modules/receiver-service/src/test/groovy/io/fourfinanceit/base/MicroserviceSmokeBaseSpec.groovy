package io.fourfinanceit.base

import com.jayway.restassured.RestAssured
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class MicroserviceSmokeBaseSpec extends Specification {

    def setup() {
        URL appUrl = new URL(System.getProperty('smokeTestAppUrl'))
        log.info('Application URL for smoke test is {}', appUrl)

        RestAssured.baseURI = "${appUrl.protocol}://${appUrl.host}"
        RestAssured.port = appUrl.port
    }

}
