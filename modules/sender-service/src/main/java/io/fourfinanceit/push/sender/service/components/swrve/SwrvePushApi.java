package io.fourfinanceit.push.sender.service.components.swrve;

import static org.slf4j.LoggerFactory.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofg.infrastructure.web.resttemplate.custom.ResponseException;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import io.fourfinanceit.push.sender.service.config.SwrveProperties;
import io.fourfinanceit.push.sender.service.config.UrlProvider;
import io.fourfinanceit.push.sender.service.util.SwrveRequestBodyBuilder;
import io.fourfinanceit.push.swrve.api.SwrveResponseDto;

@Service
public class SwrvePushApi {

    private static final Logger log = getLogger(SwrvePushApi.class);

    @Autowired
    private SwrveProperties swrveProperties;

    @Autowired
    private UrlProvider swrveUrlProvider;

    @Autowired
    private ServiceRestClient serviceRestClient;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendPush(String pushKey, String swrveUserId, String message) throws SwrveException {

        String arguments = String.format("pushKey=%s, swrveUserId=%s, message=%s", pushKey, swrveUserId, message);

        try {
            log.debug("sendPush, start: {}", arguments);

            String url = swrveUrlProvider.getUrl();
            String body = buildBody(pushKey, swrveUserId, message);
            serviceRestClient
                    .forExternalService()
                    .post()
                    .onUrl(url)
                    .body(body)
                    .withHeaders()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .andExecuteFor()
                    .ignoringResponse();
        } catch (ResponseException e) {
            throw new SwrveException(extractResponse(e), e);
        } finally {
            log.debug("sendPush, end: {}", arguments);
        }
    }

    private SwrveResponseDto extractResponse(ResponseException responseException) {

        String body = responseException.getBody();
        HttpStatus httpStatus = responseException.getHttpStatus();

        if (httpStatus.is5xxServerError()) {
            return new SwrveResponseDto(httpStatus.value(), httpStatus.getReasonPhrase());
        } else if (StringUtils.isBlank(body)) {
            return new SwrveResponseDto(httpStatus.value(), "<no response body to extract message from>");
        } else {
            try {
                return objectMapper.readValue(body, SwrveResponseDto.class);
            } catch (Exception e) {
                String message = "Error reading response from Swrve: " + body;
                log.error(message, e);
                return new SwrveResponseDto(httpStatus.value(), "<" + message + ">");
            }
        }
    }

    private String buildBody(String pushKey, String swrveUserId, String message) {
        SwrveRequestBodyBuilder builder = new SwrveRequestBodyBuilder()
                .add("push_key", pushKey)
                .add("user", swrveUserId)
                .add("message", message);
        if (swrveProperties.isTest()) {
            builder.add("test", "1");
        }
        return builder.build();
    }
}
