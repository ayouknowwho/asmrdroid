package com.ayouknowwho.asmrdroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;

public class AudioRepository {
    private AudioDbHelper audioDbHelper;
    private SQLiteDatabase audioDb;
    private String opened;
    private String files_count;
    private String samples_count;
    private String corrupted;
    private Context storedContext;

    public AudioRepository(Context newContext) {
        opened = "Repository not opened.";
        samples_count = "Samples not counted.";
        files_count = "Files not counted.";
        corrupted = "Corruption not checked.";
        storedContext = newContext;
        audioDbHelper = new AudioDbHelper(storedContext);
        try {
            audioDb = audioDbHelper.getWritableDatabase();
            setOpened("Repository opened: " + audioDbHelper.getDatabaseName());
        } catch(SQLiteException e) {
            setOpened("Error opening repository.");
        };
        checkRepository(audioDb);
    }

    // Setters
    private void setOpened(String arg_opened) {
        opened = arg_opened;
    }
    private void setFiles_count(String arg_num_files) { files_count = arg_num_files; }

    private void setSamples_count(String arg_num_samples) {
        samples_count = arg_num_samples;
    }

    private void setCorrupted(String arg_corrupted) {
        corrupted = arg_corrupted;
    }

    // Getters
    public String getOpened() {
        return opened;
    }
    public String getFiles_count() { return files_count; }

    public String getSamples_count() {
        return samples_count;
    }

    public String getCorrupted() {
        return corrupted;
    }

    public void storeAudioFile(String filename) {
        ContentValues pairs = new ContentValues();
        pairs.put(AudioDbContract.AudioFiles.COLUMN_NAME_TAG, "default");
        pairs.put(AudioDbContract.AudioFiles.COLUMN_NAME_FILENAME, "filename");
        audioDb.insert(AudioDbContract.AudioFiles.TABLE_NAME,null, pairs);
    }

    public int storeSample(Sample sample) {
        // TODO: store sample object to database
        return 0;
    }

    private Sample retrieveSample(String tag) {
        Sample sample = null;
        // TODO: get a sample with the given tag
        return sample;
    }

    public Sample[] retrieveSamples(String tag, int quantity) {
        Sample[] samples = new Sample[quantity];
        for (int i = 0; i < quantity; i++) {
            samples[i] = retrieveSample(tag);
        }
        return samples;
    }

    public void checkRepository(SQLiteDatabase db) {
        final String FILES_COUNT_SQL = "SELECT COUNT() " +
                "FROM " + AudioDbContract.AudioFiles.TABLE_NAME;
        Cursor files_count = db.rawQuery(FILES_COUNT_SQL, null);
        files_count.moveToFirst();
        setFiles_count(files_count.getString(0) + " files in repository.");
        files_count.close();

        final String SAMPLES_COUNT_SQL = "SELECT COUNT() " +
                "FROM " + AudioDbContract.Samples.TABLE_NAME;
        Cursor samples_count = db.rawQuery(SAMPLES_COUNT_SQL, null);
        samples_count.moveToFirst();
        setSamples_count(samples_count.getString(0) + " samples in repository");
        samples_count.close();

        if (db.isDatabaseIntegrityOk()) {
            setCorrupted("Checked for corruption, none found.");
        } else {
            setCorrupted("Checked for corruption, corruption found! Please recreate database.");
        }

        return;
    }

    public void emptyRepository() {
        File myDir = new File(storedContext.getFilesDir().getPath());
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
        audioDbHelper.emptyDatabase(audioDb);
    }
}
