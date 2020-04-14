package com.nhrnjic.heatingcontroller.service;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.nhrnjic.heatingcontroller.exception.ActionNotCompleteException;
import com.nhrnjic.heatingcontroller.model.DeviceAction;
import com.nhrnjic.heatingcontroller.model.ErrorListener;
import com.nhrnjic.heatingcontroller.model.SystemStatus;
import com.nhrnjic.heatingcontroller.model.SystemStatusListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MqttService {
    public static final String MQTT_BROKER_URL = "tcp://192.168.1.3:1883";
    public static final String STATUS_TOPIC = "sensors/heatingControl/status";
    public static final String ACTION_TOPIC = "sensors/heatingControl/action";

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

    public void initConnection(IMqttActionListener actionListener) throws MqttException {
        client.connect().setActionCallback(actionListener);
    }

    public void getSystemStatus(SystemStatusListener listener) throws MqttException {
        Gson gson = new Gson();
        client.subscribe(STATUS_TOPIC, 1, (topic, message) -> {
            SystemStatus systemStatus = gson.fromJson(new String(message.getPayload()), SystemStatus.class);
                listener.systemStatusReceived(systemStatus);
        });
    }

    public void pairWithDevice(SystemStatusListener listener, ErrorListener errorListener) throws MqttException{
        if(client.isConnected()){
            DeviceAction action = new DeviceAction("get_setpoints");
            sendAction(action, listener, errorListener);
        }
    }

    public void sendAction(DeviceAction action, SystemStatusListener listener, ErrorListener errorListener) throws MqttException {
        Gson gson = new Gson();
        AtomicBoolean isActionSuccess = new AtomicBoolean(false);

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            if(!isActionSuccess.get()){
                errorListener.onError();
            }
        }, 10000);

        client.subscribe(STATUS_TOPIC, 1, (topic, message) -> {
            SystemStatus systemStatus = gson.fromJson(new String(message.getPayload()), SystemStatus.class);
            if(action.getRequestId().equals(systemStatus.getRequestId())){
                isActionSuccess.set(true);
                listener.systemStatusReceived(systemStatus);
                client.unsubscribe(STATUS_TOPIC);
            }
        });

        String jsonMsg = gson.toJson(action, DeviceAction.class);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(jsonMsg.getBytes());
        client.publish(ACTION_TOPIC, mqttMessage);
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
