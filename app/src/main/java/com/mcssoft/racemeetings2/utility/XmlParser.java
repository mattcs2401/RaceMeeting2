package com.mcssoft.racemeetings2.utility;

import android.util.Xml;

import com.mcssoft.racemeetings2.model.Meeting;

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

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed();
        } finally {
            in.close();
        }
    }

    private List readFeed() throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "RaceDay");
        while (parser.next() != XmlPullParser.END_DOCUMENT) { //TAG) {
            String text = parser.getText();
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // Starts by looking for the Meeting tag.
            String name = parser.getName();           // debugging purposes.
            if (name.equals("Meeting")) {
                entries.add(readMeeting());
            } else {
                skip();
            }
        }
        return entries;
    }

    private Meeting readMeeting() throws XmlPullParserException, IOException {
        Meeting meeting = new Meeting();

        meeting.setMeetingType(parser.getAttributeValue(nameSpace,"MeetingType"));
        meeting.setAbandoned(parser.getAttributeValue(nameSpace,"Abandoned"));
        meeting.setVenueName(parser.getAttributeValue(nameSpace,"VenueName"));
        meeting.setHiRaceNo(parser.getAttributeValue(nameSpace, "HiRaceNo"));
        meeting.setMeetingCode(parser.getAttributeValue(nameSpace,"MeetingCode"));
        meeting.setMeetingId(parser.getAttributeValue(nameSpace, "MtgId"));

        return meeting;
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
