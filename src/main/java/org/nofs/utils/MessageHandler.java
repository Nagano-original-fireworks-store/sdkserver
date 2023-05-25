package org.nofs.utils;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/MessageHandler.class */
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
