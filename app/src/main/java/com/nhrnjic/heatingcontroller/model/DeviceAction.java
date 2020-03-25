package com.nhrnjic.heatingcontroller.model;

import java.util.UUID;

public class DeviceAction {
    private UUID actionId;
    private String action;

    public DeviceAction(UUID actionId, String action) {
        this.actionId = actionId;
        this.action = action;
    }

    public UUID getActionId() {
        return actionId;
    }

    public void setActionId(UUID actionId) {
        this.actionId = actionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
