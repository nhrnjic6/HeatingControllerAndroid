package com.nhrnjic.heatingcontroller.exception;

public class MaxSetpointSizeException extends Exception {
    public MaxSetpointSizeException(String message, boolean isRepeat) {
        super(message);
        this.isRepeat = isRepeat;
    }

    private boolean isRepeat;

    public boolean isRepeat() {
        return isRepeat;
    }
}
