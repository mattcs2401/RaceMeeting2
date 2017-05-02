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

    public XmlParser() {
        nameSpace = null;
        parser = Xml.newPullParser();
    }

    /**
     * Parse the feed xml into a list of objects that represent the feed elements.
     * @param feed The feed to read (e.g. Meeting or Race).
     * @param in The input stream associated with the feed.
     * @return A list of objects representing the feed elements.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List parse(String feed, InputStream in) throws XmlPullParserException, IOException {
        List list = null;
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            switch(feed) {
                case "Meetings":
                    list = parseForMeetings();
                    break;
                case "weather":
                    // special case, parse just the meeting id and weather info.
                    list = parseForMeetingWeather();
                    break;
                case "Races":
                    // parse the race info, uses same input as 'weather'.
                    list = parseForRaces();
                    break;
            }
            return list;
        } finally {
            in.close();
        }
    }

    private List parseForMeetings() throws XmlPullParserException, IOException {
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

    private List parseForMeetingWeather() throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
        while (parser.next() != XmlPullParser.END_DOCUMENT) { //TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals("Meeting")) {
                entries.add(readMeeting(null));
            } else {
                skip();
            }
        }
        return entries;
    }

    private List parseForRaces() throws XmlPullParserException, IOException {
        // derived from <race_code>.xml
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
        while (parser.next() != XmlPullParser.END_DOCUMENT) { //TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals("Race")) {
                entries.add(readRace());
            } else {
                skip();
            }
        }
        return entries;
    }

    private Meeting readMeeting(@Nullable String date) throws XmlPullParserException, IOException {
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

    private Race readRace() {
        Race race = new Race();
        race.setRaceNumber(parser.getAttributeValue(nameSpace,"RaceNo"));
        race.setRaceTime((parser.getAttributeValue(nameSpace,"RaceTime")).split("T")[1]);
        race.setRaceName(parser.getAttributeValue(nameSpace,"RaceName"));
        race.setRaceDistance(parser.getAttributeValue(nameSpace,"Distance"));
        return race;
    }

    private void skip() throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
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

    private String nameSpace;     // TBA
    private XmlPullParser parser;
}
