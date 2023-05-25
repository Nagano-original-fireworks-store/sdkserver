package org.nofs.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.jline.reader.LineReader;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/JlineLogbackAppender.class */
public class JlineLogbackAppender extends ConsoleAppender<ILoggingEvent> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase
    public void append(ILoggingEvent eventObject) {
        if (!this.started) {
            return;
        }
        Stream stream = Arrays.stream(new String(this.encoder.encode(eventObject)).split("\n\r"));
        LineReader console = org.nofs.sdkserver.getConsole();
        Objects.requireNonNull(console);
            stream.forEach(this::printAbove);
    }