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

    /**
     * Create the RaceDay Url
     * https://tatts.com/pagedata/racing/YYYY/M(M)/D(D)/RaceDay.xml
     * @param raceDate Optional date value. If not set then current date used.
     * @return The Url.
     */
    public String createRaceDayUrl(@Nullable String[] raceDate) {
        if(raceDate == null) {
            raceDate = getDateComponents(null);
        }
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
                .appendPath(raceDate[0])
                .appendPath(raceDate[1])
                .appendPath(raceDate[2])
                .appendPath(Resources.getInstance().getString(R.string.race_day_listing));
        builder.build();
        return builder.toString();
    }

    /**
     * Create the Meeting Url
     * https://tatts.com/pagedata/racing/YYYY/M(M)/D(D)/code.xml
     * @param meetingDate The meeting date value.
     * @param meetingCode The meeting code value.
     * @return The Url.
     */
    public String createMeetingUrl(String[] meetingDate, String meetingCode) {
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
                .appendPath(meetingDate[0])
                .appendPath(meetingDate[1])
                .appendPath(meetingDate[2])
                .appendPath(meetingCode + ".xml");
        builder.build();
        return builder.toString();
    }

    /**
     * Create the Race Url.
     * https://tatts.com/pagedata/racing/YYYY/M(M)/D(D)/code+number.xml
     * @param meetingDate The meeting date value.
     * @param meetingCode The meeting code value.
     * @param raceNo The race number.
     * @return The Url
     */
    public String createRaceUrl(String[] meetingDate, String meetingCode, String raceNo) {
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
                .appendPath(meetingDate[0])
                .appendPath(meetingDate[1])
                .appendPath(meetingDate[2])
                .appendPath(meetingCode + raceNo + ".xml");
        builder.build();
        return builder.toString();
    }

    /**
     * generate the YYYY, MM, DD elements of a meeting date.
     * @param raceDate Optional date value. If not set then current date used.
     * @return The date as: [0]-YYYY, [1]-M(M), [2]-D(D)
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
