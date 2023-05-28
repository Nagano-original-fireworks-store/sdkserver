package org.nofs.auth;

import org.nofs.Grasscutter;
import org.nofs.auth.DefaultAuthenticators;
import org.nofs.config.Configuration;
import org.nofs.game.Account;
import org.nofs.server.http.objects.ComboTokenResJson;
import org.nofs.server.http.objects.LoginResultJson;
import org.nofs.utils.Language;

/* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthentication.class */
public final class DefaultAuthentication implements AuthenticationSystem {
    private final Authenticator<LoginResultJson> passwordAuthenticator;
    private final Authenticator<LoginResultJson> tokenAuthenticator = new DefaultAuthenticators.TokenAuthenticator();
    private final Authenticator<ComboTokenResJson> sessionKeyAuthenticator = new DefaultAuthenticators.SessionKeyAuthenticator();
    private final ExternalAuthenticator externalAuthenticator = new DefaultAuthenticators.ExternalAuthentication();
    private final OAuthAuthenticator oAuthAuthenticator = new DefaultAuthenticators.OAuthAuthentication();

    public DefaultAuthentication() {
        if (Configuration.ACCOUNT.EXPERIMENTAL_RealPassword) {
            this.passwordAuthenticator = new DefaultAuthenticators.ExperimentalPasswordAuthenticator();
        } else {
            this.passwordAuthenticator = new DefaultAuthenticators.PasswordAuthenticator();
        }
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public void createAccount(String username, String password) {
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public void resetPassword(String username) {
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public Account verifyUser(String details) {
        Grasscutter.getLogger().info(Language.translate("messages.dispatch.authentication.default_unable_to_verify", new Object[0]));
        return null;
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public Authenticator<LoginResultJson> getPasswordAuthenticator() {
        return this.passwordAuthenticator;
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public Authenticator<LoginResultJson> getTokenAuthenticator() {
        return this.tokenAuthenticator;
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public Authenticator<ComboTokenResJson> getSessionKeyAuthenticator() {
        return this.sessionKeyAuthenticator;
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public ExternalAuthenticator getExternalAuthenticator() {
        return this.externalAuthenticator;
    }

    @Override // emu.grasscutter.auth.AuthenticationSystem
    public OAuthAuthenticator getOAuthAuthenticator() {
        return this.oAuthAuthenticator;
    }
}
