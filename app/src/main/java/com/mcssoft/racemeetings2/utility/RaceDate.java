package com.mcssoft.racemeetings2.utility;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RaceDate {

    public RaceDate() { }

    /**
     * Get today's date as YYYY MM DD
     * @return [0]=YYYY, [1]=M(M), [2]=D(D)
     */
    public String[] getDateComponents() {
        String[] components = new String[3];
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date(getTimeInMillis()));
        components[0] = Integer.toString(calendar.get(Calendar.YEAR));
        components[1] = Integer.toString(calendar.get(Calendar.MONTH) + 1); // month starts at 0.
        components[2] = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        return components;
    }

    private long getTimeInMillis() {
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }
}
