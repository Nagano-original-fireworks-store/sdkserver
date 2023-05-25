package org.nofs.utils;

import java.util.Calendar;
import java.util.Date;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/DateHelper.class */
public final class DateHelper {
    public static Date onlyYearMonthDay(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static int getUnixTime(Date localDateTime) {
        return (int) (localDateTime.getTime() / 1000);
    }
}
