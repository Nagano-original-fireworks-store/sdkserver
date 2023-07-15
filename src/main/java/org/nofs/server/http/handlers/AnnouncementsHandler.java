package org.nofs.server.http.handlers;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import dev.morphia.mapping.Mapper;
import org.nofs.Grasscutter;
import org.nofs.config.Configuration;
import org.nofs.data.DataLoader;
import org.nofs.server.http.Router;
import org.nofs.server.http.objects.HttpJsonResponse;
import org.nofs.utils.FileUtils;
import org.nofs.utils.Utils;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringJoiner;

/* loaded from: sdkserver.jar:emu/grasscutter/server/http/handlers/AnnouncementsHandler.class */
public final class AnnouncementsHandler implements Router {
    @Override // emu.grasscutter.server.http.Router
    public void applyRoutes(Javalin javalin) {
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAlertPic", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"total\":0,\"list\":[]}}"));
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAlertAnn", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"alert\":false,\"alert_id\":0,\"remind\":true}}"));
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAnnList", AnnouncementsHandler::getAnnouncement);
        allRoutes(javalin, "/common/hk4e_global/announcement/api/getAnnContent", AnnouncementsHandler::getAnnouncement);
        allRoutes(javalin, "/hk4e_global/mdk/shopwindow/shopwindow/listPriceTier", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"suggest_currency\":\"USD\",\"tiers\":[]}}"));
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-version.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-zh-cn.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-zh-tw.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-en-us.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-de-de.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-es-es.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-fr-fr.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-id-id.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-it-it.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-ja-jp.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-ko-kr.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-pt-pt.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-ru-ru.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-th-th.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-tr-tr.json", AnnouncementsHandler::MI18N);
        allRoutes(javalin, "/admin/mi18n/plat_oversea/m2020030410/m2020030410-vi-vn.json", AnnouncementsHandler::MI18N);
        javalin.post("/hk4e_cn/combo/granter/api/compareProtocolVersion", new HttpJsonResponse("{\"retcode\":0,\"message\":\"OK\",\"data\":{\"modified\":true,\"protocol\":{\"id\":0,\"app_id\":4,\"language\":\"zh-cn\",\"user_proto\":\"\",\"priv_proto\":\"\",\"major\":38,\"minimum\":0,\"create_time\":\"0\",\"teenager_proto\":\"\",\"third_proto\":\"\",\"full_priv_proto\":\"\"}}}"));
        javalin.get("/hk4e/announcement/*", AnnouncementsHandler::getPageResources);
    }

    private static void MI18N (Context ctx) {
        String data = "";
        if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-version.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-version.json"));
            } catch (Exception e) {
                if (e.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-version.json'. \n" + e);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-zh-cn.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-zh-cn.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-zh-cn.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-zh-tw.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-zh-tw.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-zh-tw.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-en-us.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-en-us.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-en-us.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-de-de.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-de-de.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-de-de.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-es-es.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-es-es.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-es-es.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-fr-fr.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-fr-fr.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-fr-fr.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-id-id.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-id-id.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-id-id.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-it-it.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-it-it.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-it-it.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-ja-jp.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-ja-jp.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-ja-jp.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-ko-kr.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-ko-kr.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-ko-kr.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-pt-pt.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-pt-pt.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-pt-pt.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-ru-ru.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-ru-ru.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-ru-ru.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-th-th.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-th-th.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-th-th.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-tr-tr.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-tr-tr.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-tr-tr.json'. \n" + e2);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/admin/mi18n/plat_oversea/m2020030410/m2020030410-vi-vn.json")) {
            try {
                data = FileUtils.readToString(DataLoader.load("m2020030410-vi-vn.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'm2020030410-vi-vn.json'. \n" + e2);
                }
            }
        } else {
            ctx.result("{\"retcode\":404,\"message\":\"Unknown request path\"}");
        }
        if (data.isEmpty()) {
            ctx.result("{\"retcode\":500,\"message\":\"Unable to fetch requsted content\"}");
            return;
        }
        ctx.result(data.getBytes(StandardCharsets.UTF_8));
    }

    private static void getAnnouncement (Context ctx) {
        String data = "";
        if (Objects.equals(ctx.endpointHandlerPath(), "/common/hk4e_global/announcement/api/getAnnContent")) {
            try {
                data = FileUtils.readToString(DataLoader.load("GameAnnouncement.json"));
            } catch (Exception e) {
                if (e.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'GameAnnouncementList.json'. \n" + e);
                }
            }
        } else if (Objects.equals(ctx.endpointHandlerPath(), "/common/hk4e_global/announcement/api/getAnnList")) {
            try {
                data = FileUtils.readToString(DataLoader.load("GameAnnouncementList.json"));
            } catch (Exception e2) {
                if (e2.getClass() == IOException.class) {
                    Grasscutter.getLogger().info("Unable to read file 'GameAnnouncementList.json'. \n" + e2);
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
            Grasscutter.getLogger().warn("File does not exist: " + ctx.path());
            ctx.status(404);
        }
    }
}
