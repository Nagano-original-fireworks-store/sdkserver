package org.nofs.server.http.objects;

import dev.morphia.mapping.Mapper;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.nofs.config.Configuration;
import org.nofs.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: org.nofs.jar:emu/org.nofs/server/http/objects/WebStaticVersionResponse.class */
public class WebStaticVersionResponse implements Handler {
    @Override // io.javalin.http.Handler
    public void handle(Context ctx) throws IOException {
        String requestFor = ctx.path().substring(ctx.path().lastIndexOf("-") + 1);
        getPageResources("/webstatic/" + requestFor, ctx);
    }

    private static void getPageResources(String path, Context ctx) {
        try {
            InputStream filestream = FileUtils.readResourceAsStream(path);
            ContentType fromExtension = ContentType.getContentTypeByExtension(path.substring(path.lastIndexOf(Mapper.IGNORED_FIELDNAME) + 1));
            ctx.contentType(fromExtension != null ? fromExtension : ContentType.APPLICATION_OCTET_STREAM);
            ctx.result(filestream.readAllBytes());
            if (filestream != null) {
                filestream.close();
            }
        } catch (Exception e) {
            if (Configuration.DISPATCH_INFO.logRequests == org.nofs.sdkserver.ServerDebugMode.MISSING) {
                org.nofs.sdkserver.getLogger().warn("Webstatic File Missing: " + path);
            }
            ctx.status(404);
        }
    }
}
