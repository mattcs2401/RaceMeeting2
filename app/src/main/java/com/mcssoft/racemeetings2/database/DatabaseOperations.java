package com.mcssoft.racemeetings2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mcssoft.racemeetings2.model.Meeting;
import com.mcssoft.racemeetings2.model.Race;
import com.mcssoft.racemeetings2.model.Runner;

/**
 * Utility class to perform database activities / actions.
 */
public class DatabaseOperations {

    public DatabaseOperations(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Get all the records in a table.
     * @param tableName The table name.
     * @return A cursor over the records.
     *         Note: No guarantee the cursor contains anything.
     */
    public Cursor getAllFromTable(String tableName) {
        SQLiteDatabase db = dbHelper.getDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(tableName, getProjection(tableName), null, null, null, null, null);
        db.endTransaction();
        return cursor;
    }

    /**
     * Delete all the records in a table.
     * @param tableName The table name.
     * @return The number of rows deleted.
     */
    public int deleteAllFromTable(String tableName) {
        int rows = 0;
        if(checkTableRowCount(tableName)) {
            SQLiteDatabase db = dbHelper.getDatabase();
            db.beginTransaction();
            rows = db.delete(tableName, "1", null);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return rows;
    }

    /**
     * Utility wrapper method to query the database.
     * @param tableName The table name.
     * @param columnNames The table columns required (Null equals all columns).
     * @param whereClause Where clause (without the "where").
     * @param whereVals Where clause values
     * @return A cursor over the parseResult set.
     */
    public Cursor getSelectionFromTable(String tableName, @Nullable String[] columnNames, String whereClause, String[] whereVals) {
        if(columnNames == null) {
            columnNames = getProjection(tableName);
        }
        SQLiteDatabase db = dbHelper.getDatabase();
        db.beginTransaction();
        Cursor cursor =  db.query(tableName, columnNames, whereClause, whereVals, null, null, null);
        db.endTransaction();
        return cursor;
    }

    /**
     * Utility method to update a single value in a single row.
     * @param tableName The table name.
     * @param where The where clause (without the "where").
     * @param rowId The table row id.
     * @param colName The table column name.
     * @param value The column value.
     * @return The update count.
     */
    public int updateTableByRowId(String tableName, String where, int rowId, String colName, String value) {
        SQLiteDatabase db = dbHelper.getDatabase();

        ContentValues cv = new ContentValues();
        cv.put(colName, value);

        db.beginTransaction();
        int counr = db.update(tableName, cv, where, new String[] {Integer.toString(rowId)});
        db.setTransactionSuccessful();
        db.endTransaction();

        return counr;
    }

    /**
     * Utility to create the '?' parameter part of an IN statement.
     * @param iterations The number of '?' characters to insert.
     * @return The formatted string e.g. " IN (?,?)".
     */
    public String createWhereIN(int iterations) {
        StringBuilder sb = new StringBuilder();
        sb.append(" IN (");
        for(int ndx = 0; ndx < iterations; ndx++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);   // remove last comma.
        sb.append(")");
        return sb.toString();
    }

    /**
     * Check a record exists using the given record identifier.
     * @param tableName  The record's associated table.
     * @param columnName The record's column to check.
     * @param identifier The identifier in the column.
     * @return True if record exists.
     */
    public boolean checkRecordsExist(String tableName, String columnName, String identifier) {
        Cursor cursor = null;
        String[] col = new String[] {columnName};
        String[] id = new String[] {identifier};
        switch(tableName) {
            case SchemaConstants.MEETINGS_TABLE:
                cursor = getSelectionFromTable(tableName, col, SchemaConstants.WHERE_MEETING_ID, id);
                break;
            case SchemaConstants.RACES_TABLE:
                cursor = getSelectionFromTable(tableName, col, SchemaConstants.WHERE_RACE_MEETING_ID, id);
                break;
            case SchemaConstants.RUNNERS_TABLE:
                // TODO - where clause for select on RUNNERS table.
                break;
        }
        return ((cursor != null) && (cursor.getCount() > 0));
    }

    /**
     * Insert a record into the MEETINGS table.
     * @param meeting Meeting object to derive values from.
     */
    public void insertMeetingRecord(Meeting meeting) {
        SQLiteDatabase db = dbHelper.getDatabase();
        ContentValues cv = new ContentValues();

        // Note: derived from RaceDay.xml.
        cv.put(SchemaConstants.MEETING_DATE, meeting.getMeetingDate());
        cv.put(SchemaConstants.MEETING_ABANDONED, meeting.getAbandoned());
        cv.put(SchemaConstants.MEETING_VENUE, meeting.getVenueName());
        cv.put(SchemaConstants.MEETING_HI_RACE, meeting.getHiRaceNo());
        cv.put(SchemaConstants.MEETING_CODE, meeting.getMeetingCode());
        cv.put(SchemaConstants.MEETING_ID, meeting.getMeetingId());

        try {
            db.beginTransaction();
            db.insertOrThrow(SchemaConstants.MEETINGS_TABLE, null, cv);
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            Log.d(context.getClass().getCanonicalName(), ex.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void updateMeetingRecordWeather(Meeting meeting) {
        SQLiteDatabase db = dbHelper.getDatabase();
        ContentValues cv = new ContentValues();
        String meetingId = meeting.getMeetingId();

        cv.put(SchemaConstants.MEETING_TRACK_DESC, meeting.getTrackDescription());
        cv.put(SchemaConstants.MEETING_TRACK_RATING, meeting.getTrackRating());
        cv.put(SchemaConstants.MEETING_WEATHER_DESC, meeting.getTrackWeather());

        try {
            db.beginTransaction();
            db.update(SchemaConstants.MEETINGS_TABLE, cv, SchemaConstants.WHERE_MEETING_ID,
                    new String[] {meetingId});
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            Log.d(context.getClass().getCanonicalName(), ex.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Insert a record into the RACES table.
     * @param race Race object to derive values from.
     */
    public void insertRaceRecord(Race race) {
        SQLiteDatabase db = dbHelper.getDatabase();
        ContentValues cv = new ContentValues();

        cv.put(SchemaConstants.RACE_MEETING_ID, race.getMeetingId());
        cv.put(SchemaConstants.RACE_NO, race.getRaceNumber());
        cv.put(SchemaConstants.RACE_TIME, race.getRaceTime());
        cv.put(SchemaConstants.RACE_NAME, race.getRaceName());
        cv.put(SchemaConstants.RACE_DIST, race.getRaceDistance());

        try {
            db.beginTransaction();
            db.insertOrThrow(SchemaConstants.RACES_TABLE, null, cv);
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            Log.d(context.getClass().getCanonicalName(), ex.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Utility method to see if rows exist in the given table.
     * @param tableName The table to check.
     * @return True if the row count > 0.
     */
    public boolean checkTableRowCount(String tableName) {
        String rowId = getRowIdName(tableName);
        SQLiteDatabase db = dbHelper.getDatabase();
        db.beginTransaction();
        Cursor cursor = getSelectionFromTable(tableName, new String[] {rowId}, null, null);
        db.endTransaction();
        return (cursor.getCount() > 0);
    }

    /**
     * Utility method to check if rows exist in the Meetings table for the given date.
     * @param date The date to check (formatted YYYY-M(M)-D(D)).
     * @return True if records exist.
     */
    public boolean checkMeetingDate(String date) {
        SQLiteDatabase db = dbHelper.getDatabase();
        db.beginTransaction();
        Cursor cursor = getSelectionFromTable(SchemaConstants.MEETINGS_TABLE,
                new String[] {SchemaConstants.MEETING_ROWID}, SchemaConstants.WHERE_MEETING_DATE,
                new String[] {date});
        db.endTransaction();
        return (cursor.getCount() > 0);
    }

    /**
     * Insert a record into the RUNNERS table.
     * @param runner Runner object to derive values from.
     */
    public void insertRunnerRecord(Runner runner) {
        SQLiteDatabase db = dbHelper.getDatabase();
        ContentValues cv = new ContentValues();

        // TODO - ContentValues for insert of RUNNER record.

        try {
            db.beginTransaction();
            db.insertOrThrow(SchemaConstants.RUNNERS_TABLE, null, cv);
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            Log.d(context.getClass().getCanonicalName(), ex.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Get the associated DatabseHelper object.
     * @return The DatabaseHelper.
     */
    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    private String[] getProjection(String tableName) {
        String[] projection = {};
        switch (tableName) {
            case SchemaConstants.MEETINGS_TABLE:
                projection = dbHelper.getProjection(DatabaseHelper.Projection.MeetingSchema);
                break;
            case SchemaConstants.RACES_TABLE:
                projection = dbHelper.getProjection(DatabaseHelper.Projection.RaceSchema);
                break;
        }
        return  projection;
    }

    /**
     * get the name of the applicable row id column.
     * @param tableName The table name.
     * @return The row id column name.
     */
    private String getRowIdName(String tableName) {
        String rowId = "";
        switch(tableName) {
            case SchemaConstants.MEETINGS_TABLE:
                rowId = SchemaConstants.MEETING_ROWID;
                break;
            case SchemaConstants.RACES_TABLE:
                rowId = SchemaConstants.RACE_ROWID;
                break;
            case SchemaConstants.RUNNERS_TABLE:
                rowId = SchemaConstants.RUNNER_ROWID;
                break;
        }
        return rowId;
    }

    private Context context;
    private DatabaseHelper dbHelper;
}
