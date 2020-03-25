package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SystemStatus {
    private UUID requestId;
    private String id;
    private BigDecimal temperature;
    private long updatedAt;
    private int rulesMode;
    private List<DbSetpoint> rules;

    public SystemStatus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public String getTemperatureRounded() {
        temperature = temperature.setScale(2 ,BigDecimal.ROUND_HALF_UP);
        return temperature.toString();
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DbSetpoint> getRules() {
        return rules;
    }

    public void setRules(List<DbSetpoint> rules) {
        this.rules = rules;
    }

    public int getRulesMode() {
        return rulesMode;
    }

    public void setRulesMode(int rulesMode) {
        this.rulesMode = rulesMode;
    }

    public String formattedUpdatedAt(){
        return new DateTime(updatedAt * 1000, DateTimeZone.UTC)
                .toString("HH:mm:ss");
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }
}
