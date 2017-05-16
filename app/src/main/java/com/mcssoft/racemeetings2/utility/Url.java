package com.mcssoft.racemeetings2.utility;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.mcssoft.racemeetings2.R;

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
            DateTime dateTime = new DateTime();
            raceDate = dateTime.getCurrentDateComponents();
        }
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Resources.getInstance().getString(R.string.base_path))
               .appendPath(raceDate[0]);

        // Remove leading zeros if exist.
        if(raceDate[1].charAt(0) == '0') {
            builder.appendPath(raceDate[1].substring(1));
        } else {
            builder.appendPath(raceDate[1]);
        }
        if(raceDate[2].charAt(0) == '0') {
            builder.appendPath(raceDate[2].substring(1));
        } else {
            builder.appendPath(raceDate[2]);
        }

        builder.appendPath(Resources.getInstance().getString(R.string.race_day_listing));
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
               .appendPath(meetingDate[0]);

        // Remove leading zeros if exist..
        if(meetingDate[1].charAt(0) == '0') {
            builder.appendPath(meetingDate[1].substring(1));
        } else {
            builder.appendPath(meetingDate[1]);
        }
        if(meetingDate[2].charAt(0) == '0') {
            builder.appendPath(meetingDate[2].substring(1));
        } else {
            builder.appendPath(meetingDate[2]);
        }

        builder.appendPath(meetingCode + ".xml");
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
}
