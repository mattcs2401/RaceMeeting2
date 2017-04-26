package com.mcssoft.racemeetings2.utility;


import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RaceDate {

    public RaceDate() { }

    /**
     * Get today's date as YYYY MM DD
     * @return [0]=YYYY, [1]=M(M), [2]=D(D)
     */
    public String[] getDateComponents(@Nullable String raceDate) {
        String[] date = new String[3];
        if(raceDate == null) {
            // get today's date.
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(new Date(getTimeInMillis()));
            date[0] = Integer.toString(calendar.get(Calendar.YEAR));
            date[1] = Integer.toString(calendar.get(Calendar.MONTH) + 1); // month starts at 0.
            date[2] = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            date = raceDate.split("-");
            String[] temp = null;
            if((temp = date[1].split("0")).length > 1) {
                date[1] = temp[1];
            }
            if((temp = date[2].split("0")).length > 1) {
                date[2] = temp[1];
            }
        }
        return date;
    }

    private long getTimeInMillis() {
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }
}
