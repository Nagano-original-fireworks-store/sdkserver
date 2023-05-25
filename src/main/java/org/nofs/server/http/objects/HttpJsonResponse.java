package org.nofs.server.http.objects;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.nofs.config.Configuration;
import org.nofs.utils.Language;

import java.util.Arrays;
import java.util.Objects;

/* loaded from: org.nofs.jar:emu/org.nofs/server/http/objects/HttpJsonResponse.class */
public final class HttpJsonResponse implements Handler {
    private final String response;
    private final String[] missingRoutes = {"/common/hk4e_global/announcement/api/getAlertPic", "/common/hk4e_global/announcement/api/getAlertAnn", "/common/hk4e_global/announcement/api/getAnnList", "/common/hk4e_global/announcement/api/getAnnContent", "/hk4e_global/mdk/shopwindow/shopwindow/listPriceTier", "/log/sdk/upload", "/sdk/upload", "/perf/config/verify", "/log", "/crash/dataUpload"};

    public HttpJsonResponse(String response) {
        this.response = response;
    }

    @Override // io.javalin.http.Handler
    public void handle(@NotNull Context ctx) throws Exception {
        if (Configuration.DISPATCH_INFO.logRequests == org.nofs.sdkserver.ServerDebugMode.MISSING && Arrays.stream(this.missingRoutes).anyMatch(x -> {
            return Objects.equals(x, ctx.endpointHandlerPath());
        })) {
            org.nofs.sdkserver.getLogger().info(Language.translate("messages.dispatch.request", ctx.ip(), ctx.method(), ctx.endpointHandlerPath()) + (Configuration.DISPATCH_INFO.logRequests == org.nofs.sdkserver.ServerDebugMode.MISSING ? "(MISSING)" : ""));
        }
        ctx.result(this.response);
    }
}
