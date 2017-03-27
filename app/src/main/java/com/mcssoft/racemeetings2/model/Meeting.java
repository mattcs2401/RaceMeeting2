package com.mcssoft.racemeetings2.model;

/**
 * Class to model a (race) Meeting.
 */
public class Meeting {

    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getMeetingCode() {
        return meetingCode;
    }

    public void setMeetingCode(String meetingCode) {
        this.meetingCode = meetingCode;
    }

    public boolean isAbandoned() {
        return abandoned;
    }

    public void setAbandoned(boolean abandoned) {
        this.abandoned = abandoned;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getTrackCondition() {
        return trackCondition;
    }

    public void setTrackCondition(String trackCondition) {
        this.trackCondition = trackCondition;
    }

    public int getTrackRating() {
        return trackRating;
    }

    public void setTrackRating(int trackRating) {
        this.trackRating = trackRating;
    }

    private int meetingId;        // e.g. 1228669952
    private String meetingType;   // e.g. "R"
    private String meetingCode;   // e.g. "NR"
    private boolean abandoned;    // e.g. "N" = false etc.
    private String venueName;     // e.g. "Balina"
    private String trackCondition;// e.g. "Heavy"
    private int trackRating;      // e.g. 9
}
/*
 Example
 -------
 Using: https://tatts.com/pagedata/racing/2017/3/27/RaceDay.xml
 <Meeting MeetingType="R"
          Abandoned="N"
          WeatherChanged="N"
          TrackChanged="N"
          VenueName="Ballina"
          SortOrder="0"
          HiRaceNo="7"
          NextRaceNo="1"
          MeetingCode="NR"
          MtgId="1228669952">
*/
/*
 Example
 -------
 Using: https://tatts.com/pagedata/racing/2017/3/27/NR.xml
 <Meeting MeetingCode="NR"
          MtgId="1228669952"
          VenueName="Ballina"
          MtgType="R"
          TrackDesc="Heavy"
          TrackCond="5"
          TrackRatingChanged="N"
          TrackRating="9"
          WeatherChanged="N"
          TrackChanged="N"
          WeatherCond="2"
          WeatherDesc="Overcast"
          MtgAbandoned="N">
 */