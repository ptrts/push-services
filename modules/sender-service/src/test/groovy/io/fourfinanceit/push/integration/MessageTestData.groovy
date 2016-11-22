package io.fourfinanceit.push.integration

import groovy.transform.TupleConstructor
import io.fourfinanceit.push.swrve.api.SwrveResponseDto

@TupleConstructor(includes = 'id,errorResponse,throwRuntimeException,shouldBeSent')
public class MessageTestData {
    String id
    SwrveResponseDto errorResponse
    boolean throwRuntimeException
    boolean shouldBeSent
    boolean errorDone
    List<Date> actualSendDates = []
}
