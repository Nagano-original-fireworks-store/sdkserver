package org.nofs.server.event.dispatch;

import org.nofs.server.event.types.ServerEvent;

public final class QueryAllRegionsEvent extends ServerEvent {
    private String regionList;
    
    public QueryAllRegionsEvent(String regionList) {
        super(Type.DISPATCH);
        
        this.regionList = regionList;
    }
    
    public void setRegionList(String regionList) {
        this.regionList = regionList;
    }
    
    public String getRegionList() {
        return this.regionList;
    }
}
