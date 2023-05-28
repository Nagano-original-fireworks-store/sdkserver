package org.nofs.utils;

/* loaded from: sdkserver.jar:emu/grasscutter/utils/MessageHandler.class */
public class MessageHandler {
    private String message = "";

    public void append(String message) {
        this.message += message + "\r\n\r\n";
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
