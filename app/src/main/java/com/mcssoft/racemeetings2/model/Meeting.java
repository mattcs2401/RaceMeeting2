package com.mcssoft.racemeetings2.model;

/**
 * Class to model a (race) Meeting.
 */
public class Meeting {

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

//    public String getMeetingType() {
//        return meetingType;
//    }

//    public void setMeetingType(String meetingType) {
//        this.meetingType = meetingType;
//    }

    public String getMeetingCode() {
        return meetingCode;
    }

    public void setMeetingCode(String meetingCode) {
        this.meetingCode = meetingCode;
    }

    public String getAbandoned() {
        return abandoned;
    }

    public void setAbandoned(String abandoned) {
        this.abandoned = abandoned;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getTrackDescription() {
        return trackDescription;
    }

    public String getHiRaceNo() {
        return hiRaceNo; }

    public void setHiRaceNo(String hiRaceNo) {
        this.hiRaceNo = hiRaceNo; }

    public void setTrackDescription(String trackDescription) {
        this.trackDescription = trackDescription;
    }

    public String getTrackRating() {
        return trackRating;
    }

    public void setTrackRating(String trackRating) {
        this.trackRating = trackRating;
    }

    public String getTrackWeather() {
        return trackWeather;
    }

    public void setTrackWeather(String trackWeather)
    {
        this.trackWeather = trackWeather;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    // From ...racing/YYYY/MM/DD/RaceDay.xml
    private String abandoned;     // e.g. "N"
    private String venueName;     // e.g. "Balina"
    private String hiRaceNo;      // e.g. "7"
    private String meetingCode;   // e.g. "NR"
    private String meetingId;     // e.g. "1228669952"
    private String meetingDate;   // e.g. "YYYY-M(M)-D(D)"

    // From ...racing/YYYY/MM/DD/<meetingCode>.xml (<meeting></meeting> tag).
    private String trackDescription; // e.g. "Heavy"
    private String trackRating;      // e.g. 9
    private String trackWeather;     // e.g. "Overcast"
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