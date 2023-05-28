package org.nofs.auth;

import org.nofs.auth.AuthenticationSystem;

/* loaded from: sdkserver.jar:emu/grasscutter/auth/OAuthAuthenticator.class */
public interface OAuthAuthenticator {

    /* loaded from: sdkserver.jar:emu/grasscutter/auth/OAuthAuthenticator$ClientType.class */
    public enum ClientType {
        DESKTOP,
        MOBILE
    }

    void handleLogin(AuthenticationSystem.AuthenticationRequest authenticationRequest);

    void handleRedirection(AuthenticationSystem.AuthenticationRequest authenticationRequest, ClientType clientType);

    void handleTokenProcess(AuthenticationSystem.AuthenticationRequest authenticationRequest);
}
