package com.ayouknowwho.asmrdroid.model;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SampleDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    // This is so onUpgrade is called.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "samples.db";

    private static final String SQL_CREATE_TABLES =
            "CREATE TABLE " + SampleDbContract.Samples.TABLE_NAME + " (" +
                    SampleDbContract.Samples.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    SampleDbContract.Samples.COLUMN_NAME_TAG + " TEXT," +
                    SampleDbContract.Samples.COLUMN_NAME_AUDIO_DATA + " BLOB)";

    private static final String SQL_DELETE_TABLES =
            "DROP TABLE IF EXISTS " + SampleDbContract.Samples.TABLE_NAME;


    public SampleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLES);
    }
    public void onOpen(SQLiteDatabase db) {

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The upgrade policy is to discard the old imported data to avoid corruption
        db.execSQL(SQL_DELETE_TABLES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}