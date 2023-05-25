package org.nofs.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.jline.console.Printer;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/StartupArguments.class */
public final class StartupArguments {
    private static final Map<String, Function<String, Boolean>> argumentHandlers = Map.of("-version", StartupArguments::printVersion, "-debug", StartupArguments::enableDebug, "-lang", parameter -> {
        org.nofs.sdkserver.setPreferredLanguage(parameter);
        return false;
    }, "-game", parameter2 -> {
        org.nofs.sdkserver.setRunModeOverride(org.nofs.sdkserver.ServerRunMode.GAME_ONLY);
        return false;
    }, "-dispatch", parameter3 -> {
        org.nofs.sdkserver.setRunModeOverride(org.nofs.sdkserver.ServerRunMode.DISPATCH_ONLY);
        return false;
    }, "-v", StartupArguments::printVersion, "-debugall", parameter4 -> {
        enableDebug(Printer.ALL);
        return false;
    });

    private StartupArguments() {
    }

    public static boolean parse(String[] args) {
        boolean exitEarly = false;
        for (String input : args) {
            boolean containsParameter = input.contains("=");
            String argument = containsParameter ? input.split("=")[0] : input;
            Function<String, Boolean> handler = argumentHandlers.get(argument.toLowerCase());
            if (handler != null) {
                exitEarly |= handler.apply(containsParameter ? input.split("=")[1] : null).booleanValue();
            }
        }
        return exitEarly;
    }

    private static boolean printVersion(String parameter) {
        System.out.println("org.nofs version: 1.0.0-dev-6cd3c228");
        return true;
    }

    private static boolean enableDebug(String parameter) {
        Level loggerLevel = (parameter == null || !parameter.equals(Printer.ALL)) ? Level.INFO : Level.DEBUG;
        org.nofs.sdkserver.getLogger().setLevel(Level.DEBUG);
        org.nofs.sdkserver.getLogger().debug("The logger is now running in debug mode.");
        ((Logger) LoggerFactory.getLogger("io.javalin")).setLevel(loggerLevel);
        ((Logger) LoggerFactory.getLogger("org.quartz")).setLevel(loggerLevel);
        ((Logger) LoggerFactory.getLogger("org.reflections")).setLevel(loggerLevel);
        ((Logger) LoggerFactory.getLogger("org.eclipse.jetty")).setLevel(loggerLevel);
        ((Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(loggerLevel);
        return false;
    }
}
