package main.java.app.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import main.java.app.solace.TopicSubscriber;
import main.java.app.utils.MessageHandler;

@Controller
public class ConnectController {

    private SimpMessagingTemplate template;

    @Autowired
    public ConnectController (SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/connect")
    public void setup() {
        String host = "tcp://mr85s7y9g5ch.messaging.solace.cloud:21216";
        String clientUsername = "solace-cloud-client";
        String msgVpn = "msgvpn-3i7dci3d6n";
        String password = "e58nmhog3r5q5i9heea8afopf1";
        String topic = "test/java";
        MessageHandler handler = new MessageHandler(this.template);

        // Start the TopicSubscriber.
        TopicSubscriber ts = new TopicSubscriber(
                host,
                clientUsername,
                msgVpn,
                password,
                topic,
                handler
        );

        Thread t = new Thread(ts);
        t.start();
    }
}