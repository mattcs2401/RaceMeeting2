package com.mcssoft.racemeetings2.database;

import android.widget.Toast;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, SchemaConstants.DATABASE_NAME, null, SchemaConstants.DATABASE_VERSION);
        sqLiteDatabase = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        sqLiteDb.beginTransaction();
        try {
            sqLiteDb.execSQL(SchemaConstants.DROP_MEETINGS_TABLE);
            sqLiteDb.execSQL(SchemaConstants.DROP_RACES_TABLE);
            sqLiteDb.execSQL(SchemaConstants.CREATE_MEETINGS_TABLE);
            sqLiteDb.execSQL(SchemaConstants.CREATE_RACES_TABLE);
            sqLiteDb.setTransactionSuccessful();
        } catch(SQLException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDb.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SchemaConstants.DATABASE_NAME + "." + SchemaConstants.MEETINGS_TABLE + ";");
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SchemaConstants.DATABASE_NAME + "." + SchemaConstants.RACES_TABLE + ";");
    }

    public SQLiteDatabase getDatabase() {
        return sqLiteDatabase;
    }

    public enum Projection {
        MeetingSchema, RaceSchema
    }

    public static String [] getProjection(Projection projection) {
        switch (projection) {
            case MeetingSchema:
                return getMeetingsProjection();
            case RaceSchema:
                return getRacesProjection();
        }
        return  null;
    }

    // TBA
    public void onStart() {
        if(sqLiteDatabase == null) {
            sqLiteDatabase = this.getWritableDatabase();
        }
    }

    // TBA
    public void onDestroy() {
        if(sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
        if(context != null) {
            context = null;
        }
    }

    private static String[] getMeetingsProjection() {
        return new String[] {
            SchemaConstants.MEETING_ROWID,
            SchemaConstants.MEETING_ABANDONED,
            SchemaConstants.MEETING_VENUE,
            SchemaConstants.MEETING_HI_RACE,
            SchemaConstants.MEETING_CODE,
            SchemaConstants.MEETING_ID,
            SchemaConstants.MEETING_DATE,
            SchemaConstants.MEETING_TRACK_DESC,
            SchemaConstants.MEETING_TRACK_RATING,
            SchemaConstants.MEETING_WEATHER_DESC};
    }

    private static String[] getRacesProjection() {
        return new String[] {
                SchemaConstants.RACE_ROWID,
                SchemaConstants.RACE_MEETING_ID,
                SchemaConstants.RACE_NO,
                SchemaConstants.RACE_TIME,
                SchemaConstants.RACE_NAME,
                SchemaConstants.RACE_DIST};
    }

    private Context context;
    private SQLiteDatabase sqLiteDatabase;
}
