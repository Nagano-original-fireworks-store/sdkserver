package org.nofs.config;

import org.nofs.Grasscutter;
import org.nofs.config.ConfigContainer;

import java.nio.file.Path;
import java.util.Locale;

/* loaded from: sdkserver.jar:emu/grasscutter/config/Configuration.class */
public final class Configuration extends ConfigContainer {
    public static ConfigContainer c = Grasscutter.config;
    public static Locale LANGUAGE = Grasscutter.config.language.language;
    public static Locale FALLBACK_LANGUAGE = Grasscutter.config.language.fallback;
    public static String DATA_FOLDER = Grasscutter.config.folderStructure.data;
    public static Database DATABASE = Grasscutter.config.databaseInfo;
    public static Account ACCOUNT = Grasscutter.config.account;
    public static HTTP HTTP_INFO = Grasscutter.config.server.http;
    public static Dispatch DISPATCH_INFO = Grasscutter.config.server.dispatch;
    public static Encryption HTTP_ENCRYPTION = Grasscutter.config.server.http.encryption;
    public static Policies HTTP_POLICIES = Grasscutter.config.server.http.policies;
    public static Files HTTP_STATIC_FILES = Grasscutter.config.server.http.files;

    public static String DATA() {
        return DATA_FOLDER;
    }

    public static String DATA(String path) {
        return Path.of(DATA_FOLDER, new String[]{path}).toString();
    }

    public static <T> T lr(T left, T right) {
        return left == null ? right : left;
    }

    public static String lr(String left, String right) {
        return left.isEmpty() ? right : left;
    }

    public static int lr(int left, int right) {
        return left == 0 ? right : left;
    }
}
