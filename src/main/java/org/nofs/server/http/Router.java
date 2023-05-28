package org.nofs.server.http;

import io.javalin.Javalin;
import io.javalin.http.Handler;

/* loaded from: sdkserver.jar:emu/grasscutter/server/http/Router.class */
public interface    Router {
    void applyRoutes(Javalin javalin);

    default Javalin allRoutes(Javalin javalin, String path, Handler ctx) {
        javalin.get(path, ctx);
        javalin.post(path, ctx);
        javalin.put(path, ctx);
        javalin.patch(path, ctx);
        javalin.delete(path, ctx);
        return javalin;
    }
}
