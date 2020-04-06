package com.nhrnjic.heatingcontroller.repository;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SetpointRepository {
    private BigDecimal temperature;
    private int heaterMode;
    private String currentRuleId;
    private long updatedAt;
    private List<DbSetpoint> setpoints;

    private static SetpointRepository setpointRepository = new SetpointRepository();

    private SetpointRepository(){
        setpoints = new ArrayList<>();
    }

    public static SetpointRepository getInstance(){
        return setpointRepository;
    }

    public Integer nextId(){
        List<DbSetpoint> setpoints = getSetpoints();
        if(setpoints.isEmpty()) return 1;

        return setpoints.get(setpoints.size() - 1).getId() + 1;
    }

    public void setSetpoints(List<DbSetpoint> setpoints) {
        this.setpoints = setpoints;
    }

    public void addSetpoint(DbSetpoint setpoint){
        setpoints.add(setpoint);
    }

    public void removeSetpoint(Integer id){
        setpoints = setpoints.stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<DbSetpoint> getSetpoints(final int day) {
        return setpoints.stream()
                .filter(x -> x.getDay() == day)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<DbSetpoint> getSetpoints() {
        return setpoints.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public DbSetpoint getSetpointById(Integer id) {
        return setpoints.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst().orElseThrow(RuntimeException::new);
    }

    public void updateSetpoint(DbSetpoint updatedSetpoint){
        setpoints = setpoints.stream()
                .map(setpoint -> {
                    if(setpoint.getId().equals(updatedSetpoint.getId())){
                        return updatedSetpoint;
                    }else {
                        return setpoint;
                    }
                }).collect(Collectors.toList());
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public int getHeaterMode() {
        return heaterMode;
    }

    public void setHeaterMode(int heaterMode) {
        this.heaterMode = heaterMode;
    }

    public String getCurrentRuleId() {
        return currentRuleId;
    }

    public void setCurrentRuleId(String currentRuleId) {
        this.currentRuleId = currentRuleId;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
