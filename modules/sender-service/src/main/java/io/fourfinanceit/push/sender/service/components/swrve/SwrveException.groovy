package io.fourfinanceit.push.sender.service.components.swrve

import groovy.transform.CompileStatic
import io.fourfinanceit.push.swrve.api.SwrveResponseDto

@CompileStatic
public class SwrveException extends Exception {

    final SwrveResponseDto swrveResponse;

    SwrveException(SwrveResponseDto swrveResponse, Throwable e) {
        super(swrveResponse == null ? null : "Swrve request error", e)
        this.swrveResponse = swrveResponse
    }
}
