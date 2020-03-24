package com.nhrnjic.heatingcontroller.service;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class MqttService {
    public static final String MQTT_BROKER_URL = "tcp://192.168.1.3:1883";
    public static final String SYSTEM_STATUS_TOPIC = "sensors/heatingControl/1/status";
    public static final String SYSTEM_CONTROL_TOPIC = "sensors/heatingControl/1";

    private static MqttService mqttService;

    private final MqttAndroidClient client;
    private List<IMqttMessageListener> messageListeners;

    private MqttService(Context context) {
        messageListeners = new ArrayList<>();
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, MQTT_BROKER_URL, clientId);
    }

    public static synchronized MqttService getInstance(Context context) {
        if(mqttService == null){
            mqttService = new MqttService(context);
        }

        return mqttService;
    }

    public void initConnection() throws MqttException {
        client.connect().setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                try {
                    System.out.println("Connected to MQTT");
                    client.subscribe(SYSTEM_STATUS_TOPIC, 1, new IMqttMessageListener() {
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            for (IMqttMessageListener listener : messageListeners) {
                                listener.messageArrived(topic, message);
                            }
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                System.out.println("Failed connecting to MQTT");
            }
        });
    }

    public void publishMessage(String jsonMsg) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(jsonMsg.getBytes());
        client.publish(SYSTEM_CONTROL_TOPIC, mqttMessage);
    }

    public void disconnect() throws MqttException {
        client.disconnect().setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                System.out.println("Disconnected from MQTT");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                System.out.println("Failed disconnecting from MQTT");
            }
        });
    }

    public void addListener(IMqttMessageListener messageListener){
        messageListeners.add(messageListener);
    }
}
