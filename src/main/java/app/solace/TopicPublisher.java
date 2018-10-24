/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package main.java.app.solace;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

public class TopicPublisher implements Runnable {
    private String host;
    private String clientUsername;
    private String msgVpn;
    private String password;
    private String topicName;

    private String message;

    public TopicPublisher () {}

    public TopicPublisher (String host, String clientUsername, String msgVpn, String password, String topic) {
        this.host = host;
        this.clientUsername = clientUsername;
        this.msgVpn = msgVpn;
        this.password = password;
        this.topicName = topic;
    }

    public TopicPublisher (String host, String clientUsername, String msgVpn, String password, String topic, String message) {
        this.host = host;
        this.clientUsername = clientUsername;
        this.msgVpn = msgVpn;
        this.password = password;
        this.topicName = topic;

        this.message = message;
    }

    @Override
    public void run () {
        try {
            send();
        } catch (JCSMPException e) {
            System.out.println(e);
        }
    }

    public void send() throws JCSMPException {
        // Create a JCSMP Session
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, host);     // host:port
        properties.setProperty(JCSMPProperties.USERNAME, clientUsername); // client-username
        properties.setProperty(JCSMPProperties.PASSWORD, password); // client-password
        properties.setProperty(JCSMPProperties.VPN_NAME,  msgVpn); // message-vpn
        final JCSMPSession session =  JCSMPFactory.onlyInstance().createSession(properties);

        session.connect();

        final Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);

        /** Anonymous inner-class for handling publishing events */
        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            @Override
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            @Override
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });

        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

        msg.setText(message);
        prod.send(msg,topic);
        session.closeSession();
    }
}