package com.ayouknowwho.asmrdroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.MediaStore;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AudioRepository {
    private AudioDbHelper audioDbHelper;
    private SQLiteDatabase audioDb;
    private String opened;
    private Integer files_count;
    private Integer samples_count;
    private String corrupted;
    private Context storedContext;

    public AudioRepository(Context newContext) {
        opened = "Repository not opened.";
        files_count = null;
        samples_count = null;
        corrupted = "Corruption not checked.";
        storedContext = newContext;
        audioDbHelper = new AudioDbHelper(storedContext);
        try {
            audioDb = audioDbHelper.getWritableDatabase();
            setOpened("Repository opened: " + audioDbHelper.getDatabaseName());
        } catch(SQLiteException e) {
            setOpened("Error opening repository.");
        };
        checkRepository();
    }

    // Setters
    private void setOpened(String arg_opened) {
        opened = arg_opened;
    }
    private void setFiles_count(Integer arg_num_files) { files_count = arg_num_files; }

    private void setSamples_count(Integer arg_num_samples) {
        samples_count = arg_num_samples;
    }

    private void setCorrupted(String arg_corrupted) {
        corrupted = arg_corrupted;
    }

    // Getters
    public String getOpened() {
        return opened;
    }
    public Integer getFiles_count() { return files_count; }

    public Integer getSamples_count() {
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

    public void storeSample(Sample sample) {
        ContentValues pairs = new ContentValues();
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID, sample.getSource_id());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_TAG, "default");
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_AUDIO_DATA, sample.getAudio_data());
        audioDb.insert(AudioDbContract.Samples.TABLE_NAME, null, pairs);
    }

    private Sample retrieveSampleByTag(String tag) {
        // TODO: The below would need to be changed to get samples_count by tag
        final Integer random_sample_number = ThreadLocalRandom.current().nextInt(0, samples_count);

        // TODO: The below would need to be changed to only select samples with the correct tag
        final String RETRIEVE_SAMPLE_SQL = "SELECT FROM " + AudioDbContract.Samples.TABLE_NAME +
                " LIMIT 1 OFFSET " + random_sample_number.toString();

        Cursor sample_cursor = audioDb.rawQuery(RETRIEVE_SAMPLE_SQL, null);
        sample_cursor.moveToFirst();
        Integer id_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_ID);
        Integer id = sample_cursor.getInt(id_index);
        Integer source_id_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID);
        Integer source_id = sample_cursor.getInt(source_id_index);
        Integer audio_data_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_AUDIO_DATA);
        byte[] audio_data = sample_cursor.getBlob(audio_data_index);

        Sample sample = new Sample(id, tag, source_id, audio_data);
        return sample;
    }

    public Sample[] retrieveSamplesByTag(String tag, int quantity) {
        Sample[] samples = new Sample[quantity];
        for (int i = 0; i < quantity; i++) {
            samples[i] = retrieveSampleByTag(tag);
        }
        return samples;
    }

    public void checkRepository() {
        final String FILES_COUNT_SQL = "SELECT COUNT() " +
                "FROM " + AudioDbContract.AudioFiles.TABLE_NAME;
        Cursor files_count = audioDb.rawQuery(FILES_COUNT_SQL, null);
        files_count.moveToFirst();
        setFiles_count(Integer.parseInt(files_count.getString(0)));
        files_count.close();

        final String SAMPLES_COUNT_SQL = "SELECT COUNT() " +
                "FROM " + AudioDbContract.Samples.TABLE_NAME;
        Cursor samples_count = audioDb.rawQuery(SAMPLES_COUNT_SQL, null);
        samples_count.moveToFirst();
        setSamples_count(Integer.parseInt(samples_count.getString(0)));
        samples_count.close();

        if (audioDb.isDatabaseIntegrityOk()) {
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
