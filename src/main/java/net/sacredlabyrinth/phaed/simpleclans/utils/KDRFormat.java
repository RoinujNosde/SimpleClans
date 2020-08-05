package net.sacredlabyrinth.phaed.simpleclans.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * @author RoinujNosde
 */
public class KDRFormat {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");

    static {
        FORMATTER.setParseBigDecimal(true);
    }

    private KDRFormat() {}

    @NotNull
    public static String format(float kdr) {
        return FORMATTER.format(kdr);
    }

    @NotNull
    public static BigDecimal parse(String kdr) {
        try {
            return (BigDecimal) FORMATTER.parse(kdr);
        } catch (ParseException e) {
            return new BigDecimal("-1");
        }
    }

    @NotNull
    public static BigDecimal toBigDecimal(float kdr) {
        return parse(format(kdr));
    }
}
