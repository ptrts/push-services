package io.fourfinanceit.push

import com.jayway.restassured.RestAssured
import com.jayway.restassured.response.Response
import groovy.util.logging.Slf4j
import io.fourfinanceit.base.MicroserviceSmokeBaseSpec

@Slf4j
class DummySpec extends MicroserviceSmokeBaseSpec {

    def "should be alive"() {
        when:
        Response response = RestAssured.given().get('/ping')

        then:
        response.statusCode == 200
        response.body.asString() == 'OK'
    }

}
