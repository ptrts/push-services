package io.fourfinanceit.push.receiver.api

import groovy.transform.AutoClone
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.fourfinanceit.push.MobilePlatform

@TupleConstructor
@AutoClone
@ToString(includeNames = true)
@CompileStatic
class PushNotificationDto {
    MobilePlatform platform
    String messagePrototypeKey

    // This is the ID the addressat has in Swrve. 
    // In terms of Swrve it is called "user_id". 
    // Don't confuse it with what is called "push_key" in Swrve. 
    // Swrve "push_key" is the UUID of so called "transactional campaign".
    // For every push you send via Swrve you specify it's transactional campaign UUID in the POST request.  
    String pushKey

    String message
    String cronExpression
}
