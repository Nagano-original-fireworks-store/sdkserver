package org.nofs.auth;

import org.nofs.game.Account;
import org.nofs.server.http.objects.ComboTokenReqJson;
import org.nofs.server.http.objects.ComboTokenResJson;
import org.nofs.server.http.objects.LoginAccountRequestJson;
import org.nofs.server.http.objects.LoginResultJson;
import org.nofs.server.http.objects.LoginTokenRequestJson;
import io.javalin.http.Context;
import javax.annotation.Nullable;

/* loaded from: org.nofs.jar:emu/grasscutter/auth/AuthenticationSystem.class */
public interface AuthenticationSystem {
    void createAccount(String str, String str2);

    void resetPassword(String str);

    Account verifyUser(String str);

    Authenticator<LoginResultJson> getPasswordAuthenticator();

    Authenticator<LoginResultJson> getTokenAuthenticator();

    Authenticator<ComboTokenResJson> getSessionKeyAuthenticator();

    ExternalAuthenticator getExternalAuthenticator();

    OAuthAuthenticator getOAuthAuthenticator();

    /* loaded from: org.nofs.jar:emu/grasscutter/auth/AuthenticationSystem$AuthenticationRequest.class */
    public static class AuthenticationRequest {
        private final Context context;
        @Nullable
        private final LoginAccountRequestJson passwordRequest;
        @Nullable
        private final LoginTokenRequestJson tokenRequest;
        @Nullable
        private final ComboTokenReqJson sessionKeyRequest;
        @Nullable
        private final ComboTokenReqJson.LoginTokenData sessionKeyData;

        /* loaded from: org.nofs.jar:emu/grasscutter/auth/AuthenticationSystem$AuthenticationRequest$AuthenticationRequestBuilder.class */
        public static class AuthenticationRequestBuilder {
            private Context context;
            private LoginAccountRequestJson passwordRequest;
            private LoginTokenRequestJson tokenRequest;
            private ComboTokenReqJson sessionKeyRequest;
            private ComboTokenReqJson.LoginTokenData sessionKeyData;

            AuthenticationRequestBuilder() {
            }

            public AuthenticationRequestBuilder context(Context context) {
                this.context = context;
                return this;
            }

            public AuthenticationRequestBuilder passwordRequest(@Nullable LoginAccountRequestJson passwordRequest) {
                this.passwordRequest = passwordRequest;
                return this;
            }

            public AuthenticationRequestBuilder tokenRequest(@Nullable LoginTokenRequestJson tokenRequest) {
                this.tokenRequest = tokenRequest;
                return this;
            }

            public AuthenticationRequestBuilder sessionKeyRequest(@Nullable ComboTokenReqJson sessionKeyRequest) {
                this.sessionKeyRequest = sessionKeyRequest;
                return this;
            }

            public AuthenticationRequestBuilder sessionKeyData(@Nullable ComboTokenReqJson.LoginTokenData sessionKeyData) {
                this.sessionKeyData = sessionKeyData;
                return this;
            }

            public AuthenticationRequest build() {
                return new AuthenticationRequest(this.context, this.passwordRequest, this.tokenRequest, this.sessionKeyRequest, this.sessionKeyData);
            }

            public String toString() {
                return "AuthenticationSystem.AuthenticationRequest.AuthenticationRequestBuilder(context=" + this.context + ", passwordRequest=" + this.passwordRequest + ", tokenRequest=" + this.tokenRequest + ", sessionKeyRequest=" + this.sessionKeyRequest + ", sessionKeyData=" + this.sessionKeyData + ")";
            }
        }

        public static AuthenticationRequestBuilder builder() {
            return new AuthenticationRequestBuilder();
        }

        public AuthenticationRequest(Context context, @Nullable LoginAccountRequestJson passwordRequest, @Nullable LoginTokenRequestJson tokenRequest, @Nullable ComboTokenReqJson sessionKeyRequest, @Nullable ComboTokenReqJson.LoginTokenData sessionKeyData) {
            this.context = context;
            this.passwordRequest = passwordRequest;
            this.tokenRequest = tokenRequest;
            this.sessionKeyRequest = sessionKeyRequest;
            this.sessionKeyData = sessionKeyData;
        }

        public Context getContext() {
            return this.context;
        }

        @Nullable
        public LoginAccountRequestJson getPasswordRequest() {
            return this.passwordRequest;
        }

        @Nullable
        public LoginTokenRequestJson getTokenRequest() {
            return this.tokenRequest;
        }

        @Nullable
        public ComboTokenReqJson getSessionKeyRequest() {
            return this.sessionKeyRequest;
        }

        @Nullable
        public ComboTokenReqJson.LoginTokenData getSessionKeyData() {
            return this.sessionKeyData;
        }
    }

    static AuthenticationRequest fromPasswordRequest(Context ctx, LoginAccountRequestJson jsonData) {
        return AuthenticationRequest.builder().context(ctx).passwordRequest(jsonData).build();
    }

    static AuthenticationRequest fromTokenRequest(Context ctx, LoginTokenRequestJson jsonData) {
        return AuthenticationRequest.builder().context(ctx).tokenRequest(jsonData).build();
    }

    static AuthenticationRequest fromComboTokenRequest(Context ctx, ComboTokenReqJson jsonData, ComboTokenReqJson.LoginTokenData tokenData) {
        return AuthenticationRequest.builder().context(ctx).sessionKeyRequest(jsonData).sessionKeyData(tokenData).build();
    }

    static AuthenticationRequest fromExternalRequest(Context ctx) {
        return AuthenticationRequest.builder().context(ctx).build();
    }
}
