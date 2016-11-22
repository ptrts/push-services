package io.fourfinanceit.push.test.load.swrve.mock

import groovy.transform.CompileStatic
import io.fourfinanceit.push.swrve.api.SwrveResponseDto
import io.fourfinanceit.push.test.load.swrve.mock.model.MessageData
import io.fourfinanceit.push.test.load.swrve.mock.model.MessageDataRepository
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

import java.nio.charset.StandardCharsets

import static org.slf4j.LoggerFactory.getLogger

@RestController
@RequestMapping('push')
@SuppressWarnings("GrMethodMayBeStatic")
@CompileStatic
class SwrveMockPushController {

    private static final Logger log = getLogger(SwrveMockPushController.class)

    @Autowired
    private RestTemplate restTemplate

    @Autowired
    private MessageDataRepository messageDataRepository

    @Value('${load-test.swrve.mock.url}')
    private URI url
    
    @Value('${load-test.swrve.mock.fake}')
    private boolean fake

    @RequestMapping(
            method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> post(@RequestBody String body, @RequestBody MultiValueMap<String, String> form) {
        
        log.trace('Request: {}', body)

        RequestBodyModel model = new RequestBodyModel()

        model.pushKey = getAndCheckNotBlank(form, 'push_key')
        model.userId = getAndCheckNotBlank(form, 'user')
        model.message = getAndCheckNotBlank(form, 'message')

        if (fake) {
            return doFake(model)
        } else {
            String message = form.getFirst('message')
            return delegate(body, message)
        }
    }

    private ResponseEntity<?> delegate(String body, String message) {

        URI pushApiUrl = UriComponentsBuilder
                .fromUri(url)
                .path('/push')
                .build()
                .toUri()

        RequestEntity<String> request = RequestEntity

                .post(pushApiUrl)

                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)

                .body(body)

        try {

            log.trace('Sending message [{}]', message)
            
            ResponseEntity<String> response = restTemplate.exchange(request, String.class)

            String responseBody = response.getBody()

            log.trace('Response received for message [{}]: {}', message, responseBody)

            return new ResponseEntity<>(responseBody, response.getHeaders(), response.getStatusCode())
            
        } catch (HttpStatusCodeException e) {

            log.error('Request [{}] received an error: {}', body, e.getMessage())
            
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getResponseHeaders(), e.getStatusCode())
        }
    }

    private String getAndCheckNotBlank(MultiValueMap<String, String> form, String name) {
        String value = form.getFirst(name)
        if (StringUtils.isBlank(value)) {
            log.error('there must be non-blank {}', name)
            return null
        } else {
            return value
        }
    }

    private ResponseEntity<SwrveResponseDto> doFake(RequestBodyModel model) {

        long startTimeMillis = System.currentTimeMillis()

        String text = model.message
        
        String[] parts = text.split(/-/)

        int threadCode = Integer.parseInt(parts[0])
        int messageCode = Integer.parseInt(parts[1])

        HttpStatus httpStatus = getHttpStatus(threadCode, messageCode)

        MessageData messageData = new MessageData()
        messageData.text = text
        messageData.threadCode = threadCode
        messageData.messageCode = messageCode
        messageData.httpStatusCode = httpStatus.value()
        messageData.time = new Date()

        messageDataRepository.save(messageData)

        long millisSpent = System.currentTimeMillis() - startTimeMillis
        
        long wholeProcessingMillis = 200 + (long) ((1000 - 200) * Math.random())

        log.trace('Waiting {} millis...', wholeProcessingMillis)
        
        try {
            long timeout = wholeProcessingMillis - millisSpent
            if (timeout > 0) {
                Thread.sleep(timeout)
            }
        } catch (InterruptedException ignored) {
        }

        def responseDto = new SwrveResponseDto(httpStatus.value(), httpStatus.reasonPhrase)
        
        return new ResponseEntity<>(responseDto, httpStatus)
    }

    private HttpStatus getHttpStatus(int threadCode, int messageCode) {

        int count = messageDataRepository.count(threadCode, messageCode)
        
        switch ((messageCode - 1) % 20 + 1) {
            case 5:
                // 1 retry (500)
                return count < 1 ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK
            case 10:
                // 2 retries (500)
                return count < 2 ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK
            case 15:
                // 2 retries and giving up (500)
                return count < 3 ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK
            case 20:
                // fail (403)
                return HttpStatus.FORBIDDEN
            default:
                // ok (200)
                return HttpStatus.OK
        }
    }
}
