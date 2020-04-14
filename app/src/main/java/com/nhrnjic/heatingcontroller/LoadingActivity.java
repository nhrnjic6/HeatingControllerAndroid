package com.nhrnjic.heatingcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhrnjic.heatingcontroller.exception.ActionNotCompleteException;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;
import com.nhrnjic.heatingcontroller.service.MqttService;
import com.pnikosis.materialishprogress.ProgressWheel;

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

        ProgressWheel wheel = new ProgressWheel(this);
        wheel.setBarColor(Color.BLUE);

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
                            repository.setUpdatedAt(status.getUpdatedAt());
                            repository.setTempList(status.getTempList());

                            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                            startActivity(intent);
                        }, () -> failedConnectionActivity());
                    } catch (MqttException e) {
                        failedConnectionActivity();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    failedConnectionActivity();
                }
            });

        } catch (MqttException e) {
            failedConnectionActivity();
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void failedConnectionActivity(){
        Intent intent = new Intent(LoadingActivity.this, ReloadActivity.class);
        startActivity(intent);
        finish();
    }
}
