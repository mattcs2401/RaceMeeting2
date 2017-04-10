package com.mcssoft.racemeetings2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

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
     * @return A cursor over the result set.
     * Note: Returns all columns.
     */
    public Cursor getSelectionFromTable(String tableName, @Nullable String[] columnNames, String whereClause, String[] whereVals) {
        if(columnNames == null) {
            columnNames = getProjection(tableName);
        }
        SQLiteDatabase db = dbHelper.getDatabase();
        db.beginTransaction();
        Cursor cursor =  db.query(tableName, columnNames, whereClause, whereVals,
                null, null, null);
//        db.setTransactionSuccessful();
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
     * Utility method to see if rows exist in the given table.
     * @param tableName The table to check.
     * @return True if the row count > 0.
     */
    private boolean checkTableRowCount(String tableName) {
        return (getAllFromTable(tableName).getCount() > 0);
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

    private boolean meetingIdExists(int meetingId) {
        boolean retVal = false;
//        Cursor cursor = getSelectionFromTable(SchemaConstants.RACES_TABLE, null,
//                SchemaConstants.WHERE_FOR_GET_RACE_MEETINGID, new String[] { Integer.toString(meetingId)});
//        if(cursor.getCount() > 0) {
//            retVal = true;
//        }
        return retVal;
    }

    private boolean raceIdExists(int raceId) {
        boolean retVal = false;
//        Cursor cursor = getSelectionFromTable(SchemaConstants.RACE_DETAILS_TABLE, null,
//                SchemaConstants.WHERE_FOR_GET_RACE_DETAILS_RACEID, new String[] { Integer.toString(raceId)});
//        if(cursor.getCount() > 0) {
//            retVal = true;
//        }
        return retVal;
    }

    private Context context;
    private DatabaseHelper dbHelper;
}
