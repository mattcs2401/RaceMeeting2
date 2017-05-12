package com.mcssoft.racemeetings2.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to manipulate date and time related strings.
 */
public class DateTime {

    public DateTime() { }

    /**
     * Generate the YYYY, MM, DD elements of a meeting date based on the current date.
     * @return The date as: [0]-YYYY, [1]-M(M), [2]-D(D)
     */
    public String[] getCurrentDateComponents() {
        String[] date = new String[3];
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date(getTimeInMillis()));
        date[0] = Integer.toString(calendar.get(Calendar.YEAR));
        date[1] = Integer.toString(calendar.get(Calendar.MONTH) + 1); // month starts at 0.
        date[2] = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        return date;
    }

    /**
     * Generate the YYYY, MM, DD elements of a meeting date based on the given parameter.
     * @param raceDate Date value as YYYY-MM-DD.
     * @return The date as: [0]-YYYY, [1]-M(M), [2]-D(D)
     */
    public String[] getDateComponents(String raceDate) {
        String[] date = raceDate.split("-");
        String[] temp = null;
        if((temp = date[1].split("0")).length > 1) {
            date[1] = temp[1];
        }
        if((temp = date[2].split("0")).length > 1) {
            date[2] = temp[1];
        }
        return date;
    }

    /**
     * Extract the time from the Xml time value.
     * @param time formatted as "YYYY-MM-DDTHH:MM:SS" (from Xml).
     * @return time as HH:MM format.
     */
    public String getTimeComponent(String time) {
        String[] array = (time.split("T")[1]).split(":");
        return (array[0] + ":" + array[1]);
    }

    private long getTimeInMillis() {
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }
}
