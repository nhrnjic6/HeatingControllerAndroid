package com.nhrnjic.heatingcontroller;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhrnjic.heatingcontroller.repository.SetpointRepository;
import com.nhrnjic.heatingcontroller.service.MqttService;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        MqttService mqttService = MqttService.getInstance(this);
        SetpointRepository repository = SetpointRepository.getInstance();

        try {
            mqttService.initConnection(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connected to MQTT");

                    try {
                        mqttService.pairWithDevice((status) -> {
                            repository.setCurrentRuleId(status.getId());
                            repository.setHeaterMode(status.getRulesMode());
                            repository.setTemperature(status.getTemperature());
                            repository.setSetpoints(status.getRules());

                            Intent intent = new Intent(LoadingActivity.this, SetpointListActivity.class);
                            startActivity(intent);
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

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
