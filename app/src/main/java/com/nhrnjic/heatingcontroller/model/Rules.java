package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import java.util.List;

public class Rules {
    private int rulesSize;
    private int rulesMode;
    private List<DbSetpoint> rules;

    public Rules() {
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

    public int getRulesMode() {
        return rulesMode;
    }

    public void setRulesMode(int rulesMode) {
        this.rulesMode = rulesMode;
    }
}
