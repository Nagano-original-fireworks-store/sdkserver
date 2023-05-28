package org.nofs.server.http.objects;

/* loaded from: sdkserver.jar:emu/grasscutter/server/http/objects/ComboTokenResJson.class */
public class ComboTokenResJson {
    public String message;
    public int retcode;
    public LoginData data = new LoginData();

    /* loaded from: sdkserver.jar:emu/grasscutter/server/http/objects/ComboTokenResJson$LoginData.class */
    public static class LoginData {
        public boolean heartbeat;
        public String combo_id;
        public String combo_token;
        public String open_id;
        public int account_type = 1;
        public String data = "{\"guest\":false}";
        public String fatigue_remind = null;
    }
}
