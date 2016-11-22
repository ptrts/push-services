package io.fourfinanceit.base

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc
import io.fourfinanceit.push.receiver.service.controller.PushController
import spock.lang.Specification

class MicroserviceAccurestBaseSpec extends Specification {

    def setup() {
        RestAssuredMockMvc.standaloneSetup(
                Mock(PushController)
        )
    }
}
