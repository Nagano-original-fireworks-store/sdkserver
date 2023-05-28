package org.nofs.auth;

import org.nofs.auth.AuthenticationSystem;

/* loaded from: sdkserver.jar:emu/grasscutter/auth/ExternalAuthenticator.class */
public interface ExternalAuthenticator {
    void handleLogin(AuthenticationSystem.AuthenticationRequest authenticationRequest);

    void handleAccountCreation(AuthenticationSystem.AuthenticationRequest authenticationRequest);

    void handlePasswordReset(AuthenticationSystem.AuthenticationRequest authenticationRequest);
}
