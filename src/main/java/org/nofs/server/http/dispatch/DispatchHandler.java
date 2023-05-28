    package org.nofs.server.http.dispatch;

    import org.nofs.Grasscutter;
    import org.nofs.auth.AuthenticationSystem;
    import org.nofs.auth.OAuthAuthenticator;
    import org.nofs.server.http.Router;
    import org.nofs.server.http.objects.ComboTokenReqJson;
    import org.nofs.server.http.objects.ComboTokenResJson;
    import org.nofs.server.http.objects.LoginAccountRequestJson;
    import org.nofs.server.http.objects.LoginResultJson;
    import org.nofs.server.http.objects.LoginTokenRequestJson;
    import org.nofs.utils.JsonUtils;
    import org.nofs.utils.Language;
    import io.javalin.Javalin;
    import io.javalin.http.Context;

    /* loaded from: sdkserver.jar:emu/grasscutter/server/http/dispatch/DispatchHandler.class */
    public final class DispatchHandler implements Router {
        @Override // emu.grasscutter.server.http.Router
        public void applyRoutes(Javalin javalin) {
            javalin.post("/hk4e_global/mdk/shield/api/login", DispatchHandler::clientLogin);
            javalin.post("/hk4e_global/mdk/shield/api/verify", DispatchHandler::tokenLogin);
            javalin.post("/hk4e_global/combo/granter/login/v2/login", DispatchHandler::sessionKeyLogin);
            javalin.get("/authentication/type", ctx -> {
                ctx.result(Grasscutter.getAuthenticationSystem().getClass().getSimpleName());
            });
            javalin.post("/authentication/login", ctx2 -> {
                Grasscutter.getAuthenticationSystem().getExternalAuthenticator().handleLogin(AuthenticationSystem.fromExternalRequest(ctx2));
            });
            javalin.post("/authentication/register", ctx3 -> {
                Grasscutter.getAuthenticationSystem().getExternalAuthenticator().handleAccountCreation(AuthenticationSystem.fromExternalRequest(ctx3));
            });
            javalin.post("/authentication/change_password", ctx4 -> {
                Grasscutter.getAuthenticationSystem().getExternalAuthenticator().handlePasswordReset(AuthenticationSystem.fromExternalRequest(ctx4));
            });
            javalin.post("/hk4e_global/mdk/shield/api/loginByThirdparty", ctx5 -> {
                Grasscutter.getAuthenticationSystem().getOAuthAuthenticator().handleLogin(AuthenticationSystem.fromExternalRequest(ctx5));
            });
            javalin.get("/authentication/openid/redirect", ctx6 -> {
                Grasscutter.getAuthenticationSystem().getOAuthAuthenticator().handleTokenProcess(AuthenticationSystem.fromExternalRequest(ctx6));
            });
            javalin.get("/Api/twitter_login", ctx7 -> {
                Grasscutter.getAuthenticationSystem().getOAuthAuthenticator().handleRedirection(AuthenticationSystem.fromExternalRequest(ctx7), OAuthAuthenticator.ClientType.DESKTOP);
            });
            javalin.get("/sdkTwitterLogin.html", ctx8 -> {
                Grasscutter.getAuthenticationSystem().getOAuthAuthenticator().handleRedirection(AuthenticationSystem.fromExternalRequest(ctx8), OAuthAuthenticator.ClientType.MOBILE);
            });
        }

        private static void clientLogin(Context ctx) {
            String rawBodyData = ctx.body();
            LoginAccountRequestJson bodyData = (LoginAccountRequestJson) JsonUtils.decode(rawBodyData, LoginAccountRequestJson.class);
            if (bodyData == null) {
                return;
            }
            LoginResultJson responseData = Grasscutter.getAuthenticationSystem().getPasswordAuthenticator().authenticate(AuthenticationSystem.fromPasswordRequest(ctx, bodyData));
            ctx.json(responseData);
            Grasscutter.getLogger().info(Language.translate("messages.dispatch.account.login_attempt", ctx.ip()));
        }

        private static void tokenLogin(Context ctx) {
            String rawBodyData = ctx.body();
            LoginTokenRequestJson bodyData = (LoginTokenRequestJson) JsonUtils.decode(rawBodyData, LoginTokenRequestJson.class);
            if (bodyData == null) {
                return;
            }
            LoginResultJson responseData = Grasscutter.getAuthenticationSystem().getTokenAuthenticator().authenticate(AuthenticationSystem.fromTokenRequest(ctx, bodyData));
            ctx.json(responseData);
            Grasscutter.getLogger().info(Language.translate("messages.dispatch.account.login_attempt", ctx.ip()));
        }

        private static void sessionKeyLogin(Context ctx) {
            String rawBodyData = ctx.body();
            ComboTokenReqJson bodyData = (ComboTokenReqJson) JsonUtils.decode(rawBodyData, ComboTokenReqJson.class);
            if (bodyData == null || bodyData.data == null) {
                return;
            }
            ComboTokenReqJson.LoginTokenData tokenData = (ComboTokenReqJson.LoginTokenData) JsonUtils.decode(bodyData.data, ComboTokenReqJson.LoginTokenData.class);
            ComboTokenResJson responseData = Grasscutter.getAuthenticationSystem().getSessionKeyAuthenticator().authenticate(AuthenticationSystem.fromComboTokenRequest(ctx, bodyData, tokenData));
            ctx.json(responseData);
            Grasscutter.getLogger().info(Language.translate("messages.dispatch.account.login_attempt", ctx.ip()));
        }
    }
