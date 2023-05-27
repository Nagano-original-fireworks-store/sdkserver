package org.nofs;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.util.ContextInitializer;
import com.google.gson.Gson;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.nofs.auth.AuthenticationSystem;
import org.nofs.auth.DefaultAuthentication;
import org.nofs.config.ConfigContainer;
import org.nofs.config.Configuration;
import org.nofs.database.DatabaseHelper;
import org.nofs.database.DatabaseManager;
import org.nofs.game.Account;
import org.nofs.server.http.HttpServer;
import org.nofs.server.http.dispatch.DispatchHandler;
import org.nofs.server.http.dispatch.RegionHandler;
import org.nofs.server.http.handlers.AnnouncementsHandler;
import org.nofs.server.http.handlers.GenericHandler;
import org.nofs.server.http.handlers.LogHandler;
import org.nofs.utils.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Calendar;
import java.util.Locale;

/* loaded from: sdkserver.jar:emu/grasscutter/Grasscutter.class */
public final class sdkserver {
    private static Language language;
    private static int day;
    private static String preferredLanguage;
    private static HttpServer httpServer;
    private static AuthenticationSystem authenticationSystem;
    public static ConfigContainer config;
    private static final Logger log = (Logger) LoggerFactory.getLogger(sdkserver.class);
    private static LineReader consoleLineReader = null;
    public static final File configFile = new File("./config.json");
    private static ServerRunMode runModeOverride = null;
    public static final Reflections reflector = new Reflections("sdkserver", new Scanner[0]);


    /* loaded from: sdkserver.jar:emu/grasscutter/Grasscutter$ServerDebugMode.class */
    public enum ServerDebugMode {
        ALL,
        MISSING,
        WHITELIST,
        BLACKLIST,
        NONE
    }

    /* loaded from: sdkserver.jar:emu/grasscutter/Grasscutter$ServerRunMode.class */
    public enum ServerRunMode {
        HYBRID,
        DISPATCH_ONLY,
        GAME_ONLY
    }

