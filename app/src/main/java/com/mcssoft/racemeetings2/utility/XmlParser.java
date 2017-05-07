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
            if (parser.getName().equals("Meeting")) {
                entries.add(readMeeting(date));
            } else {
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

    /**
     * Read Meeting weather related info.
     * @return A list of (primarily) weather related info.
     *         [0]-meeting id, [1]-track desc, [2]-track rating, [3]-weather desc.
     *                       e.g.  Soft            7                 Fine
     */
    private List readMeetingWeather() {
        List list = new ArrayList();
        list.add(parser.getAttributeValue(nameSpace, "MtgId"));
        list.add(parser.getAttributeValue(nameSpace, "TrackDesc"));
        list.add(parser.getAttributeValue(nameSpace, "TrackRating"));
        list.add(parser.getAttributeValue(nameSpace, "WeatherDesc"));
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
            if (name.equals("Meeting")) {
                // in this case we're only after weather information.
                theList.add(readMeetingWeather());
//            } else if (name.equals("Pool")) {
//                // Note: this doesn;t seem to work, i.e. skipp all pool entries.
//                skip();
            } else if(name.equals("Race")) {
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
        // race time in "YYYY-MM-DDTHH:MM:SS" format (only want time).
        race.setRaceTime((parser.getAttributeValue(nameSpace,"RaceTime")).split("T")[1]);
        race.setRaceName(parser.getAttributeValue(nameSpace,"RaceName"));
        race.setRaceDistance(parser.getAttributeValue(nameSpace,"Distance"));
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

    private String nameSpace;
    private XmlPullParser parser;
}
