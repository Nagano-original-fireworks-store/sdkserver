package org.nofs.auth;

import org.nofs.auth.AuthenticationSystem;

/* loaded from: sdkserver.jar:emu/grasscutter/auth/Authenticator.class */
public interface Authenticator<T> {
    T authenticate(AuthenticationSystem.AuthenticationRequest authenticationRequest);
}
