package org.nofs.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.nofs.Grasscutter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.jline.reader.LineReader;

/* loaded from: sdkserver.jar:emu/grasscutter/utils/JlineLogbackAppender.class */
public class JlineLogbackAppender extends ConsoleAppender<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!started) {
            return;
        }
        Arrays.stream(
                new String(encoder.encode(eventObject)).split("\n\r")
        ).forEach(Grasscutter.getConsole()::printAbove);
    }
}
