package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;
import java.util.UUID;

public class Rules {
    private UUID newRulesUUID;
    private int rulesSize;
    private List<DbSetpoint> rules;

    public Rules() {
    }

    public UUID getNewRulesUUID() {
        return newRulesUUID;
    }

    public void setNewRulesUUID(UUID newRulesUUID) {
        this.newRulesUUID = newRulesUUID;
    }

    public List<DbSetpoint> getRules() {
        return rules;
    }

    public void setRules(List<DbSetpoint> rules) {
        this.rules = rules;
    }

    public int getRulesSize() {
        return rulesSize;
    }

    public void setRulesSize(int rulesSize) {
        this.rulesSize = rulesSize;
    }
}
