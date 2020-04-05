package com.nhrnjic.heatingcontroller.service;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.exception.FieldNotSetException;
import com.nhrnjic.heatingcontroller.exception.MaxSetpointSizeException;
import com.nhrnjic.heatingcontroller.model.DeviceAction;
import com.nhrnjic.heatingcontroller.model.SystemStatusListener;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;

import org.eclipse.paho.client.mqttv3.MqttException;

public class HeatingControlService {
    private static final int MAX_SIZE_PER_DAY = 2;

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
            SystemStatusListener listener)
            throws FieldNotSetException, MaxSetpointSizeException {
        validateSetpoint(setpoint, repeatWorkDay, repeatWeekend);

        DeviceAction action = new DeviceAction("set_setpoints");
        action.setRulesMode(setpointRepository.getHeaterMode());
        action.setRules(setpointRepository.getSetpoints());

        try {
            mqttService.sendAction(action, listener);
        } catch (MqttException e) {
            System.out.println("Failed sending action to device");
        }
    }

    private void validateSetpoint(
            DbSetpoint setpoint,
            boolean repeatWorkDay,
            boolean repeatWeekend) throws FieldNotSetException, MaxSetpointSizeException {
        validateFields(setpoint ,repeatWorkDay || repeatWeekend);

        if(repeatWorkDay){
            for(int dayOfWeek = 1; dayOfWeek < 6; dayOfWeek++){
                int setpointsInDay = setpointRepository.getSetpoints(dayOfWeek).size();
                if(setpointsInDay >= MAX_SIZE_PER_DAY){
                    throw new MaxSetpointSizeException("Maximum of 5 setpoints can be added to any given day", true);
                }
            }

            for(int dayOfWeek = 1; dayOfWeek < 6; dayOfWeek++){
                DbSetpoint clonedSetpoint = new DbSetpoint(setpoint);
                clonedSetpoint.setDay(dayOfWeek);
                setpointRepository.addSetpoint(clonedSetpoint);
            }
        }

        if(repeatWeekend){
            for(int dayOfWeek = 6; dayOfWeek < 8; dayOfWeek++){
                int setpointsInDay = setpointRepository.getSetpoints(dayOfWeek).size();
                if(setpointsInDay >= MAX_SIZE_PER_DAY){
                    throw new MaxSetpointSizeException("Maximum of 5 setpoints can be added to any given day", true);
                }
            }

            for(int dayOfWeek = 6; dayOfWeek < 8; dayOfWeek++){
                DbSetpoint clonedSetpoint = new DbSetpoint(setpoint);
                clonedSetpoint.setDay(dayOfWeek);
                setpointRepository.addSetpoint(clonedSetpoint);
            }
        }

        if(!repeatWorkDay && !repeatWeekend){
            int setpointsInDay = setpointRepository.getSetpoints(setpoint.getDay()).size();
            if(setpointsInDay >= MAX_SIZE_PER_DAY){
                throw new MaxSetpointSizeException("Maximum of 5 setpoints can be added to any given day", false);
            }

            setpointRepository.addSetpoint(setpoint);
        }
    }

    private void validateFields(
            DbSetpoint setpoint,
            boolean isRepeat) throws FieldNotSetException {
        if(setpoint.getDay() == null && !isRepeat) throw new FieldNotSetException("Field day is required", "Day");
        if(setpoint.getHour() == null) throw new FieldNotSetException("Field hour is required", "Hour");
        if(setpoint.getMinute() == null) throw new FieldNotSetException("Field minute is required", "Minute");
        if(setpoint.getTemperature() == null) throw new FieldNotSetException("Field temperature is required", "Temperature");
    }
}
