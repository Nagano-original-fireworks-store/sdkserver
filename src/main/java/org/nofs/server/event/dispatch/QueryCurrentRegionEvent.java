package org.nofs.server.event.dispatch;

import org.nofs.server.event.types.ServerEvent;

public final class QueryCurrentRegionEvent extends ServerEvent {
    private String regionInfo;

    public QueryCurrentRegionEvent(String regionInfo) {
        super(Type.DISPATCH);

        this.regionInfo = regionInfo;
    }

    public void setRegionInfo(String regionInfo) {
        this.regionInfo = regionInfo;
    }

    public String getRegionInfo() {
        return this.regionInfo;
    }
}