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
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase
    public void append(ILoggingEvent eventObject) {
        if (!this.started) {
            return;
        }
        Stream stream = Arrays.stream(new String(this.encoder.encode(eventObject)).split("\n\r"));
        LineReader console = Grasscutter.getConsole();
        Objects.requireNonNull(console);
        stream.forEach(this::printAbove);
    }
}
