package android.support.v4.util;

import java.io.*;

public class TimeUtils
{
    public static final int HUNDRED_DAY_FIELD_LEN = 19;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static char[] sFormatStr;
    private static final Object sFormatSync;
    
    static {
        sFormatSync = new Object();
        TimeUtils.sFormatStr = new char[24];
    }
    
    private static int accumField(final int n, final int n2, final boolean b, final int n3) {
        if (n > 99 || (b && n3 >= 3)) {
            return n2 + 3;
        }
        if (n > 9 || (b && n3 >= 2)) {
            return n2 + 2;
        }
        if (b || n > 0) {
            return n2 + 1;
        }
        return 0;
    }
    
    public static void formatDuration(final long n, final long n2, final PrintWriter printWriter) {
        if (n == 0L) {
            printWriter.print("--");
            return;
        }
        formatDuration(n - n2, printWriter, 0);
    }
    
    public static void formatDuration(final long n, final PrintWriter printWriter) {
        formatDuration(n, printWriter, 0);
    }
    
    public static void formatDuration(final long n, final PrintWriter printWriter, final int n2) {
        synchronized (TimeUtils.sFormatSync) {
            printWriter.print(new String(TimeUtils.sFormatStr, 0, formatDurationLocked(n, n2)));
        }
    }
    
    public static void formatDuration(final long n, final StringBuilder sb) {
        synchronized (TimeUtils.sFormatSync) {
            sb.append(TimeUtils.sFormatStr, 0, formatDurationLocked(n, 0));
        }
    }
    
    private static int formatDurationLocked(long n, final int n2) {
        if (TimeUtils.sFormatStr.length < n2) {
            TimeUtils.sFormatStr = new char[n2];
        }
        final char[] sFormatStr = TimeUtils.sFormatStr;
        if (n == 0L) {
            while (n2 - 1 < 0) {
                sFormatStr[0] = ' ';
            }
            sFormatStr[0] = '0';
            return 1;
        }
        char c;
        if (n > 0L) {
            c = '+';
        }
        else {
            c = '-';
            n = -n;
        }
        final int n3 = (int)(n % 1000L);
        final int n5;
        int n4 = n5 = (int)Math.floor(n / 1000L);
        int n6 = 0;
        if (n5 > 86400) {
            n6 = n4 / 86400;
            n4 -= 86400 * n6;
        }
        final int n7 = n4;
        int n8 = 0;
        if (n7 > 3600) {
            n8 = n4 / 3600;
            n4 -= n8 * 3600;
        }
        final int n9 = n4;
        int n10 = 0;
        if (n9 > 60) {
            n10 = n4 / 60;
            n4 -= n10 * 60;
        }
        int n11 = 0;
        if (n2 != 0) {
            final int accumField = accumField(n6, 1, false, 0);
            final int n12 = accumField + accumField(n8, 1, accumField > 0, 2);
            final int n13 = n12 + accumField(n10, 1, n12 > 0, 2);
            final int n14 = n13 + accumField(n4, 1, n13 > 0, 2);
            int n15;
            if (n14 > 0) {
                n15 = 3;
            }
            else {
                n15 = 0;
            }
            for (int i = n14 + (1 + accumField(n3, 2, true, n15)); i < n2; ++i) {
                sFormatStr[n11] = ' ';
                ++n11;
            }
        }
        sFormatStr[n11] = c;
        final int n16 = n11 + 1;
        boolean b;
        if (n2 != 0) {
            b = true;
        }
        else {
            b = false;
        }
        final int printField = printField(sFormatStr, n6, 'd', n16, false, 0);
        final boolean b2 = printField != n16;
        int n17;
        if (b) {
            n17 = 2;
        }
        else {
            n17 = 0;
        }
        final int printField2 = printField(sFormatStr, n8, 'h', printField, b2, n17);
        final boolean b3 = printField2 != n16;
        int n18;
        if (b) {
            n18 = 2;
        }
        else {
            n18 = 0;
        }
        final int printField3 = printField(sFormatStr, n10, 'm', printField2, b3, n18);
        final boolean b4 = printField3 != n16;
        int n19;
        if (b) {
            n19 = 2;
        }
        else {
            n19 = 0;
        }
        final int printField4 = printField(sFormatStr, n4, 's', printField3, b4, n19);
        int n20;
        if (b && printField4 != n16) {
            n20 = 3;
        }
        else {
            n20 = 0;
        }
        final int printField5 = printField(sFormatStr, n3, 'm', printField4, true, n20);
        sFormatStr[printField5] = 's';
        return printField5 + 1;
    }
    
    private static int printField(final char[] array, int n, final char c, int n2, final boolean b, final int n3) {
        if (b || n > 0) {
            final int n4 = n2;
            if ((b && n3 >= 3) || n > 99) {
                final int n5 = n / 100;
                array[n2] = (char)(n5 + 48);
                ++n2;
                n -= n5 * 100;
            }
            if ((b && n3 >= 2) || n > 9 || n4 != n2) {
                final int n6 = n / 10;
                array[n2] = (char)(n6 + 48);
                ++n2;
                n -= n6 * 10;
            }
            array[n2] = (char)(n + 48);
            final int n7 = n2 + 1;
            array[n7] = c;
            n2 = n7 + 1;
        }
        return n2;
    }
}
