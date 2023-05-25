package org.nofs.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

import java.util.Arrays;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/JlineLogbackAppender.class */
public class JlineLogbackAppender extends ConsoleAppender<ILoggingEvent> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!started) {
            return;
        }
        Arrays.stream(
                new String(encoder.encode(eventObject)).split("\n\r")
        ).forEach(org.nofs.sdkserver.getConsole()::printAbove);
    }
}