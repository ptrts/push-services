package io.fourfinanceit.push.swrve.api

import groovy.transform.CompileStatic

@CompileStatic
class SwrveResponseDto {
    int code;
    String message;

    @SuppressWarnings("GroovyUnusedDeclaration")
    SwrveResponseDto() {
    }

    SwrveResponseDto(int code, String message) {
        this.code = code
        this.message = message
    }
}
