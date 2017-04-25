package com.mcssoft.racemeetings2.model;

/**
 * Class to model a race.
 */
public class Race {

    public String getRaceNumber() {
        return raceNumber;
    }

    public void setRaceNumber(String raceNumber) {
        this.raceNumber = raceNumber;
    }

    public String getRaceTime() {
        return raceTime;
    }

    public void setRaceTime(String raceTime) {
        this.raceTime = raceTime;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getRaceDistance() {
        return raceDistance;
    }

    public void setRaceDistance(String raceDistance) {
        this.raceDistance = raceDistance;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    private String raceNumber;    // e.g. "1"
    private String raceTime;      // e.g. "12:55"
    private String raceName;      // e.g. "BM 60 HANDICAP"
    private String raceDistance;  // e.g. "1905"

    // aditional.
    private String meetingId;     // FK link to MEETINGS table.
}
/*
 Example
 -------

 Using: Using: https://tatts.com/pagedata/racing/2017/3/27/NR.xml

 <Race RaceNo="1"
       RaceTime="2017-03-27T12:55:00"
       RaceName="BM 60 HANDICAP"
       Distance="1905"
       RaceDisplayStatus="SELLING"
       WeatherChanged="N"
       WeatherCond="2"
       WeatherDesc="Overcast"
       TrackChanged="N"
       TrackCond="5"
       TrackDesc="Heavy">
 */
