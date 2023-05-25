package org.nofs.auth;

/* loaded from: org.nofs.jar:emu/grasscutter/auth/ExternalAuthenticator.class */
public interface ExternalAuthenticator {
    void handleLogin(AuthenticationSystem.AuthenticationRequest authenticationRequest);

    void handleAccountCreation(AuthenticationSystem.AuthenticationRequest authenticationRequest);

    void handlePasswordReset(AuthenticationSystem.AuthenticationRequest authenticationRequest);
}
