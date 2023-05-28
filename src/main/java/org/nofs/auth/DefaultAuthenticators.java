package org.nofs.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.nofs.Grasscutter;
import org.nofs.auth.AuthenticationSystem;
import org.nofs.auth.OAuthAuthenticator;
import org.nofs.config.Configuration;
import org.nofs.database.DatabaseHelper;
import org.nofs.game.Account;
import org.nofs.server.http.objects.ComboTokenReqJson;
import org.nofs.server.http.objects.ComboTokenResJson;
import org.nofs.server.http.objects.LoginAccountRequestJson;
import org.nofs.server.http.objects.LoginResultJson;
import org.nofs.server.http.objects.LoginTokenRequestJson;
import org.nofs.utils.FileUtils;
import org.nofs.utils.Language;
import org.nofs.utils.Utils;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;

/* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthenticators.class */
public final class DefaultAuthenticators {

    /* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthenticators$PasswordAuthenticator.class */
    public static class PasswordAuthenticator implements Authenticator<LoginResultJson> {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !DefaultAuthenticators.class.desiredAssertionStatus();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // emu.grasscutter.auth.Authenticator
        public LoginResultJson authenticate(AuthenticationSystem.AuthenticationRequest request) {
            LoginResultJson response = new LoginResultJson();
            LoginAccountRequestJson requestData = request.getPasswordRequest();
            if ($assertionsDisabled || requestData != null) {
                boolean successfulLogin = false;
                String address = request.getContext().ip();
                String responseMessage = Language.translate("messages.dispatch.account.username_error", new Object[0]);
                String loggerMessage = "";
                Account account = DatabaseHelper.getAccountByName(requestData.account);
                if (account == null && Configuration.ACCOUNT.autoCreate) {
                    account = DatabaseHelper.createAccountWithUid(requestData.account, 0);
                    if (account == null) {
                        responseMessage = Language.translate("messages.dispatch.account.username_create_error", new Object[0]);
                        Grasscutter.getLogger().info(Language.translate("messages.dispatch.account.account_login_create_error", address));
                    } else {
                        successfulLogin = true;
                        Grasscutter.getLogger().info(Language.translate("messages.dispatch.account.account_login_create_success", address, response.data.account.uid));
                    }
                } else if (account != null) {
                    successfulLogin = true;
                } else {
                    loggerMessage = Language.translate("messages.dispatch.account.account_login_exist_error", address);
                }
                if (successfulLogin) {
                    response.message = "OK";
                    response.data.account.uid = account.getId();
                    response.data.account.token = account.generateSessionKey();
                    response.data.account.email = account.getEmail();
                    loggerMessage = Language.translate("messages.dispatch.account.login_success", address, account.getId());
                } else {
                    response.retcode = -201;
                    response.message = responseMessage;
                }
                Grasscutter.getLogger().info(loggerMessage);
                return response;
            }
            throw new AssertionError();
        }
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthenticators$ExperimentalPasswordAuthenticator.class */
    public static class ExperimentalPasswordAuthenticator implements Authenticator<LoginResultJson> {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !DefaultAuthenticators.class.desiredAssertionStatus();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // emu.grasscutter.auth.Authenticator
        public LoginResultJson authenticate(AuthenticationSystem.AuthenticationRequest request) {
            String decryptedPassword;
            LoginResultJson response = new LoginResultJson();
            LoginAccountRequestJson requestData = request.getPasswordRequest();
            if ($assertionsDisabled || requestData != null) {
                boolean successfulLogin = false;
                String address = request.getContext().ip();
                String responseMessage = Language.translate("messages.dispatch.account.username_error", new Object[0]);
                String loggerMessage = "";
                try {
                    byte[] key = FileUtils.readResource("/keys/auth_private-key.der");
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    RSAPrivateKey private_key = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(2, private_key);
                    decryptedPassword = new String(cipher.doFinal(Utils.base64Decode(request.getPasswordRequest().password)), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    decryptedPassword = request.getPasswordRequest().password;
                }
                if (decryptedPassword == null) {
                    successfulLogin = false;
                    loggerMessage = Language.translate("messages.dispatch.account.login_password_error", address);
                    responseMessage = Language.translate("messages.dispatch.account.password_error", new Object[0]);
                }
                Account account = DatabaseHelper.getAccountByName(requestData.account);
                if (account == null && Configuration.ACCOUNT.autoCreate) {
                    if (decryptedPassword.length() >= 8) {
                        account = DatabaseHelper.createAccountWithUid(requestData.account, 0);
                        account.setPassword(BCrypt.withDefaults().hashToString(12, decryptedPassword.toCharArray()));
                        account.save();
                        if (account == null) {
                            responseMessage = Language.translate("messages.dispatch.account.username_create_error", new Object[0]);
                            loggerMessage = Language.translate("messages.dispatch.account.account_login_create_error", address);
                        } else {
                            successfulLogin = true;
                            Grasscutter.getLogger().info(Language.translate("messages.dispatch.account.account_login_create_success", address, response.data.account.uid));
                        }
                    } else {
                        successfulLogin = false;
                        loggerMessage = Language.translate("messages.dispatch.account.login_password_error", address);
                        responseMessage = Language.translate("messages.dispatch.account.password_length_error", new Object[0]);
                    }
                } else if (account != null) {
                    if (account.getPassword() != null && !account.getPassword().isEmpty()) {
                        if (BCrypt.verifyer().verify(decryptedPassword.toCharArray(), account.getPassword()).verified) {
                            successfulLogin = true;
                        } else {
                            successfulLogin = false;
                            loggerMessage = Language.translate("messages.dispatch.account.login_password_error", address);
                            responseMessage = Language.translate("messages.dispatch.account.password_error", new Object[0]);
                        }
                    } else {
                        successfulLogin = false;
                        loggerMessage = Language.translate("messages.dispatch.account.login_password_storage_error", address);
                        responseMessage = Language.translate("messages.dispatch.account.password_storage_error", new Object[0]);
                    }
                } else {
                    loggerMessage = Language.translate("messages.dispatch.account.account_login_exist_error", address);
                }
                if (successfulLogin) {
                    response.message = "OK";
                    response.data.account.uid = account.getId();
                    response.data.account.token = account.generateSessionKey();
                    response.data.account.email = account.getEmail();
                    loggerMessage = Language.translate("messages.dispatch.account.login_success", address, account.getId());
                } else {
                    response.retcode = -201;
                    response.message = responseMessage;
                }
                Grasscutter.getLogger().info(loggerMessage);
                return response;
            }
            throw new AssertionError();
        }
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthenticators$TokenAuthenticator.class */
    public static class TokenAuthenticator implements Authenticator<LoginResultJson> {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !DefaultAuthenticators.class.desiredAssertionStatus();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // emu.grasscutter.auth.Authenticator
        public LoginResultJson authenticate(AuthenticationSystem.AuthenticationRequest request) {
            String loggerMessage;
            LoginResultJson response = new LoginResultJson();
            LoginTokenRequestJson requestData = request.getTokenRequest();
            if ($assertionsDisabled || requestData != null) {
                String address = request.getContext().ip();
                Grasscutter.getLogger().info(Language.translate("messages.dispatch.account.login_token_attempt", address));
                Account account = DatabaseHelper.getAccountById(requestData.uid);
                boolean successfulLogin = account != null && account.getSessionKey().equals(requestData.token);
                if (successfulLogin) {
                    response.message = "OK";
                    response.data.account.uid = account.getId();
                    response.data.account.token = account.getSessionKey();
                    response.data.account.email = account.getEmail();
                    loggerMessage = Language.translate("messages.dispatch.account.login_token_success", address, requestData.uid);
                } else {
                    response.retcode = -201;
                    response.message = Language.translate("messages.dispatch.account.account_cache_error", new Object[0]);
                    loggerMessage = Language.translate("messages.dispatch.account.login_token_error", address);
                }
                Grasscutter.getLogger().info(loggerMessage);
                return response;
            }
            throw new AssertionError();
        }
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthenticators$SessionKeyAuthenticator.class */
    public static class SessionKeyAuthenticator implements Authenticator<ComboTokenResJson> {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !DefaultAuthenticators.class.desiredAssertionStatus();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // emu.grasscutter.auth.Authenticator
        public ComboTokenResJson authenticate(AuthenticationSystem.AuthenticationRequest request) {
            String loggerMessage;
            ComboTokenResJson response = new ComboTokenResJson();
            ComboTokenReqJson requestData = request.getSessionKeyRequest();
            ComboTokenReqJson.LoginTokenData loginData = request.getSessionKeyData();
            if ($assertionsDisabled || requestData != null) {
                if ($assertionsDisabled || loginData != null) {
                    String address = request.getContext().ip();
                    Account account = DatabaseHelper.getAccountById(loginData.uid);
                    boolean successfulLogin = account != null && account.getSessionKey().equals(loginData.token);
                    if (successfulLogin) {
                        response.message = "OK";
                        response.data.open_id = account.getId();
                        response.data.combo_id = "157795300";
                        response.data.combo_token = account.generateLoginToken();
                        loggerMessage = Language.translate("messages.dispatch.account.combo_token_success", address);
                    } else {
                        response.retcode = -201;
                        response.message = Language.translate("messages.dispatch.account.session_key_error", new Object[0]);
                        loggerMessage = Language.translate("messages.dispatch.account.combo_token_error", address);
                    }
                    Grasscutter.getLogger().info(loggerMessage);
                    return response;
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthenticators$ExternalAuthentication.class */
    public static class ExternalAuthentication implements ExternalAuthenticator {
        @Override // emu.grasscutter.auth.ExternalAuthenticator
        public void handleLogin(AuthenticationSystem.AuthenticationRequest request) {
            request.getContext().result("Authentication is not available with the default authentication method.");
        }

        @Override // emu.grasscutter.auth.ExternalAuthenticator
        public void handleAccountCreation(AuthenticationSystem.AuthenticationRequest request) {
            request.getContext().result("Authentication is not available with the default authentication method.");
        }

        @Override // emu.grasscutter.auth.ExternalAuthenticator
        public void handlePasswordReset(AuthenticationSystem.AuthenticationRequest request) {
            request.getContext().result("Authentication is not available with the default authentication method.");
        }
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/auth/DefaultAuthenticators$OAuthAuthentication.class */
    public static class OAuthAuthentication implements OAuthAuthenticator {
        @Override // emu.grasscutter.auth.OAuthAuthenticator
        public void handleLogin(AuthenticationSystem.AuthenticationRequest request) {
            request.getContext().result("Authentication is not available with the default authentication method.");
        }

        @Override // emu.grasscutter.auth.OAuthAuthenticator
        public void handleRedirection(AuthenticationSystem.AuthenticationRequest request, OAuthAuthenticator.ClientType type) {
            request.getContext().result("Authentication is not available with the default authentication method.");
        }

        @Override // emu.grasscutter.auth.OAuthAuthenticator
        public void handleTokenProcess(AuthenticationSystem.AuthenticationRequest request) {
            request.getContext().result("Authentication is not available with the default authentication method.");
        }
    }
}
