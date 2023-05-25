package org.nofs.auth;

/* loaded from: org.nofs.jar:emu/grasscutter/auth/Authenticator.class */
public interface Authenticator<T> {
    T authenticate(AuthenticationSystem.AuthenticationRequest authenticationRequest);
}
