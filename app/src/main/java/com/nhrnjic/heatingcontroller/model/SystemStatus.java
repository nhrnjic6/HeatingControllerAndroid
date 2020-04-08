package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SystemStatus {
    private UUID requestId;
    private Integer id;
    private BigDecimal temperature;
    private Long updatedAt;
    private Integer rulesMode;
    private List<DbSetpoint> rules;

    public SystemStatus() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DbSetpoint> getRules() {
        return rules;
    }

    public void setRules(List<DbSetpoint> rules) {
        this.rules = rules;
    }

    public Integer getRulesMode() {
        return rulesMode;
    }

    public void setRulesMode(Integer rulesMode) {
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
