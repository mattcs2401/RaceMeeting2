package com.mcssoft.racemeetings2.database;

public class SchemaConstants {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RACEDAY";

    public static final String MEETINGS_TABLE = "MEETINGS";
    public static final String RACES_TABLE = "RACES";

    // Columns for MEETINGS table.
    // From RaceDay.xml
    public static final String MEETING_ROWID = "_id";
    public static final String MEETING_ABANDONED = "ABANDONED";           // e.g. "N"
    public static final String MEETING_VENUE = "VENUE_NAME";              // e.g. "Goulburn"
    public static final String MEETING_HI_RACE = "HI_RACE";               // e.g "7"
    public static final String MEETING_CODE = "MEETING_CODE";             // e.g. "NR"
    public static final String MEETING_ID = "MEETING_ID";                 // e.g. "1224999936"
    // Derived from <RaceDay RaceDayDate=.../>
    public static final String MEETING_DATE = "MEETING_DATE";             // e.g. "YYYY-M(M)-D(D)"
    // From <meeting_code>.xml
    public static final String MEETING_TRACK_DESC = "TRACK_DESC";         // e.g. "Soft"
    public static final String MEETING_TRACK_RATING = "TRACK_RATING";     // e.g. "5"
    public static final String MEETING_WEATHER_DESC = "WEATHER_DESC";     // e.g. "Overcast"

    // Columns RACES table;
    // From <meeting_code>.xml
    public static final String RACE_ROWID = "_id";
    public static final String RACE_MEETING_ID = "MEETING_ID";       // from above.
    public static final String RACE_NO = "RACE_NO";                  // e.g. "1"
    public static final String RACE_TIME = "RACE_TIME";              // e.g. "2017-04-10T13:35:00"
    public static final String RACE_NAME = "RACE_NAME";              // e.g. "MAIDEN PLATE"
    public static final String RACE_DIST = "RACE_DIST";              // e.g. "1500"

    // MEETINGS table create.
    public static final String CREATE_MEETINGS_TABLE = "CREATE TABLE "
            + MEETINGS_TABLE       + " ("
            + MEETING_ROWID        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MEETING_ABANDONED    + " TEXT NOT NULL, "
            + MEETING_VENUE        + " TEXT NOT NULL, "
            + MEETING_HI_RACE      + " TEXT NOT NULL, "
            + MEETING_CODE         + " TEXT NOT NULL, "
            + MEETING_ID           + " TEXT NOT NULL, "
            + MEETING_DATE         + " TEXT NOT NULL, "
            + MEETING_TRACK_DESC   + " TEXT, "
            + MEETING_TRACK_RATING + " TEXT, "
            + MEETING_WEATHER_DESC + " TEXT)";

    // RACES table create.
    public static final String CREATE_RACES_TABLE = "CREATE TABLE "
            + RACES_TABLE     + " ("
            + RACE_ROWID      + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RACE_MEETING_ID + " TEXT NOT NULL, "
            + RACE_NO         + " TEXT NOT NULL, "
            + RACE_TIME       + " TEXT NOT NULL, "
            + RACE_NAME       + " TEXT NOT NULL, "
            + RACE_DIST       + " TEXT NOT NULL)";

    public  static final String DROP_MEETINGS_TABLE = "DROP TABLE IF EXISTS " + DATABASE_NAME + "." + MEETINGS_TABLE + ";";
    public  static final String DROP_RACES_TABLE = "DROP TABLE IF EXISTS " + DATABASE_NAME + "." + RACES_TABLE + ";";

    public static final String WHERE_MEETING_ID = MEETING_ID + "=?";
    public static final String WHERE_RACE_MEETING_ID = RACE_MEETING_ID + "=?";

}
