package com.nhrnjic.heatingcontroller.service;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.exception.FieldNotSetException;
import com.nhrnjic.heatingcontroller.exception.MaxSetpointSizeException;
import com.nhrnjic.heatingcontroller.model.DeviceAction;
import com.nhrnjic.heatingcontroller.model.SystemStatusListener;
import com.nhrnjic.heatingcontroller.repository.SetpointRepository;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeatingControlService {
    private static final int MAX_SIZE_PER_DAY = 5;
    public static final String SET_SETPOINTS_ACTION = "set_setpoints";

    private MqttService mqttService;
    private SetpointRepository setpointRepository;

    public HeatingControlService() {
        mqttService = MqttService.getInstance(null);
        setpointRepository = SetpointRepository.getInstance();
    }

    public void changeRulesMode(
            int rulesMode,
            SystemStatusListener listener){
        DeviceAction action = new DeviceAction(SET_SETPOINTS_ACTION);
        action.setRulesMode(rulesMode);
        action.setRules(setpointRepository.getSetpoints());

        sendAction(action, listener);
    }

    public void saveNewSetpoint(
            DbSetpoint setpoint,
            boolean repeatWorkDay,
            boolean repeatWeekend,
            SystemStatusListener listener)
            throws FieldNotSetException, MaxSetpointSizeException {
        validateSetpoint(setpoint, repeatWorkDay, repeatWeekend);

        List<DbSetpoint> setpoints = setpointRepository.getSetpoints();

        if(repeatWorkDay){
            IntStream.rangeClosed(1,5).forEach(day -> {
                DbSetpoint newSetpoint = new DbSetpoint(setpoint);
                newSetpoint.setId(setpointRepository.nextId());
                newSetpoint.setDay(day);
                setpoints.add(newSetpoint);
            });
        }

        if(repeatWeekend){
            IntStream.rangeClosed(6,7).forEach(day -> {
                DbSetpoint newSetpoint = new DbSetpoint(setpoint);
                newSetpoint.setId(setpointRepository.nextId());
                newSetpoint.setDay(day);
                setpoints.add(newSetpoint);
            });
        }

        if(!repeatWorkDay && !repeatWeekend){
            setpoint.setId(setpointRepository.nextId());
            setpoints.add(setpoint);
        }

        DeviceAction action = new DeviceAction(SET_SETPOINTS_ACTION);
        action.setRulesMode(setpointRepository.getHeaterMode());
        action.setRules(setpoints);

        sendAction(action, listener);
    }

    public void updateSetpoint(
            DbSetpoint setpoint,
            SystemStatusListener listener)
            throws FieldNotSetException {
        validateFields(setpoint, false);

        List<DbSetpoint> setpoints = setpointRepository.getSetpoints()
                .stream()
                .map(s -> {
                    if(s.getId().equals(setpoint.getId())) return setpoint;
                    return s;
                }).collect(Collectors.toList());

        DeviceAction action = new DeviceAction(SET_SETPOINTS_ACTION);
        action.setRulesMode(setpointRepository.getHeaterMode());
        action.setRules(setpoints);

        sendAction(action, listener);
    }

    public void deleteSetpoint(
            Integer id,
            SystemStatusListener listener) {
        List<DbSetpoint> setpoints = setpointRepository.getSetpoints()
                .stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());

        DeviceAction action = new DeviceAction(SET_SETPOINTS_ACTION);
        action.setRulesMode(setpointRepository.getHeaterMode());
        action.setRules(setpoints);

        sendAction(action, listener);
    }

    private void sendAction(DeviceAction action, SystemStatusListener listener){
        try {
            mqttService.sendAction(action, systemStatus -> {
                setpointRepository.setSystemStatus(systemStatus);
                listener.systemStatusReceived(systemStatus);
            });
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
        }

        if(repeatWeekend){
            for(int dayOfWeek = 6; dayOfWeek < 8; dayOfWeek++){
                int setpointsInDay = setpointRepository.getSetpoints(dayOfWeek).size();
                if(setpointsInDay >= MAX_SIZE_PER_DAY){
                    throw new MaxSetpointSizeException("Maximum of 5 setpoints can be added to any given day", true);
                }
            }
        }

        if(!repeatWorkDay && !repeatWeekend){
            int setpointsInDay = setpointRepository.getSetpoints(setpoint.getDay()).size();
            if(setpointsInDay >= MAX_SIZE_PER_DAY){
                throw new MaxSetpointSizeException("Maximum of " + MAX_SIZE_PER_DAY + " setpoints can be added to any given day", false);
            }
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
