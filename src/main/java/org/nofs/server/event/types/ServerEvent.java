package org.nofs.server.event.types;

import org.nofs.server.event.Event;

public abstract class ServerEvent extends Event {
    protected final Type type;

    public ServerEvent(Type type) {
        this.type = type;
    }

    public Type getServerType() {
        return this.type;
    }

    public enum Type {
        DISPATCH,
        GAME
    }
}
