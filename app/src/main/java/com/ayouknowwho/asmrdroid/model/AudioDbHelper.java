package com.ayouknowwho.asmrdroid.model;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AudioDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    // This is so onUpgrade is called.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "audio.db";

    private static final String SQL_CREATE_AUDIO_FILE_TABLE =
            "CREATE TABLE " + AudioDbContract.AudioFiles.TABLE_NAME + " (" +
                    AudioDbContract.AudioFiles.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    AudioDbContract.AudioFiles.COLUMN_NAME_TAG + " TEXT," +
                    AudioDbContract.AudioFiles.COLUMN_NAME_FILENAME + " TEXT)";

    private static final String SQL_CREATE_SAMPLE_TABLE =
            "CREATE TABLE " + AudioDbContract.Samples.TABLE_NAME + " (" +
                    AudioDbContract.Samples.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID + " INTEGER NOT NULL," +
                    AudioDbContract.Samples.COLUMN_NAME_TAG + " TEXT," +
                    AudioDbContract.Samples.COLUMN_NAME_NUM_CHANNELS + " INTEGER," +
                    AudioDbContract.Samples.COLUMN_NAME_NUM_FRAMES + " TEXT," +
                    AudioDbContract.Samples.COLUMN_NAME_BITS_PER_SAMPLE + " INTEGER," +
                    AudioDbContract.Samples.COLUMN_NAME_SAMPLE_RATE + " TEXT," +
                    AudioDbContract.Samples.COLUMN_NAME_AUDIO_DATA + " BLOB," +
                    "FOREIGN KEY(" + AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID + ") " +
                    "REFERENCES " + AudioDbContract.AudioFiles.TABLE_NAME + "(" +
                    AudioDbContract.AudioFiles.COLUMN_NAME_ID + "))";

    private static final String SQL_DELETE_AUDIO_FILE_TABLE =
            "DROP TABLE IF EXISTS " + AudioDbContract.AudioFiles.TABLE_NAME;

    private static final String SQL_DELETE_SAMPLE_TABLE =
            "DROP TABLE IF EXISTS " + AudioDbContract.Samples.TABLE_NAME;

    public AudioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_AUDIO_FILE_TABLE);
        db.execSQL(SQL_CREATE_SAMPLE_TABLE);
    }
    public void onOpen(SQLiteDatabase db) {

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        emptyDatabase(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void emptyDatabase(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_AUDIO_FILE_TABLE);
        db.execSQL(SQL_DELETE_SAMPLE_TABLE);
        onCreate(db);
    }
}