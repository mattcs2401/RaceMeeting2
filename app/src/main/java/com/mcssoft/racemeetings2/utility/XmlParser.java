package com.mcssoft.racemeetings2.utility;

import android.support.annotation.Nullable;
import android.util.Xml;

import com.mcssoft.racemeetings2.model.Meeting;
import com.mcssoft.racemeetings2.model.Race;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//https://developer.android.com/training/basics/network-ops/xml.html#skip

public class XmlParser {

    public XmlParser(InputStream inputStream) throws XmlPullParserException, IOException {
        nameSpace = null;
        parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);
        parser.nextTag();
    }

    /**
     * Parse the feed xml into a list of objects that represent the feed elements.
     * @param feed The feed to read (e.g. Meeting or Race).
     * @return A list of objects representing the feed elements.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List parse(String feed) throws XmlPullParserException, IOException {
        List list = null;
        switch(feed) {
            case "Meetings":
                list = parseForMeetings(parser);
                break;
            case "Races":
                list = parseForRaces(parser);
                break;
        }
        return list;
    }

    private List parseForMeetings(XmlPullParser parser) throws XmlPullParserException, IOException {
        // derived from RaceDay.xml
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
        String date = parser.getAttributeValue(nameSpace,"RaceDayDate");
        while (parser.next() != XmlPullParser.END_DOCUMENT) { //TAG) {
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

//    private List parseForMeetingWeather(InputStream in) throws XmlPullParserException, IOException {
//        parser.setInput(in, null);
//        parser.nextTag();
//        List entries = new ArrayList();
//
//        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
//        while (parser.next() != XmlPullParser.END_DOCUMENT) { //TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            if (parser.getName().equals("Meeting")) {
//                entries.add(readMeeting(null));
//            } else {
//                skip();
//            }
//        }
//        return entries;
//    }

    private List parseForRaces(XmlPullParser parser) throws XmlPullParserException, IOException {
        List meetings = new ArrayList();
        List races = new ArrayList();
        List theList = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the Meeting tag
            if (name.equals("Meeting")) {
                meetings.add(readMeeting(parser), null);
            } else if (name.equals("Pool")) {
                // Note: this doesn;t seem to work, i.e. skipp all pool entries.
                skip(parser);
            } else if(name.equals("Race")) {
                races.add(readRace(parser));
                skip(parser);
            } else if (name.equals("Tipster")) {
                // nothig we want after this.
                break;
            } else {
                skip(parser);
            }
        }
        theList.add(meetings);
        theList.add(races);
        return theList;
//        // TODO - seems to work but lot of redundant processing.
//        // derived from <race_code>.xml
//        List entries = new ArrayList();
//        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
//        while (parser.next() != XmlPullParser.END_DOCUMENT) { //TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            parser.require(XmlPullParser.START_TAG, nameSpace, "Meeting");
//            while (parser.next() != XmlPullParser.END_DOCUMENT) {
//                if(parser.getEventType() != XmlPullParser.START_TAG) {
//                    continue;
//                }
//
//                String name = parser.getName();
//                if(parser.getName().equals("Race")) {
//                    entries.add(readRace());
//                    skip();
//                } else {
//                    skip();
//                }
//            }
//        }
//        return entries;
    }
/*
    private List readListing(XmlPullParser parser) throws XmlPullParserException, IOException {
        List meetings = new ArrayList();
        List races = new ArrayList();
        List theList = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "RaceDay");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the Meeting tag
            if (name.equals("Meeting")) {
                meetings.add(readMeeting(parser));
            } else if (name.equals("Pool")) {
                // Note: this doesn;t seem to work, i.e. skipp all pool entries.
                skip(parser);
            } else if(name.equals("Race")) {
                races.add(readRace(parser));
                skip(parser);
            } else if (name.equals("Tipster")) {
                // nothig we want after this.
                break;
            } else {
                skip(parser);
            }
        }
        theList.add(meetings);
        theList.add(races);
        return theList;
    }
 */


    private Meeting readMeeting(XmlPullParser parser, @Nullable String date) throws XmlPullParserException, IOException {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(parser.getAttributeValue(nameSpace, "MtgId"));
        if(date != null) {
            // essentially a new Meeting record.
            meeting.setMeetingDate((date.split("T"))[0]); // format "2017-04-16T00:00:00"
            meeting.setAbandoned(parser.getAttributeValue(nameSpace, "Abandoned"));
            meeting.setVenueName(parser.getAttributeValue(nameSpace, "VenueName"));
            meeting.setHiRaceNo(parser.getAttributeValue(nameSpace, "HiRaceNo"));
            meeting.setMeetingCode(parser.getAttributeValue(nameSpace, "MeetingCode"));
        } else {
            // only weather info.
            meeting.setTrackDescription(parser.getAttributeValue(nameSpace, "TrackDesc"));
            meeting.setTrackRating(parser.getAttributeValue(nameSpace, "TrackRating"));
            meeting.setTrackWeather(parser.getAttributeValue(nameSpace, "WeatherDesc"));
        }
        return meeting;
    }

    private Race readRace(XmlPullParser parser) {
        Race race = new Race();
        race.setRaceNumber(parser.getAttributeValue(nameSpace,"RaceNo"));
        race.setRaceTime((parser.getAttributeValue(nameSpace,"RaceTime")).split("T")[1]);
        race.setRaceName(parser.getAttributeValue(nameSpace,"RaceName"));
        race.setRaceDistance(parser.getAttributeValue(nameSpace,"Distance"));
        return race;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        String name;
        while (depth != 0) {
//            name  = parser.getName(); // testing
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

    private String nameSpace;     // TBA
    private XmlPullParser parser;
}
