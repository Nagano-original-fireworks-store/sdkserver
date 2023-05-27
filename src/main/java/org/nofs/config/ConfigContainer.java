package org.nofs.config;

import com.google.gson.JsonObject;
import org.nofs.sdkserver;
import org.nofs.utils.JsonUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer.class */
public class ConfigContainer {
    public Structure folderStructure = new Structure();
    public Database databaseInfo = new Database();
    public Language language = new Language();
    public Account account = new Account();
    public Server server = new Server();
    public int version = version();

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Account.class */
    public static class Account {
        public boolean autoCreate = false;
        public boolean EXPERIMENTAL_RealPassword = false;
        public String[] defaultPermissions = new String[0];
        public int maxPlayer = -1;
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Database.class */
    public static class Database {
        public DataStore server = new DataStore();

        /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Database$DataStore.class */
        public static class DataStore {
            public String connectionUri = "mongodb://localhost:27017";
            public String collection = "NOFS";
        }
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Dispatch.class */
    public static class Dispatch {
        public Region[] regions = {new Region()};
        public sdkserver.ServerDebugMode logRequests = org.nofs.sdkserver.ServerDebugMode.NONE;
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Encryption.class */
    public static class Encryption {
        public boolean useEncryption = false;
        public boolean useInRouting = false;
        public String keystore = "./keystore.p12";
        public String keystorePassword = "123456";
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Files.class */
    public static class Files {
        public String indexFile = "./index.html";
        public String errorFile = "./404.html";
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$HTTP.class */
    public static class HTTP {
        public String bindAddress = "0.0.0.0";
        public int bindPort = 443;
        public String accessAddress = "127.0.0.1";
        public int accessPort = 0;
        public Encryption encryption = new Encryption();
        public Policies policies = new Policies();
        public Files files = new Files();
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Language.class */
    public static class Language {
        public Locale language = Locale.getDefault();
        public Locale fallback = Locale.US;
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Policies.class */
    public static class Policies {
        public CORS cors = new CORS();

        /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Policies$CORS.class */
        public static class CORS {
            public boolean enabled = false;
            public String[] allowedOrigins = {"*"};
        }
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Region.class */
    public static class Region {
        public String Name = "dev_client";
        public String Title = "NOFS";
        public String type = "DEV_PUBLIC";
        public String DispatchUrl = "https://127.0.0.1:20001/query_cur_region";

    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Server.class */
    public static class Server {
        public HTTP http = new HTTP();
        public Dispatch dispatch = new Dispatch();
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/config/ConfigContainer$Structure.class */
    public static class Structure {
        public String data = "./data/";
    }

    private static int version() {
        return 5;
    }

    public static void updateConfig() {
        try {
            JsonObject configObject = (JsonObject) JsonUtils.loadToClass(org.nofs.sdkserver.configFile.toPath(), JsonObject.class);
            if (!configObject.has("version")) {
                org.nofs.sdkserver.getLogger().info("Updating legacy ..");
                org.nofs.sdkserver.saveConfig(null);
            }
        } catch (Exception e) {
        }
        int existing = org.nofs.sdkserver.config.version;
        int latest = version();
        if (existing == latest) {
            return;
        }
        ConfigContainer updated = new ConfigContainer();
        Field[] fields = ConfigContainer.class.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                field.set(updated, field.get(org.nofs.sdkserver.config));
            } catch (Exception exception) {
                org.nofs.sdkserver.getLogger().error("Failed to update a configuration field.", (Throwable) exception);
            }
        });
        updated.version = version();
        try {
            org.nofs.sdkserver.saveConfig(updated);
            org.nofs.sdkserver.loadConfig();
        } catch (Exception exception) {
            org.nofs.sdkserver.getLogger().warn("Failed to inject the updated ", (Throwable) exception);
        }
    }
}
