package io.fourfinanceit.push.receiver.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.fourfinanceit.push.receiver.api.PushNotificationDto;
import io.fourfinanceit.push.receiver.api.PushServiceMediaTypes;
import io.fourfinanceit.push.receiver.service.components.Acceptor;

@RestController
@RequestMapping("/push")
public class PushController {

    private final Acceptor acceptor;

    @Autowired
    public PushController(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = PushServiceMediaTypes.JSON_V1)
    public void push(@RequestBody PushNotificationDto dto) {
        acceptor.accept(dto);
    }
}
