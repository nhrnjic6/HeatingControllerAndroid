package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;
import java.util.UUID;

public class DeviceAction {
    private UUID requestId;
    private String actionType;
    private int rulesMode;
    private List<DbSetpoint> rules;

    public DeviceAction(){

    }

    public DeviceAction(UUID requestId, String action) {
        this.requestId = requestId;
        this.actionType = action;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getRulesMode() {
        return rulesMode;
    }

    public void setRulesMode(int rulesMode) {
        this.rulesMode = rulesMode;
    }

    public List<DbSetpoint> getRules() {
        return rules;
    }

    public void setRules(List<DbSetpoint> rules) {
        this.rules = rules;
    }
}
