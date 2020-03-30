package com.nhrnjic.heatingcontroller.service;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.DeviceAction;
import com.nhrnjic.heatingcontroller.model.SystemStatusListener;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;

public class HeatingControlService {
    private MqttService mqttService;

    public HeatingControlService() {
        mqttService = MqttService.getInstance(null);
    }

    public void updateConfig(
            int rulesMode,
            List<DbSetpoint> setpoints,
            SystemStatusListener listener){
        DeviceAction action = new DeviceAction("set_setpoints");
        action.setRulesMode(rulesMode);
        action.setRules(setpoints);

        try {
            mqttService.sendAction(action, listener);
        } catch (MqttException e) {
            System.out.println("Failed sending action to device");
        }
    }
}
