package org.nofs.server.http;

import dev.morphia.mapping.Mapper;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.nofs.config.Configuration;
import org.nofs.utils.FileUtils;
import org.nofs.utils.Language;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;

/* loaded from: org.nofs.jar:emu/org.nofs/server/http/HttpServer.class */
public final class HttpServer {
    private final Javalin javalin = Javalin.create(config -> {
        config.server(HttpServer::createServer);
        config.enforceSsl = Configuration.HTTP_ENCRYPTION.useEncryption;
        if (Configuration.HTTP_POLICIES.cors.enabled) {
            String[] allowedOrigins = Configuration.HTTP_POLICIES.cors.allowedOrigins;
            if (allowedOrigins.length > 0) {
                config.enableCorsForOrigin(allowedOrigins);
            } else {
                config.enableCorsForAllOrigins();
            }
        }
        if (Configuration.DISPATCH_INFO.logRequests == org.nofs.sdkserver.ServerDebugMode.ALL) {
            config.enableDevLogging();
        }
    });

    private static Server createServer() {
        Server server = new Server();
        ServerConnector serverConnector = new ServerConnector(server);
        if (Configuration.HTTP_ENCRYPTION.useEncryption) {
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            File keystoreFile = new File(Configuration.HTTP_ENCRYPTION.keystore);
            try {
                if (!keystoreFile.exists()) {
                    Configuration.HTTP_ENCRYPTION.useEncryption = false;
                    Configuration.HTTP_ENCRYPTION.useInRouting = false;
                    org.nofs.sdkserver.getLogger().warn(Language.translate("messages.dispatch.keystore.no_keystore_error", new Object[0]));
                } else {
                    try {
                        sslContextFactory.setKeyStorePath(keystoreFile.getPath());
                        sslContextFactory.setKeyStorePassword(Configuration.HTTP_ENCRYPTION.keystorePassword);
                        serverConnector = new ServerConnector(server, sslContextFactory);
                    } catch (Exception e) {
                        org.nofs.sdkserver.getLogger().warn(Language.translate("messages.dispatch.keystore.password_error", new Object[0]));
                        try {
                            sslContextFactory.setKeyStorePath(keystoreFile.getPath());
                            sslContextFactory.setKeyStorePassword("123456");
                            org.nofs.sdkserver.getLogger().warn(Language.translate("messages.dispatch.keystore.default_password", new Object[0]));
                        } catch (Exception exception) {
                            org.nofs.sdkserver.getLogger().warn(Language.translate("messages.dispatch.keystore.general_error", new Object[0]), (Throwable) exception);
                        }
                        serverConnector = new ServerConnector(server, sslContextFactory);
                    }
                }
            } catch (Throwable th) {
                new ServerConnector(server, sslContextFactory);
                throw th;
            }
        }
        serverConnector.setPort(Configuration.HTTP_INFO.bindPort);
        server.setConnectors(new ServerConnector[]{serverConnector});
        return server;
    }

    public Javalin getHandle() {
        return this.javalin;
    }

    public HttpServer addRouter(Class<? extends Router> router, Object... args) {
        Class<?>[] types = new Class[args.length];
        for (Object argument : args) {
            types[args.length - 1] = argument.getClass();
        }
        try {
            Constructor<? extends Router> constructor = router.getDeclaredConstructor(types);
            Router routerInstance = constructor.newInstance(args);
            routerInstance.applyRoutes(this.javalin);
        } catch (Exception exception) {
            org.nofs.sdkserver.getLogger().warn(Language.translate("messages.dispatch.router_error", new Object[0]), (Throwable) exception);
        }
        return this;
    }

    public void start() throws UnsupportedEncodingException {
        if (Configuration.HTTP_INFO.bindAddress.equals("")) {
            this.javalin.start(Configuration.HTTP_INFO.bindPort);
        } else {
            this.javalin.start(Configuration.HTTP_INFO.bindAddress, Configuration.HTTP_INFO.bindPort);
        }
        org.nofs.sdkserver.getLogger().info(Language.translate("messages.dispatch.address_bind", Configuration.HTTP_INFO.accessAddress, Integer.valueOf(this.javalin.port())));
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/server/http/HttpServer$DefaultRequestRouter.class */
    public static class DefaultRequestRouter implements Router {
        @Override // org.nofs.server.http.Router
        public void applyRoutes(Javalin javalin) {
            javalin.get("/", ctx -> {
                File file = new File(Configuration.HTTP_STATIC_FILES.indexFile);
                if (!file.exists()) {
                    ctx.contentType(ContentType.TEXT_HTML);
                    ctx.result("<!DOCTYPE html>\n<html>\n    <head>\n        <meta charset=\"utf8\">\n    </head>\n    <body>%s</body>\n</html>\n".formatted(new Object[]{Language.translate("messages.status.welcome", new Object[0])}));
                    return;
                }
                String filePath = file.getPath();
                ContentType fromExtension = ContentType.getContentTypeByExtension(filePath.substring(filePath.lastIndexOf(Mapper.IGNORED_FIELDNAME) + 1));
                ctx.contentType(fromExtension != null ? fromExtension : ContentType.TEXT_HTML);
                ctx.result(FileUtils.read(filePath));
            });
        }
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/server/http/HttpServer$UnhandledRequestRouter.class */
    public static class UnhandledRequestRouter implements Router {
        @Override // org.nofs.server.http.Router
        public void applyRoutes(Javalin javalin) {
            javalin.error(404, ctx -> {
                if (Configuration.DISPATCH_INFO.logRequests == org.nofs.sdkserver.ServerDebugMode.MISSING) {
                    org.nofs.sdkserver.getLogger().info(Language.translate("messages.dispatch.unhandled_request_error", ctx.method(), ctx.url()));
                }
                File file = new File(Configuration.HTTP_STATIC_FILES.errorFile);
                if (!file.exists()) {
                    ctx.contentType(ContentType.TEXT_HTML);
                    ctx.result("<!DOCTYPE html>\n<html>\n    <head>\n        <meta charset=\"utf8\">\n    </head>\n\n    <body>\n        <img src=\"https://http.cat/404\" />\n    </body>\n</html>\n");
                    return;
                }
                String filePath = file.getPath();
                ContentType fromExtension = ContentType.getContentTypeByExtension(filePath.substring(filePath.lastIndexOf(Mapper.IGNORED_FIELDNAME) + 1));
                ctx.contentType(fromExtension != null ? fromExtension : ContentType.TEXT_HTML);
                ctx.result(FileUtils.read(filePath));
            });
        }
    }
}
