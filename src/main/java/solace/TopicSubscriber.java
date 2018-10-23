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

package solace;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageConsumer;
import com.solacesystems.jcsmp.XMLMessageListener;

import hello.MessageHandler;

public class TopicSubscriber implements Runnable {
    private String host;
    private String clientUsername;
    private String msgVpn;
    private String password;
    private String topicName;
    private MessageHandler handler;

    private JCSMPSession session;
    private XMLMessageConsumer cons;

    public TopicSubscriber () {}

    public TopicSubscriber (String host, String clientUsername, String msgVpn, String password, String topic, MessageHandler handler) {
        this.host = host;
        this.clientUsername = clientUsername;
        this.msgVpn = msgVpn;
        this.password = password;
        this.topicName = topic;
        this.handler = handler;
    }

    @Override
    public void run () {
        try {
            subscribe();
        } catch (JCSMPException e) {
            System.out.println(e);
        }

    }

    public void subscribe () throws JCSMPException {

        System.out.println("TopicSubscriber initializing...");
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, host);     // host:port
        properties.setProperty(JCSMPProperties.USERNAME, clientUsername); // client-username
        properties.setProperty(JCSMPProperties.PASSWORD, password); // client-password
        properties.setProperty(JCSMPProperties.VPN_NAME, msgVpn); // message-vpn
        final Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);

        session = JCSMPFactory.onlyInstance().createSession(properties);

        session.connect();
        /** Anonymous inner-class for MessageListener
         *  This demonstrates the async threaded message callback */
        cons = session.getMessageConsumer(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                if (msg instanceof TextMessage) {
//                    System.out.printf("TextMessage received: '%s'%n",
//                            ((TextMessage) msg).getText());
                    try {
                        handler.sendToUI(((TextMessage) msg).getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    System.out.println("Message received.");
//                    System.out.printf("Message Dump:%n%s%n", msg.dump());
                    try {
                        handler.sendToUI(msg.dump());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onException(JCSMPException e) {
                System.out.printf("Consumer received exception: %s%n", e);
            }
        });
        session.addSubscription(topic);
        System.out.println("Connected. Awaiting message...");
        cons.start();
    }

    public void close () {
        if (cons != null && session != null) {
            // Close consumer
            cons.close();
            System.out.println("Exiting.");
            session.closeSession();
        }
    }
}
