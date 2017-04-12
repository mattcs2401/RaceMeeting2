package com.mcssoft.racemeetings2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.mcssoft.racemeetings2.model.Meeting;

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
    public boolean checkRecordExists(String tableName, String columnName, String identifier) {
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
        cv.put(SchemaConstants.MEETING_ABANDONED, meeting.getAbandoned());
        cv.put(SchemaConstants.MEETING_VENUE, meeting.getVenueName());
        cv.put(SchemaConstants.MEETING_HI_RACE, meeting.getHiRaceNo());
        cv.put(SchemaConstants.MEETING_CODE, meeting.getMeetingCode());
        cv.put(SchemaConstants.MEETING_ID, meeting.getMeetingId());
//        cv.put(SchemaConstants.MEETING_TRACK_DESC, meeting.getTrackDescription());
//        cv.put(SchemaConstants.MEETING_TRACK_RATING, meeting.getTrackRating());
//        cv.put(SchemaConstants.MEETING_WEATHER_DESC, meeting.getTrackWeather());

        try {
            db.beginTransaction();
            db.insertOrThrow(SchemaConstants.MEETINGS_TABLE, null, cv);
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Utility method to see if rows exist in the given table.
     * @param tableName The table to check.
     * @return True if the row count > 0.
     */
    private boolean checkTableRowCount(String tableName) {
        SQLiteDatabase db = dbHelper.getDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName + ";", null);
        db.endTransaction();
        return (cursor.getCount() > 0);
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

    private Context context;
    private DatabaseHelper dbHelper;
}
