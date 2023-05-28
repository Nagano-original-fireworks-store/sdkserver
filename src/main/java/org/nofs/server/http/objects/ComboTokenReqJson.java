package org.nofs.server.http.objects;

/* loaded from: sdkserver.jar:emu/grasscutter/server/http/objects/ComboTokenReqJson.class */
public class ComboTokenReqJson {
    public int app_id;
    public int channel_id;
    public String data;
    public String device;
    public String sign;

    /* loaded from: sdkserver.jar:emu/grasscutter/server/http/objects/ComboTokenReqJson$LoginTokenData.class */
    public static class LoginTokenData {
        public String uid;
        public String token;
        public boolean guest;
    }
}
