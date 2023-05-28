package org.nofs.config;

import com.google.gson.JsonObject;
import org.nofs.Grasscutter;
import org.nofs.utils.JsonUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer.class */
public class ConfigContainer {
    public Structure folderStructure = new Structure();
    public Database databaseInfo = new Database();
    public Language language = new Language();
    public Account account = new Account();
    public Server server = new Server();
    public int version = version();

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Account.class */
    public static class Account {
        public boolean autoCreate = false;
        public boolean EXPERIMENTAL_RealPassword = false;
        public String[] defaultPermissions = new String[0];
        public int maxPlayer = -1;
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Database.class */
    public static class Database {
        public DataStore server = new DataStore();

        /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Database$DataStore.class */
        public static class DataStore {
            public String connectionUri = "mongodb://localhost:27017";
            public String collection = "grasscutter";
        }
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Dispatch.class */
    public static class Dispatch {
        public Region[] regions = {new Region()};
        public Grasscutter.ServerDebugMode logRequests = Grasscutter.ServerDebugMode.NONE;
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Encryption.class */
    public static class Encryption {
        public boolean useEncryption = true;
        public boolean useInRouting = true;
        public String keystore = "./keystore.p12";
        public String keystorePassword = "123456";
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Files.class */
    public static class Files {
        public String indexFile = "./index.html";
        public String errorFile = "./404.html";
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$HTTP.class */
    public static class HTTP {
        public String bindAddress = "0.0.0.0";
        public int bindPort = 443;
        public String accessAddress = "127.0.0.1";
        public int accessPort = 0;
        public Encryption encryption = new Encryption();
        public Policies policies = new Policies();
        public Files files = new Files();
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Language.class */
    public static class Language {
        public Locale language = Locale.getDefault();
        public Locale fallback = Locale.US;
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Policies.class */
    public static class Policies {
        public CORS cors = new CORS();

        /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Policies$CORS.class */
        public static class CORS {
            public boolean enabled = false;
            public String[] allowedOrigins = {"*"};
        }
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Region.class */
    public static class Region {
        public String Name = "os_usa";
        public String Title = "Grasscutter";
        public String type = "DEV_PUBLIC";
        public String DispatchUrl = "https://127.0.0.1:20001/query_cur_region";
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Server.class */
    public static class Server {
        public HTTP http = new HTTP();
        public Dispatch dispatch = new Dispatch();
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/config/ConfigContainer$Structure.class */
    public static class Structure {
        public String data = "./data/";
    }

    private static int version() {
        return 5;
    }

    public static void updateConfig() {
        try {
            JsonObject configObject = (JsonObject) JsonUtils.loadToClass(Grasscutter.configFile.toPath(), JsonObject.class);
            if (!configObject.has("version")) {
                Grasscutter.getLogger().info("Updating legacy ..");
                Grasscutter.saveConfig(null);
            }
        } catch (Exception e) {
        }
        int existing = Grasscutter.config.version;
        int latest = version();
        if (existing == latest) {
            return;
        }
        ConfigContainer updated = new ConfigContainer();
        Field[] fields = ConfigContainer.class.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                field.set(updated, field.get(Grasscutter.config));
            } catch (Exception exception) {
                Grasscutter.getLogger().error("Failed to update a configuration field.", (Throwable) exception);
            }
        });
        updated.version = version();
        try {
            Grasscutter.saveConfig(updated);
            Grasscutter.loadConfig();
        } catch (Exception exception) {
            Grasscutter.getLogger().warn("Failed to inject the updated ", (Throwable) exception);
        }
    }
}
