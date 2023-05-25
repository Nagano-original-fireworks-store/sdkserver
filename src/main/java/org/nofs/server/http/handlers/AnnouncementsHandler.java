package org.nofs.server.http.handlers;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import dev.morphia.mapping.Mapper;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import org.nofs.config.Configuration;
import org.nofs.data.DataLoader;
import org.nofs.server.http.Router;
import org.nofs.server.http.objects.HttpJsonResponse;
import org.nofs.utils.FileUtils;
import org.nofs.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.StringJoiner;

/* loaded from: sdkserver.jar:emu/grasscutter/server/http/handlers/AnnouncementsHandler.class */
public final class AnnouncementsHandler implements Router {
    @Override // org.nofs.server.http.Router
    public void applyRoutes(Javalin javalin) {
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAlertPic", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"total\":0,\"list\":[]}}"));
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAlertAnn", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"alert\":false,\"alert_id\":0,\"remind\":true}}"));
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAnnList", AnnouncementsHandler::getAnnouncement);
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAnnContent", AnnouncementsHandler::getAnnouncement);
        allRoutes(javalin, "/hk4e_global/mdk/shopwindow/shopwindow/listPriceTier", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"suggest_currency\":\"USD\",\"tiers\":[]}}"));
        javalin.get("/hk4e/announcement/*", AnnouncementsHandler::getPageResources);
    }

    private static void getAnnouncement(Context ctx) {
        String data = "";
        if (Objects.equals(ctx.endpointHandlerPath(), "/common/hk4e_global/announcement/api/getAnnContent")) {
            try {
                data = FileUtils.readToString(DataLoader.load("GameAnnouncement.json"));
            } catch (Exception e) {
                if (e.getClass() == IOException.class) {
                    org.nofs.sdkserver.getLogger().info("Unable to read file 'GameAnnouncementList.json'. \n" + e);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/common/hk4e_global/announcement/api/getAnnList")) {
            try {
                data = FileUtils.readToString(DataLoader.load("GameAnnouncementList.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    org.nofs.sdkserver.getLogger().info("Unable to read file 'GameAnnouncementList.json'. \n" + e2);
                }
            }
        } else {
            ctx.result("{\"retcode\":404,\"message\":\"Unknown request path\"}");
        }
        if (data.isEmpty()) {
            ctx.result("{\"retcode\":500,\"message\":\"Unable to fetch requsted content\"}");
            return;
        }
        String dispatchDomain = "http" + (Configuration.HTTP_ENCRYPTION.useInRouting ? "s" : "") + "://" + Configuration.lr(Configuration.HTTP_INFO.accessAddress, Configuration.HTTP_INFO.bindAddress) + ":" + Configuration.lr(Configuration.HTTP_INFO.accessPort, Configuration.HTTP_INFO.bindPort);
        ctx.result("{\"retcode\":0,\"message\":\"OK\",\"data\": " + data.replace("{{DISPATCH_PUBLIC}}", dispatchDomain).replace("{{SYSTEM_TIME}}", String.valueOf(System.currentTimeMillis())) + "}");
    }

    private static void getPageResources(Context ctx) {
        String[] path = ctx.path().split("/");
        StringJoiner stringJoiner = new StringJoiner("/");
        for (String pathName : path) {
            if (!pathName.isEmpty() && !pathName.equals(CallerDataConverter.DEFAULT_RANGE_DELIMITER) && !pathName.contains("\\")) {
                stringJoiner.add(pathName);
            }
        }
        try {
            InputStream filestream = DataLoader.load(stringJoiner.toString());
            String possibleFilename = Utils.toFilePath(Configuration.DATA(ctx.path()));
            ContentType fromExtension = ContentType.getContentTypeByExtension(possibleFilename.substring(possibleFilename.lastIndexOf(Mapper.IGNORED_FIELDNAME) + 1));
            ctx.contentType(fromExtension != null ? fromExtension : ContentType.APPLICATION_OCTET_STREAM);
            ctx.result(filestream.readAllBytes());
            if (filestream != null) {
                filestream.close();
            }
        } catch (Exception e) {
            org.nofs.sdkserver.getLogger().warn("File does not exist: " + ctx.path());
            ctx.status(404);
        }
    }
}
