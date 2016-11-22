package io.fourfinanceit.base

import com.ofg.infrastructure.base.IntegrationSpec
import io.fourfinanceit.push.sender.service.Application
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [Application], loader = SpringApplicationContextLoader)
class MicroserviceIntegrationSpec extends IntegrationSpec {
}
