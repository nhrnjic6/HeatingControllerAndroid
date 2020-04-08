package com.nhrnjic.heatingcontroller.repository;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;
import com.nhrnjic.heatingcontroller.model.SystemStatus;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SetpointRepository {
    private BigDecimal temperature;
    private int heaterMode;
    private Integer currentRuleId;
    private long updatedAt;
    private List<DbSetpoint> setpoints;

    private static SetpointRepository setpointRepository = new SetpointRepository();

    private SetpointRepository(){
        setpoints = new ArrayList<>();
    }

    public static SetpointRepository getInstance(){
        return setpointRepository;
    }

    public SystemStatus getSystemStatus(){
        SystemStatus systemStatus = new SystemStatus();
        systemStatus.setRulesMode(heaterMode);
        systemStatus.setRules(setpoints);
        systemStatus.setTemperature(temperature);
        systemStatus.setId(currentRuleId);
        systemStatus.setUpdatedAt(updatedAt);

        return systemStatus;
    }

    public void setSystemStatus(SystemStatus systemStatus){
        temperature = systemStatus.getTemperature();
        currentRuleId = systemStatus.getId();
        updatedAt = systemStatus.getUpdatedAt();

        if(systemStatus.getRulesMode() != null){
            heaterMode = systemStatus.getRulesMode();
        }

        if(systemStatus.getRules() != null){
            setpoints = systemStatus.getRules();
        }
    }

    public Integer nextId(){
        List<DbSetpoint> sortedByIdDesc = setpoints.stream()
                .sorted((e1, e2) -> e2.getId() - e1.getId())
                .collect(Collectors.toList());

        if(sortedByIdDesc.isEmpty()) return 1;
        return sortedByIdDesc.get(0).getId() + 1;
    }

    public void setSetpoints(List<DbSetpoint> setpoints) {
        this.setpoints = setpoints;
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

    public DbSetpoint findNextSetpoint(){
        DateTime now = DateTime.now();
        long weekPassedMinutes = ((now.getDayOfWeek() - 1) * 24 * 60) + (now.getHourOfDay() * 60) + now.getMinuteOfHour();

        return setpoints.stream()
                .filter(s -> s.getWeekInstant() > weekPassedMinutes)
                .findFirst()
                .orElse(null);
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

    public Integer getCurrentRuleId() {
        return currentRuleId;
    }

    public void setCurrentRuleId(Integer currentRuleId) {
        this.currentRuleId = currentRuleId;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
