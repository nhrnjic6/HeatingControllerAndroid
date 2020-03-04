package com.nhrnjic.heatingcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.nhrnjic.heatingcontroller.database.SetpointDatabaseService;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.Setpoint;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private SetpointDatabaseService setpointDatabaseService = new SetpointDatabaseService();

    private ListView setpointListView;
    private Button mAddSetpointButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setpointListView = findViewById(R.id.setpoint_list_view);
        mAddSetpointButton = findViewById(R.id.btn_setpoint_create);

        mAddSetpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SetpointCreateActivity.class);
                startActivity(intent);
            }
        });

        List<DbSetpoint> setpoints = setpointDatabaseService.getAllSetpoints();

        setpointListView.setAdapter(new SetpointListAdapter(setpoints, this));

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(this, "tcp://test.mosquitto.org:1883", clientId);
        try {
            client.connect().setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connected to mqtt");
                    MqttMessage message = new MqttMessage();
                    message.setPayload("hi from android".getBytes());
                    try {
                        client.publish("sensors/heatingControl/1", message);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    try {
                        client.subscribe("sensors/heatingControl/1", 1, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                System.out.println("recevide payload: " + new String(message.getPayload()));
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Failed to connected to mqtt");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
