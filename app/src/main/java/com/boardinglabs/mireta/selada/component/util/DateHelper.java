package com.boardinglabs.mireta.selada.component.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Dhimas on 10/6/17.
 */

public class DateHelper {
    private static final String TIME_ZONE = "UTC";



    /**
     * Get unix time until milisecond
     * @return datetime in unix
     */
    public static long getUnixTime() {
        Calendar nowDate = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        nowDate.setLenient(false);
        nowDate.set(Calendar.MILLISECOND, 0);

        return nowDate.getTimeInMillis() / 1000;
    }


    public static String formatDelay(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.US);
        int[] steps = {1, 60, 3600, 3600 * 24};
        String[] names = {"detik lalu", "menit lalu", "jam lalu", "hari lalu"};
        try {
            Date d = format.parse(date);
            Long stamp = d.getTime() / 1000;
            Long now = System.currentTimeMillis() / 1000;
            Long dif = now - stamp;

            if (stamp > now) return "";

            for (int i = 0; i < steps.length; i++) {
                if (dif < steps[i]) {
                    String output = Long.toString(dif / steps[i - 1]) + " " + names[i - 1];
                    return output;
                }
            }

            return Long.toString(dif / steps[3]) + " " + names[3];


        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDateOrTime(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        int[] steps = {1, 60, 3600, 3600 * 24};
        String[] names = {"detik lalu", "menit lalu", "jam lalu", "hari lalu"};
        try {
            Date d = format.parse(date);
            Long stamp = d.getTime() / 1000;
            Long now = System.currentTimeMillis() / 1000;
            Long dif = now - stamp;

            if (stamp > now) return "";

            for (int i = 0; i < steps.length; i++) {
                if (dif < steps[i]) {
                    return timeFormat.format(d);
                }
            }

            return dateFormat.format(d);


        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
