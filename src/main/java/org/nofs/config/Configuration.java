package org.nofs.config;


import org.nofs.sdkserver;

import java.nio.file.Path;
import java.util.Locale;

/* loaded from: org.nofs.jar:emu/org.nofs/config/Configuration.class */
public final class Configuration extends ConfigContainer {
    public static ConfigContainer c = org.nofs.sdkserver.config;
    public static Locale LANGUAGE = org.nofs.sdkserver.config.language.language;
    public static Locale FALLBACK_LANGUAGE = org.nofs.sdkserver.config.language.fallback;
    public static String DATA_FOLDER = org.nofs.sdkserver.config.folderStructure.data;
    public static Database DATABASE = org.nofs.sdkserver.config.databaseInfo;
    public static Account ACCOUNT = org.nofs.sdkserver.config.account;
    public static HTTP HTTP_INFO = sdkserver.config.server.http;
    public static game GAME_INFO = sdkserver.config.server.game;
    public static Dispatch DISPATCH_INFO = org.nofs.sdkserver.config.server.dispatch;
    public static HTTP defaultName= sdkserver.config.server.http;
    public static Encryption HTTP_ENCRYPTION = org.nofs.sdkserver.config.server.http.encryption;
    public static Policies HTTP_POLICIES = org.nofs.sdkserver.config.server.http.policies;
    public static Files HTTP_STATIC_FILES = org.nofs.sdkserver.config.server.http.files;

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
