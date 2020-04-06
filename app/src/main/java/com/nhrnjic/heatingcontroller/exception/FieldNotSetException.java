package com.nhrnjic.heatingcontroller.exception;

public class FieldNotSetException extends Exception {
    public FieldNotSetException(String message, String field) {
        super(message);
        this.field = field;
    }

    private String field;

    public String getField() {
        return field;
    }
}
