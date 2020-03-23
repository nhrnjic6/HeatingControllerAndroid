package com.nhrnjic.heatingcontroller.service;

import com.google.gson.Gson;
import com.nhrnjic.heatingcontroller.database.SetpointDatabaseService;
import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.Rules;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;

public class HeatingControlService {
    private SetpointDatabaseService setpointDatabaseService;
    private MqttService mqttService;

    public HeatingControlService(SetpointDatabaseService databaseService) {
        setpointDatabaseService = databaseService;
        mqttService = MqttService.getInstance(null);
    }

    public void sendCurrentRules(int rulesMode){
        List<DbSetpoint> setpoints = setpointDatabaseService.getAllSetpoints();
        Rules rules = new Rules();
        rules.setRules(setpoints);
        rules.setRulesSize(setpoints.size());
        rules.setRulesMode(rulesMode);

        Gson gson = new Gson();
        String jsonRules = gson.toJson(rules, Rules.class);

        System.out.println("Rules: " + jsonRules);
        try {
            mqttService.publishMessage(jsonRules);
        } catch (MqttException e) {
            System.out.println("Failed sending message to heatingControl/1");
        }
    }
}
