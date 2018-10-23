package hello;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import solace.TopicSubscriber;

@Controller
public class GreetingController {

    private SimpMessagingTemplate template;

    @Autowired
    public GreetingController (SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting (HelloMessage message) throws Exception {
        System.out.println("Message received with name: " + message.getName());
        Thread.sleep(1000);
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()));
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