package org.nofs.server.http.dispatch;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.nofs.auth.AuthenticationSystem;
import org.nofs.auth.OAuthAuthenticator;
import org.nofs.server.http.Router;
import org.nofs.server.http.objects.*;
import org.nofs.utils.JsonUtils;
import org.nofs.utils.Language;

/* loaded from: org.nofs.jar:emu/org.nofs/server/http/dispatch/DispatchHandler.class */
public final class DispatchHandler implements Router {
    @Override // org.nofs.server.http.Router
    public void applyRoutes(Javalin javalin) {
        javalin.post("/hk4e_global/mdk/shield/api/login", DispatchHandler::clientLogin);
        javalin.post("/hk4e_global/mdk/shield/api/verify", DispatchHandler::tokenLogin);
        javalin.post("/hk4e_global/combo/granter/login/v2/login", DispatchHandler::sessionKeyLogin);
        javalin.get("/authentication/type", ctx -> {
            ctx.result(org.nofs.sdkserver.getAuthenticationSystem().getClass().getSimpleName());
        });
        javalin.post("/authentication/login", ctx2 -> {
            org.nofs.sdkserver.getAuthenticationSystem().getExternalAuthenticator().handleLogin(AuthenticationSystem.fromExternalRequest(ctx2));
        });
        javalin.post("/authentication/register", ctx3 -> {
            org.nofs.sdkserver.getAuthenticationSystem().getExternalAuthenticator().handleAccountCreation(AuthenticationSystem.fromExternalRequest(ctx3));
        });
        javalin.post("/authentication/change_password", ctx4 -> {
            org.nofs.sdkserver.getAuthenticationSystem().getExternalAuthenticator().handlePasswordReset(AuthenticationSystem.fromExternalRequest(ctx4));
        });
        javalin.post("/hk4e_global/mdk/shield/api/loginByThirdparty", ctx5 -> {
            org.nofs.sdkserver.getAuthenticationSystem().getOAuthAuthenticator().handleLogin(AuthenticationSystem.fromExternalRequest(ctx5));
        });
        javalin.get("/authentication/openid/redirect", ctx6 -> {
            org.nofs.sdkserver.getAuthenticationSystem().getOAuthAuthenticator().handleTokenProcess(AuthenticationSystem.fromExternalRequest(ctx6));
        });
        javalin.get("/Api/twitter_login", ctx7 -> {
            org.nofs.sdkserver.getAuthenticationSystem().getOAuthAuthenticator().handleRedirection(AuthenticationSystem.fromExternalRequest(ctx7), OAuthAuthenticator.ClientType.DESKTOP);
        });
        javalin.get("/sdkTwitterLogin.html", ctx8 -> {
            org.nofs.sdkserver.getAuthenticationSystem().getOAuthAuthenticator().handleRedirection(AuthenticationSystem.fromExternalRequest(ctx8), OAuthAuthenticator.ClientType.MOBILE);
        });
    }

    private static void clientLogin(Context ctx) {
        String rawBodyData = ctx.body();
        LoginAccountRequestJson bodyData = (LoginAccountRequestJson) JsonUtils.decode(rawBodyData, LoginAccountRequestJson.class);
        if (bodyData == null) {
            return;
        }
        LoginResultJson responseData = org.nofs.sdkserver.getAuthenticationSystem().getPasswordAuthenticator().authenticate(AuthenticationSystem.fromPasswordRequest(ctx, bodyData));
        ctx.json(responseData);
        org.nofs.sdkserver.getLogger().info(Language.translate("messages.dispatch.account.login_attempt", ctx.ip()));
    }

    private static void tokenLogin(Context ctx) {
        String rawBodyData = ctx.body();
        LoginTokenRequestJson bodyData = (LoginTokenRequestJson) JsonUtils.decode(rawBodyData, LoginTokenRequestJson.class);
        if (bodyData == null) {
            return;
        }
        LoginResultJson responseData = org.nofs.sdkserver.getAuthenticationSystem().getTokenAuthenticator().authenticate(AuthenticationSystem.fromTokenRequest(ctx, bodyData));
        ctx.json(responseData);
        org.nofs.sdkserver.getLogger().info(Language.translate("messages.dispatch.account.login_attempt", ctx.ip()));
    }

    private static void sessionKeyLogin(Context ctx) {
        String rawBodyData = ctx.body();
        ComboTokenReqJson bodyData = (ComboTokenReqJson) JsonUtils.decode(rawBodyData, ComboTokenReqJson.class);
        if (bodyData == null || bodyData.data == null) {
            return;
        }
        ComboTokenReqJson.LoginTokenData tokenData = (ComboTokenReqJson.LoginTokenData) JsonUtils.decode(bodyData.data, ComboTokenReqJson.LoginTokenData.class);
        ComboTokenResJson responseData = org.nofs.sdkserver.getAuthenticationSystem().getSessionKeyAuthenticator().authenticate(AuthenticationSystem.fromComboTokenRequest(ctx, bodyData, tokenData));
        ctx.json(responseData);
        org.nofs.sdkserver.getLogger().info(Language.translate("messages.dispatch.account.login_attempt", ctx.ip()));
    }
}
