package org.nofs.server.http.handlers;

import org.nofs.server.http.Router;
import io.javalin.Javalin;
import io.javalin.http.Context;

/* loaded from: sdkserver.jar:emu/grasscutter/server/http/handlers/LogHandler.class */
public final class LogHandler implements Router {
    @Override // emu.grasscutter.server.http.Router
    public void applyRoutes(Javalin javalin) {
        javalin.post("/log", LogHandler::log);
        javalin.post("/crash/dataUpload", LogHandler::log);
    }

    private static void log(Context ctx) {
        ctx.result("{\"code\":0}");
    }
}
