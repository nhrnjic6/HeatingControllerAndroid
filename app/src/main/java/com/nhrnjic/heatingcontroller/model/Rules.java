package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;
import java.util.UUID;

public class Rules {
    private UUID newRulesUUID;
    private List<DbSetpoint> setpoints;

    public Rules() {
    }

    public UUID getNewRulesUUID() {
        return newRulesUUID;
    }

    public void setNewRulesUUID(UUID newRulesUUID) {
        this.newRulesUUID = newRulesUUID;
    }

    public List<DbSetpoint> getSetpoints() {
        return setpoints;
    }

    public void setSetpoints(List<DbSetpoint> setpoints) {
        this.setpoints = setpoints;
    }
}
