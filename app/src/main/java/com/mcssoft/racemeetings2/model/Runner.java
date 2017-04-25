package com.mcssoft.racemeetings2.model;

/**
 * Class to model a race runner.
 */
public class Runner {

    public String getRunnerNumber() {
        return runnerNumber;
    }

    public void setRunnerNumber(String runnerNumber) {
        this.runnerNumber = runnerNumber;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public String getRunnerScratched() {
        return runnerScratched;
    }

    public void setRunnerScratched(String runnerScratched) {
        this.runnerScratched = runnerScratched;
    }

    public String getRunnerJockeyName() {
        return runnerJockeyName;
    }

    public void setRunnerJockeyName(String runnerJockeyName) {
        this.runnerJockeyName = runnerJockeyName;
    }

    public String getRunnerBarrier() {
        return runnerBarrier;
    }

    public void setRunnerBarrier(String runnerBarrier) {
        this.runnerBarrier = runnerBarrier;
    }

    public String getRunnerHandicap() {
        return runnerHandicap;
    }

    public void setRunnerHandicap(String runnerHandicap) {
        this.runnerHandicap = runnerHandicap;
    }

    public String getRunnerWeight() {
        return runnerWeight;
    }

    public void setRunnerWeight(String runnerWeight) {
        this.runnerWeight = runnerWeight;
    }

    public String getRunnerForm() {
        return runnerForm;
    }

    public void setRunnerForm(String runnerForm) {
        this.runnerForm = runnerForm;
    }

    public String getRunnerLastResult() {
        return runnerLastResult;
    }

    public void setRunnerLastResult(String runnerLastResult) {
        this.runnerLastResult = runnerLastResult;
    }

    public String getRunnerRating() {
        return runnerRating;
    }

    public void setRunnerRating(String runnerRating) {
        this.runnerRating = runnerRating;
    }

    public String getRaceNumber() {
        return raceNumber;
    }

    public void setRaceNumber(String raceNumber) {
        this.raceNumber = raceNumber;
    }

    public String getRaceMeetingId() {
        return raceMeetingId;
    }

    public void setRaceMeetingId(String raceMeetingId) {
        this.raceMeetingId = raceMeetingId;
    }

    private String runnerNumber;       // e.g. "1"
    private String runnerName;         // e.g. "MY COUSIN JACKIE"
    private String runnerScratched;    // e.g. "N"
    private String runnerJockeyName;   // e.g. "J MURPHY(A)"
    private String runnerBarrier;      // e.g. "3"
    private String runnerHandicap;     // e.g. "0"
    private String runnerWeight;       // e.g. "62.0"
    private String runnerForm;         // e.g. "WC"
    private String runnerLastResult;   // e.g. "801"
    private String runnerRating;       // e.g. "95"

    // additional.
    private String raceNumber;         // FK link to RACES table (no race id value in xml).
    private String raceMeetingId;      // FK link to MEETINGS table.
}
/*
 Example:
 --------
 Using: https://tatts.com/pagedata/racing/2017/3/27/NR1.xml

 <meeting ...
   <race ...
     <Runner RunnerNo="1"
             RunnerName="MY COUSIN JACKIE"
             Scratched="N"
             Rider="J MURPHY(A)"
             RiderChanged="N"
             Barrier="3"
             Handicap="0"
             Weight="62.0"
             Form="WC"
             LastResult="801"
             Rtng="95"
             JockeySilk="57804">
*/