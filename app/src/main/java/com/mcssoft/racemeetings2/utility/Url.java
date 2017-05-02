package com.mcssoft.racemeetings2.utility;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.mcssoft.racemeetings2.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to create download Urls.
 */
public class Url {

    public void Url() { }

    public String createRaceDayUrl(@Nullable String[] date) {
        if(date == null) {
            date = getDateComponents(null);
        }
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
                .appendPath(date[0])
                .appendPath(date[1])
                .appendPath(date[2])
                .appendPath(Resources.getInstance().getString(R.string.race_day_listing));
        builder.build();
        return builder.toString();
    }

    public String createMeetingUrl(String[] date, String code) {
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
                .appendPath(date[0])
                .appendPath(date[1])
                .appendPath(date[2])
                .appendPath(code + ".xml");
        builder.build();
        return builder.toString();
    }

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
