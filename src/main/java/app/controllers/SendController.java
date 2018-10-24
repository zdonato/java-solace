package main.java.app.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import main.java.app.solace.TopicPublisher;
import main.java.app.messages.SimpleNameMessage;

@Controller
public class SendController {

    @MessageMapping("/send")
    public void send (SimpleNameMessage message) {
        String host = "tcp://mr85s7y9g5ch.messaging.solace.cloud:21216";
        String clientUsername = "solace-cloud-client";
        String msgVpn = "msgvpn-3i7dci3d6n";
        String password = "e58nmhog3r5q5i9heea8afopf1";
        String topic = "test/java";

        System.out.println("Sending message to solace:\n" + message.getName());

        TopicPublisher tp = new TopicPublisher(
                host,
                clientUsername,
                msgVpn,
                password,
                topic,
                message.getName()
        );

        Thread t = new Thread(tp);
        t.start();
    }
}