package com.nhrnjic.heatingcontroller.service;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.DeviceAction;
import com.nhrnjic.heatingcontroller.model.SystemStatusListener;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;

import org.eclipse.paho.client.mqttv3.MqttException;

public class HeatingControlService {
    private MqttService mqttService;
    private SetpointRepository setpointRepository;

    public HeatingControlService() {
        mqttService = MqttService.getInstance(null);
        setpointRepository = SetpointRepository.getInstance();
    }

    public void changeRulesMode(
            int rulesMode,
            SystemStatusListener listener){
        DeviceAction action = new DeviceAction("set_setpoints");
        action.setRulesMode(rulesMode);
        action.setRules(setpointRepository.getSetpoints());

        try {
            mqttService.sendAction(action, listener);
        } catch (MqttException e) {
            System.out.println("Failed sending action to device");
        }
    }

    public void saveNewSetpoint(
            DbSetpoint setpoint,
            boolean repeatWorkDay,
            boolean repeatWeekend,
            SystemStatusListener listener){

        // TODO: validation before adding setpoint
        setpointRepository.addSetpoint(setpoint);

        DeviceAction action = new DeviceAction("set_setpoints");
        action.setRulesMode(setpointRepository.getHeaterMode());
        action.setRules(setpointRepository.getSetpoints());

        try {
            mqttService.sendAction(action, listener);
        } catch (MqttException e) {
            System.out.println("Failed sending action to device");
        }
    }
}
