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
        pairs.put(AudioDbContract.AudioFiles.COLUMN_NAME_FILENAME, filename);
        audioDb.insert(AudioDbContract.AudioFiles.TABLE_NAME,null, pairs);
        checkRepository();
    }

    public void storeSample(Sample sample) {
        ContentValues pairs = new ContentValues();
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID, sample.getSource_id());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_TAG, "default");
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_AUDIO_DATA, sample.getAudio_data());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_NUM_CHANNELS, sample.getNum_channels());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_NUM_FRAMES, sample.getNum_frames());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_BITS_PER_SAMPLE, sample.getBits_per_sample());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_SAMPLE_RATE, sample.getSample_rate());
        audioDb.insert(AudioDbContract.Samples.TABLE_NAME, null, pairs);
        checkRepository();
    }

    private Sample retrieveSampleByTag(String tag) {
        // TODO: The below would need to be changed to get samples_count by tag
        final Integer random_sample_number = ThreadLocalRandom.current().nextInt(0, samples_count);

        // TODO: The below would need to be changed to only select samples with the correct tag
        final String RETRIEVE_SAMPLE_SQL = "SELECT FROM " + AudioDbContract.Samples.TABLE_NAME +
                " LIMIT 1 OFFSET " + random_sample_number.toString();

        Cursor sample_cursor = audioDb.rawQuery(RETRIEVE_SAMPLE_SQL, null);
        sample_cursor.moveToFirst();

        Integer source_id_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID);
        Integer source_id = sample_cursor.getInt(source_id_index);
        Integer num_channels_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_NUM_CHANNELS);
        Integer num_channels = sample_cursor.getInt(num_channels_index);
        Integer num_frames_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_NUM_FRAMES);
        long num_frames = Long.parseLong(sample_cursor.getString(num_frames_index));
        Integer bits_per_sample_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_BITS_PER_SAMPLE);
        Integer bits_per_sample = sample_cursor.getInt(bits_per_sample_index);
        Integer audio_data_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_AUDIO_DATA);
        Integer sample_rate_index = sample_cursor.getColumnIndex(AudioDbContract.Samples.COLUMN_NAME_SAMPLE_RATE);
        long sample_rate = Long.parseLong(sample_cursor.getString(sample_rate_index));
        byte[] audio_data = sample_cursor.getBlob(audio_data_index);

        sample_cursor.close();

        Sample sample = new Sample(source_id, tag, num_channels, num_frames, bits_per_sample, sample_rate, audio_data);
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
        // Check corruption
        if (audioDb.isDatabaseIntegrityOk()) {
            setCorrupted("Checked for corruption, none found.");
        } else {
            setCorrupted("Checked for corruption, corruption found! Please recreate database.");
            return;
        }

        // Count files
        final String FILES_COUNT_SQL = "SELECT COUNT() " +
                "FROM " + AudioDbContract.AudioFiles.TABLE_NAME;
        Cursor files_count = audioDb.rawQuery(FILES_COUNT_SQL, null);
        files_count.moveToFirst();
        setFiles_count(Integer.parseInt(files_count.getString(0)));
        files_count.close();

        // Count samples
        final String SAMPLES_COUNT_SQL = "SELECT COUNT() " +
                "FROM " + AudioDbContract.Samples.TABLE_NAME;
        Cursor samples_count = audioDb.rawQuery(SAMPLES_COUNT_SQL, null);
        samples_count.moveToFirst();
        setSamples_count(Integer.parseInt(samples_count.getString(0)));
        samples_count.close();

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
        checkRepository();
    }

    public boolean sampleExistsForFileIndex(Integer index) {
        // Get source_id from file index
        final String GET_SOURCE_ID_FROM_INDEX_SQL = "SELECT * FROM " + AudioDbContract.AudioFiles.TABLE_NAME +
                " LIMIT 1 OFFSET " + index.toString();
        Cursor source_cursor = audioDb.rawQuery(GET_SOURCE_ID_FROM_INDEX_SQL, null);
        source_cursor.moveToFirst();
        Integer source_id_column_index = source_cursor.getColumnIndex(AudioDbContract.AudioFiles.COLUMN_NAME_ID);
        Integer source_id = source_cursor.getInt(source_id_column_index);
        source_cursor.close();

        // Check for any sample with matching source_id
        final String SAMPLE_CHECK_FROM_SOURCE_ID_SQL = "SELECT * FROM " + AudioDbContract.Samples.TABLE_NAME +
                " WHERE " + AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID +
                " = " + source_id.toString();
        Cursor sample_cursor = audioDb.rawQuery(SAMPLE_CHECK_FROM_SOURCE_ID_SQL, null);
        if (sample_cursor.getCount() == 0) {
            sample_cursor.close();
            return false;
        }
        else {
            sample_cursor.close();
            return true;
        }
    }

    public String getFilenameFromFileIndex(Integer index) {
        final String GET_FILENAME_FROM_INDEX_SQL = "SELECT * FROM " + AudioDbContract.AudioFiles.TABLE_NAME +
                " LIMIT 1 OFFSET " + index.toString();
        Cursor filename_cursor = audioDb.rawQuery(GET_FILENAME_FROM_INDEX_SQL, null);
        filename_cursor.moveToFirst();
        Integer filename_column_index = filename_cursor.getColumnIndex(AudioDbContract.AudioFiles.COLUMN_NAME_FILENAME);
        String filename = filename_cursor.getString(filename_column_index);
        filename_cursor.close();
        return filename;
    }

    public Integer getIdFromFileIndex(Integer index) {
        final String GET_ID_FROM_INDEX_SQL = "SELECT * FROM " + AudioDbContract.AudioFiles.TABLE_NAME +
                " LIMIT 1 OFFSET " + index.toString();
        Cursor id_cursor = audioDb.rawQuery(GET_ID_FROM_INDEX_SQL, null);
        id_cursor.moveToFirst();
        Integer id_column_index = id_cursor.getColumnIndex(AudioDbContract.AudioFiles.COLUMN_NAME_ID);
        Integer id = id_cursor.getInt(id_column_index);
        id_cursor.close();
        return id;
    }

    public String getTagFromFileIndex(Integer index) {
        final String GET_TAG_FROM_INDEX_SQL = "SELECT * FROM " + AudioDbContract.AudioFiles.TABLE_NAME +
                " LIMIT 1 OFFSET " + index.toString();
        Cursor tag_cursor = audioDb.rawQuery(GET_TAG_FROM_INDEX_SQL, null);
        tag_cursor.moveToFirst();
        Integer tag_column_index = tag_cursor.getColumnIndex(AudioDbContract.AudioFiles.COLUMN_NAME_TAG);
        String tag = tag_cursor.getString(tag_column_index);
        tag_cursor.close();
        return tag;
    }
}
