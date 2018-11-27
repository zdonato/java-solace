package main.java.app.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import com.solacesystems.jcsmp.JCSMPException;

import main.java.app.solace.TopicPublisher;
import main.java.app.messages.SimpleNameMessage;

@Controller
public class SendController {
    private final TopicPublisher tp;

    @Autowired
    public SendController() {
        String host = "tcp://mr85s7y9g5ch.messaging.solace.cloud:21216";
        String clientUsername = "solace-cloud-client";
        String msgVpn = "msgvpn-3i7dci3d6n";
        String password = "e58nmhog3r5q5i9heea8afopf1";
        String topic = "test/zach";

        tp = new TopicPublisher(
                host,
                clientUsername,
                msgVpn,
                password,
                topic
        );
    }

    @MessageMapping("/send")
    public void send (SimpleNameMessage message) {

        try {
            tp.send(message.getName());
        } catch (JCSMPException e) {
            e.printStackTrace();
        }

    }
}