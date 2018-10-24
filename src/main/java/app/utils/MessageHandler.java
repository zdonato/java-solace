package main.java.app.utils;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class MessageHandler {
    private SimpMessagingTemplate template;

    public MessageHandler (SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendToUI (String msg) throws Exception {
        System.out.println("Received message from Solace, sending to UI:\n" + msg);

        this.template.convertAndSend("/topic/messages", msg);
    }
}