    static {
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./resources/logback.xml");
        Logger mongoLogger = (Logger) LoggerFactory.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.OFF);
        loadConfig();
        ConfigContainer.updateConfig();
        loadLanguage();
        Utils.startupCheck();
    }

    public static void setRunModeOverride(ServerRunMode runModeOverride2) {
        runModeOverride = runModeOverride2;
    }

    public static String getPreferredLanguage() {
        return preferredLanguage;
    }

    public static void setPreferredLanguage(String preferredLanguage2) {
        preferredLanguage = preferredLanguage2;
    }

    public static void main(String[] args) throws Exception {
        Crypto.loadKeys();
        if (StartupArguments.parse(args)) {
            System.exit(0);
        }
        getLogger().info(Language.translate("messages.status.starting", new Object[0]));
        getLogger().info(Language.translate("messages.status.game_version", GameConstants.VERSION));
//        getLogger().info(Language.translate("messages.status.version", BuildConfig.VERSION, BuildConfig.GIT_HASH));
        DatabaseManager.initialize();
        authenticationSystem = new DefaultAuthentication();
        httpServer = new HttpServer();
        httpServer.addRouter(HttpServer.UnhandledRequestRouter.class, new Object[0]);
        httpServer.addRouter(HttpServer.DefaultRequestRouter.class, new Object[0]);
        httpServer.addRouter(RegionHandler.class, new Object[0]);
        httpServer.addRouter(LogHandler.class, new Object[0]);
        httpServer.addRouter(GenericHandler.class, new Object[0]);
        httpServer.addRouter(AnnouncementsHandler.class, new Object[0]);
        httpServer.addRouter(DispatchHandler.class, new Object[0]);
        httpServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(sdkserver::onShutdown));
        startConsole();
        log.info("messages.status.starting");
    }

    private static void onShutdown() {
    }

    public static void loadLanguage() {
        Locale locale = config.language.language;
        language = Language.getLanguage(Utils.getLanguageCode(locale));
    }

    public static void loadConfig() {
        if (!configFile.exists()) {
            getLogger().info("config.json could not be found. Generating a default configuration ...");
            config = new ConfigContainer();
            saveConfig(config);
            return;
        }
        try {
            config = (ConfigContainer) JsonUtils.loadToClass(configFile.toPath(), ConfigContainer.class);
            Configuration.c = config;
            Configuration.LANGUAGE = config.language.language;
            Configuration.FALLBACK_LANGUAGE = config.language.fallback;
            Configuration.DATA_FOLDER = config.folderStructure.data;
            Configuration.DATABASE = config.databaseInfo;
            Configuration.ACCOUNT = config.account;
            Configuration.HTTP_INFO = config.server.http;
            Configuration.DISPATCH_INFO = config.server.dispatch;
            Configuration.HTTP_ENCRYPTION = config.server.http.encryption;
            Configuration.HTTP_POLICIES = config.server.http.policies;
            Configuration.HTTP_STATIC_FILES = config.server.http.files;
        } catch (Exception e) {
            getLogger().error("There was an error while trying to load the configuration from config.json. Please make sure that there are no syntax errors. If you want to start with a default configuration, delete your existing config.json.");
            System.exit(1);
        }
    }

    public static void saveConfig(@Nullable ConfigContainer config2) {
        if (config2 == null) {
            config2 = new ConfigContainer();
        }
        try {
            FileWriter file = new FileWriter(configFile);
            file.write(JsonUtils.encode(config2));
            file.close();
        } catch (IOException e) {
            getLogger().error("Unable to write to config file.");
        } catch (Exception e2) {
            getLogger().error("Unable to save config file.", (Throwable) e2);
        }
    }

    public static ConfigContainer getConfig() {
        return config;
    }

    public static Language getLanguage() {
        return language;
    }

    public static void setLanguage(Language language2) {
        language = language2;
    }

    public static Language getLanguage(String langCode) {
        return Language.getLanguage(langCode);
    }

    public static Logger getLogger() {
        return log;
    }

    public static LineReader getConsole() {
        if (consoleLineReader == null) {
            Terminal terminal = null;
            try {
                terminal = TerminalBuilder.builder().jna(true).build();
            } catch (Exception e) {
                try {
                    terminal = TerminalBuilder.builder().dumb(true).build();
                } catch (Exception e2) {
                }
            }
            consoleLineReader = LineReaderBuilder.builder().terminal(terminal).build();
        }
        return consoleLineReader;
    }

    public static void startConsole() {
        getLogger().info(Language.translate("messages.status.done", new Object[0]));
        String input = null;
        boolean isLastInterrupted = false;
        while (true) {
            try {
                input = consoleLineReader.readLine("Dispatch> ");
            } catch (IOError e) {
                getLogger().error("An IO error occurred.", (Throwable) e);
            } catch (EndOfFileException e2) {
                getLogger().info("EOF detected.");
            } catch (UserInterruptException e3) {
                if (!isLastInterrupted) {
                    isLastInterrupted = true;

                } else {
                    Runtime.getRuntime().exit(0);
                }
            }
            isLastInterrupted = false;
            try {
                onInput(input);
            } catch (Exception e4) {
                getLogger().error(Language.translate("messages.game.command_error", new Object[0]), (Throwable) e4);
            }
        }
    }

    private static void onInput(String input) {
//        Console console = System.console();
        boolean isLastInterrupted = false;
        if (!isLastInterrupted) {
            getLogger().info(Language.translate("messages.status.shutdown"));
            System.exit(0);
            ;}
        if (input.equals("stop")) {
            getLogger().info(Language.translate("messages.status.shutdown"));
            System.exit(0);
        }
        if (input.equals("reload")) {
            loadConfig();
            getLogger().info("Reload config success!");
        } else if (input.startsWith("account")) {
            String[] args = input.split(" ");
            if (args.length < 3) {
                if (config.account.EXPERIMENTAL_RealPassword) {
                    getLogger().error("account create <name> <password> [uid]");
                    return;
                } else {
                    getLogger().error("account create <name> [uid]");
                    return;
                }
            }
            String username = args[2];
            String str = args[1];
            boolean z = true;
            switch (str.hashCode()) {
                case -1352294148:
                    if (str.equals("create")) {
                        z = true;
                        break;
                    }
                    break;
                case 2024620512:
                    if (str.equals("resetpass")) {
                        z = true;
                        break;
                    }
                    break;
            }
            if (z) {
                int uid = 0;
                String password = "";
                if (config.account.EXPERIMENTAL_RealPassword) {
                    if (args.length >= 4) {
                        password = args[3];
                        if (args.length == 5) {
                            uid = Integer.parseInt(args[4]);
                        }
                    } else {
                        getLogger().error("account create <name> <password> [uid]");
                        return;
                    }
                } else if (args.length == 4) {
                    uid = Integer.parseInt(args[3]);
                }
                Account account = DatabaseHelper.createAccountWithUid(username, uid);
                if (account == null) {
                    getLogger().error(Language.translate("commands.account.exists", new Object[0]));
                    return;
                }
                if (config.account.EXPERIMENTAL_RealPassword) {
                    account.setPassword(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
                }
                if (config.account.defaultPermissions != null) {
                    if (config.account.defaultPermissions.length == 0) {
                        account.addPermission("*");
                    } else {
                        account.addPermission(config.account.defaultPermissions);
                    }
                }
                account.save();
                getLogger().info(Language.translate("commands.account.create", Integer.valueOf(account.getReservedPlayerUid())));
                return;
            } else if (z) {
                if (!Configuration.ACCOUNT.EXPERIMENTAL_RealPassword) {
                    getLogger().error("resetpass requires EXPERIMENTAL_RealPassword to be true.");
                    return;
                } else if (args.length != 3) {
                    getLogger().error("Invalid Args");
                    getLogger().error("Usage: account resetpass <username> <password>");
                    return;
                } else {
                    Account toUpdate = DatabaseHelper.getAccountByName(username);
                    if (toUpdate == null) {
                        getLogger().error(Language.translate("commands.account.no_account", new Object[0]));
                        return;
                    }
                    toUpdate.setPassword(BCrypt.withDefaults().hashToString(12, args[2].toCharArray()));
                    toUpdate.save();
                    getLogger().error("Password Updated.");
                    return;
                }
            }
            if (config.account.EXPERIMENTAL_RealPassword) {
                getLogger().error("account create <name> <password> [uid]");
                getLogger().error("account resetpass <username> <password>");
                return;
            }
            getLogger().error("account create <name> [uid]");
            return;
        }
    }

        public static Gson getGsonFactory() {
        return JsonUtils.getGsonFactory();
    }

    public static HttpServer getHttpServer() {
        return httpServer;
    }

    public static AuthenticationSystem getAuthenticationSystem() {
        return authenticationSystem;
    }

    public static int getCurrentDayOfWeek() {
        return day;
    }

    public static void updateDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(7);
    }

    public static void setAuthenticationSystem(AuthenticationSystem authenticationSystem2) {
        authenticationSystem = authenticationSystem2;
    }
}
