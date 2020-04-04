package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;
import java.util.UUID;

public class DeviceAction {
    private final UUID requestId;
    private final String actionType;
    private int rulesMode;
    private List<DbSetpoint> rules;
    private int rulesSize;

    public DeviceAction(String action) {
        this.requestId = UUID.randomUUID();
        this.actionType = action;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRulesMode(int rulesMode) {
        this.rulesMode = rulesMode;
    }

    public List<DbSetpoint> getRules() {
        return rules;
    }

    public void setRules(List<DbSetpoint> rules) {
        this.rules = rules;
        this.rulesSize = rules.size();
    }
}
