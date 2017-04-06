package com.mcssoft.racemeetings2.utility;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RaceDate {

    public RaceDate() { }

    /**
     * Get today's date as YYYY MM DD
     * @return [0]=YYYY, [1]=MM, [2]=DD
     */
    public int[] getDateComponents() {
        int[] components = new int[3];
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date(getTimeInMillis()));
        components[0] = calendar.get(Calendar.YEAR);
        components[1] = (calendar.get(Calendar.MONTH) + 1); // month starts at 0.
        components[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return components;
    }

    private long getTimeInMillis() {
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }
}
