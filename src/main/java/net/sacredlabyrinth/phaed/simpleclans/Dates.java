package net.sacredlabyrinth.phaed.simpleclans;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */

public class Dates {

    private Dates() {
    }

    public static double differenceInMonths(Timestamp date1, Timestamp date2) {
        return differenceInMonths(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    public static double differenceInYears(Timestamp date1, Timestamp date2) {
        return differenceInYears(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    public static double differenceInDays(Timestamp date1, Timestamp date2) {
        return differenceInDays(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    public static double differenceInHours(Timestamp date1, Timestamp date2) {
        return differenceInHours(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    public static double differenceInMinutes(Timestamp date1, Timestamp date2) {
        return differenceInMinutes(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    public static double differenceInSeconds(Timestamp date1, Timestamp date2) {
        return differenceInSeconds(new Date(date1.getTime()), new Date(date2.getTime()));
    }

    public static double differenceInMonths(Date date1, Date date2) {
        return differenceInYears(date1, date2) * 12;
    }

    public static double differenceInYears(Date date1, Date date2) {
        double days = differenceInDays(date1, date2);
        return days / 365.2425;
    }

    public static double differenceInDays(Date date1, Date date2) {
        return differenceInHours(date1, date2) / 24.0;
    }

    public static double differenceInHours(Date date1, Date date2) {
        return differenceInMinutes(date1, date2) / 60.0;
    }

    public static double differenceInMinutes(Date date1, Date date2) {
        return differenceInSeconds(date1, date2) / 60.0;
    }

    public static double differenceInSeconds(Date date1, Date date2) {
        return differenceInMilliseconds(date1, date2) / 1000.0;
    }

    private static double differenceInMilliseconds(Date date1, Date date2) {
        return Math.abs(getTimeInMilliseconds(date1) - getTimeInMilliseconds(date2));
    }

    private static long getTimeInMilliseconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());
    }

    /**
     * @author RoboMWM, LaxWasHere
     * @param seconds the time in seconds
     * @param depth Max amount of detail (e.g. only display days and hours if set to 1 and seconds {@literal >} 1 day)
     * @return the string representation of the amount of time labeled as days, hours, minutes, seconds
     */
    public static String formatTime(Long seconds, int depth) {
        if (seconds == null || seconds < 1) {
            return lang("bb.moments");
        }

        if (seconds < 60) {
            return seconds + lang("bb.seconds");
        }

        if (seconds < 3600) {
            long count = (long) Math.ceil(seconds / 60f);
            String res;
            if (count > 1) {
                res = count + lang("bb.minutes");
            } else {
                res = lang("bb.one.minute");
            }
            long remaining = seconds % 60;
            if (depth > 0 && remaining >= 5) {
                return res + ", " + formatTime(remaining, --depth);
            }
            return res;
        }
        if (seconds < 86400) {
            long count = (long) Math.ceil(seconds / 3600f);
            String res;
            if (count > 1) {
                res = count + lang("bb.hours");
            } else {
                res = lang("bb.one.hour");
            }
            if (depth > 0) {
                return res + ", " + formatTime(seconds % 3600, --depth);
            }
            return res;
        }
        long count = (long) Math.ceil(seconds / 86400f);
        String res;
        if (count > 1) {
            res = count + lang("bb.days");
        } else {
            res = lang("bb.one.day");
        }
        if (depth > 0) {
            return res + ", " + formatTime(seconds % 86400, --depth);
        }
        return res;
    }
}
