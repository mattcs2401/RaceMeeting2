package com.mcssoft.racemeetings2.utility;

import android.util.Xml;

import com.mcssoft.racemeetings2.model.Meeting;
import com.mcssoft.racemeetings2.model.Race;
import com.mcssoft.racemeetings2.model.Runner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    public XmlParser(InputStream inputStream) throws XmlPullParserException, IOException {
        haveWeather = false;
        nameSpace = null;
        parser = Xml.newPullParser();
        parser.setInput(inputStream, null);
        parser.nextTag();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
    }

    /**
     * Parse the feed xml into a list of objects that represent the feed elements.
     * @param element The part of the feed to read, e.g. Meeting, Race or Runner elements.
     * @return A list of objects representing the feed elements.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List parse(String element) throws XmlPullParserException, IOException {
        List list = null;
        switch(element) {
            case "Meetings":
                list = parseForMeetings();
                break;
            case "Races":
                list = parseForRaces();
                break;
            case "Runners":
                list = parseForRunners();
                break;
        }
        return list;
    }

    //<editor-fold defaultstate="collapsed" desc="Region: Meetings">
    /**
     * Parse the Xml for Meeting information.
     * Note: Based on https://tatts.com/pagedata/racing/YYYY/M(M)/D(D)/RaceDay.xml
     * @return A list of Meeting objects.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List parseForMeetings() throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
        String date = parser.getAttributeValue(nameSpace,"RaceDayDate");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Meeting")) {
                entries.add(readMeeting(date));
                haveWeather = false;
            } else if(name.equals("Race") && !haveWeather) {
                entries = readMeetingWeather(entries);
                haveWeather = true;
            }
            else {
                skip();
            }
        }
        return entries;
    }

    /**
     * Read Meeting infor from the Xml.
     * @param date A derived value from the RaceDayDate attribute of the RaceDay element.
     * @return A Meeting object.
     */
    private Meeting readMeeting(String date) {
        Meeting meeting = new Meeting();
        if(date != null) {
            meeting.setMeetingId(parser.getAttributeValue(nameSpace, "MtgId"));
            // // date format YYYY-MM-DDT00:00:00 (only want date part).
            meeting.setMeetingDate((date.split("T"))[0]);
            meeting.setAbandoned(parser.getAttributeValue(nameSpace, "Abandoned"));
            meeting.setVenueName(parser.getAttributeValue(nameSpace, "VenueName"));
            meeting.setHiRaceNo(parser.getAttributeValue(nameSpace, "HiRaceNo"));
            meeting.setMeetingCode(parser.getAttributeValue(nameSpace, "MeetingCode"));
        }
        return meeting;
    }

    private List readMeetingWeather(List list) {
        // list entry we want is the last one.
        Meeting meeting = (Meeting) list.get(list.size() - 1);

        meeting.setTrackWeather(parser.getAttributeValue(nameSpace, "WeatherDesc"));
        meeting.setTrackRating(parser.getAttributeValue(nameSpace, "TrackRating"));
        meeting.setTrackDescription(parser.getAttributeValue(nameSpace, "TrackDesc"));

        return list;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Races">
    /**
     * Parse the Xml for Race information.
     * Note: Based on https://tatts.com/pagedata/racing/YYYY/M(M)/D(D)/<racecode>.xml
     * @return A list (primarily) of Race objects. [0]-weather (as Meeting), [1]-nn Race objects.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List parseForRaces() throws XmlPullParserException, IOException {
        List theList = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if(name.equals("Race")) {
                theList.add(readRace());
                skip();
            } else if (name.equals("Tipster")) {
                // nothig we want after this (ATT).
                break;
            } else {
                skip();
            }
        }
        return theList;
    }

    /**
     * Read Race info from the Xml.
     * @return A Race object.
     */
    private Race readRace() {
        Race race = new Race();
        race.setRaceNumber(parser.getAttributeValue(nameSpace,"RaceNo"));
        DateTime dateTime = new DateTime();
        race.setRaceTime(dateTime.getTimeComponent(parser.getAttributeValue(nameSpace,"RaceTime")));
        race.setRaceName(parser.getAttributeValue(nameSpace,"RaceName"));
        race.setRaceDistance(parser.getAttributeValue(nameSpace,"Distance") + "m");
        return race;
    }
    ///</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Runners">
    /**
     * Parse the Xml for Runner information.
     * Note: Based on https://tatts.com/pagedata/racing/YYYY/M(M)/D(D)/<racecode><racenum>.xml
     * @return A list of Runner objects.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List parseForRunners() throws XmlPullParserException, IOException {
        List runners = new ArrayList();
        // TODO - parse for Runners
        return runners;
    }

    /**
     * Read Runner info from the Xml.
     * @return A Runner object.
     */
    private Runner readRunner() {
        Runner runner = new Runner();
        // TODO - read Runner.
        return runner;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Region: Utility">
    /**
     * Ignore what we don't want.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip() throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        String name;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    //</editor-fold>

    private String nameSpace;
    private XmlPullParser parser;
    private  boolean haveWeather;      // flag, weather details retieved.
}
