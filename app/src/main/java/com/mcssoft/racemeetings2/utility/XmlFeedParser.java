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

public class XmlFeedParser {

    public XmlFeedParser() {
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
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the Meeting tag.
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
        parser.require(XmlPullParser.START_TAG, nameSpace, "Meeting");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String elementName = parser.getName();
            if(elementName.equals("Meeting")) {
                meeting.setMeetingType(parser.getAttributeValue(nameSpace,"MeetingType"));
                meeting.setVenueName(parser.getAttributeValue(nameSpace,"VenuName"));
                meeting.setMeetingCode(parser.getAttributeValue(nameSpace,"MeetingCode"));
                meeting.setMeetingId(Integer.parseInt(parser.getAttributeValue(nameSpace, "MtgId")));
            }

        }
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

    private String nameSpace = null;        // TBA
    private XmlPullParser parser = null;
}
