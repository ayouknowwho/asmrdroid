package com.ayouknowwho.asmrdroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ThreadLocalRandom;

public class AudioRepository {
    private AudioDbHelper audioDbHelper;
    private SQLiteDatabase audioDb;
    private String opened;
    private Integer files_count;
    private Integer samples_count;
    private String corrupted;
    private Context storedContext;
    private final Integer MAX_CURSOR_WINDOW_SIZE = 1000000;

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
        }
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

    public void storeAudioFile(String filename, String tag) {
        ContentValues pairs = new ContentValues();
        pairs.put(AudioDbContract.AudioFiles.COLUMN_NAME_TAG, tag);
        pairs.put(AudioDbContract.AudioFiles.COLUMN_NAME_FILENAME, filename);
        audioDb.insert(AudioDbContract.AudioFiles.TABLE_NAME,null, pairs);
        updateFilesCount();
    }

    public void storeSample(Sample sample) {
        // Convert the Sample object to a byte array
        ByteArrayOutputStream bos;
        ObjectOutputStream oos;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(sample);
        } catch (java.io.IOException e) {
            Log.i("File import","IO error storing sample.");
            return;
        }

        // Store the byte array with the metadata in the database
        ContentValues pairs = new ContentValues();
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_SOURCE_ID, sample.getSource_id());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_TAG, sample.getTag());
        pairs.put(AudioDbContract.Samples.COLUMN_NAME_SAMPLE_OBJECT_DATA, bos.toByteArray());
        audioDb.insert(AudioDbContract.Samples.TABLE_NAME, null, pairs);
        updateSamplesCount();
    }

    public Sample retrieveRandomSampleByTag(String tag) {
        // TODO: The below would need to be changed to get samples_count by tag
        final Integer random_sample_number = ThreadLocalRandom.current().nextInt(0, samples_count);

        // Get the blob as a byte array
        byte[] audio_data = get_sample_byte_array_from_database(random_sample_number);

        // Extract the byte array to a Sample object
        Sample sample = get_sample_from_byte_array(audio_data);
        return sample;
    }

    private Sample get_sample_from_byte_array(byte[] audio_data) {
        Sample sample = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(audio_data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            sample = (Sample) ois.readObject();
        } catch (java.io.IOException e) {
            Log.i("Sample Generation","IO Error while reading Sample from database");
        } catch (ClassNotFoundException e) {
            Log.i("Sample Generation","ClassNotFound Error while reading Sample from database");
        }
        return sample;
    }

    private byte[] get_sample_byte_array_from_database(Integer sample_number) {
        Integer data_length = get_sample_data_length(sample_number);
        Integer blob_offset = 0;
        byte[] audio_data = new byte[data_length];

        while (blob_offset < data_length) {
            Integer sql_substr_offset = blob_offset + 1; // SUBSTR offset in SQL is 1-indexed
            Integer left_to_read = data_length - blob_offset;
            Integer size_of_read = Math.min(left_to_read, MAX_CURSOR_WINDOW_SIZE);

            final String RETRIEVE_SAMPLE_DATA_SQL = "SELECT SUBSTR(" +
                    AudioDbContract.Samples.COLUMN_NAME_SAMPLE_OBJECT_DATA +
                    ", " + sql_substr_offset + ", " + size_of_read +
                    ") FROM " + AudioDbContract.Samples.TABLE_NAME +
                    " LIMIT 1 OFFSET " + sample_number;

            Cursor blob_chunk_cursor = audioDb.rawQuery(RETRIEVE_SAMPLE_DATA_SQL,null);
            blob_chunk_cursor.moveToFirst();
            byte[] blob_chunk = blob_chunk_cursor.getBlob(0);
            blob_chunk_cursor.close();

            System.arraycopy(blob_chunk, 0, audio_data, blob_offset, size_of_read);
            blob_offset += size_of_read;
        }
        return audio_data;
    }

    private Integer get_sample_data_length(Integer sample_number) {
        final String RETRIEVE_SAMPLE_DATA_LENGTH_SQL = "SELECT LENGTH(" + AudioDbContract.Samples.COLUMN_NAME_SAMPLE_OBJECT_DATA +
                ") FROM " + AudioDbContract.Samples.TABLE_NAME +
                " LIMIT 1 OFFSET " + sample_number;
        Cursor sample_data_length_cursor = audioDb.rawQuery(RETRIEVE_SAMPLE_DATA_LENGTH_SQL, null);
        sample_data_length_cursor.moveToFirst();
        Integer data_length = sample_data_length_cursor.getInt(0);
        sample_data_length_cursor.close();
        return data_length;
    }

    public void checkRepository() {
        // Check corruption
        if (audioDb.isDatabaseIntegrityOk()) {
            setCorrupted("Checked for corruption, none found.");
        } else {
            setCorrupted("Checked for corruption, corruption found! Please recreate database.");
            return;
        }

        updateFilesCount();
        updateSamplesCount();
    }

    public void updateFilesCount() {
        setFiles_count(table_length(AudioDbContract.AudioFiles.TABLE_NAME));
    }

    public void updateSamplesCount() {
        setSamples_count(table_length(AudioDbContract.Samples.TABLE_NAME));

    }

    private Integer table_length(String table_name) {
        final String TABLE_LENGTH_SQL = "SELECT COUNT() " +
                "FROM " + table_name;
        Cursor files_count_cursor = audioDb.rawQuery(TABLE_LENGTH_SQL, null);
        files_count_cursor.moveToFirst();
        Integer files_count = Integer.parseInt(files_count_cursor.getString(0));
        files_count_cursor.close();
        return files_count;
    }

    public void emptyRepository() {
        File myDir = new File(storedContext.getFilesDir().getPath());
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (String child : children) {
                new File(myDir, child).delete();
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
                " = " + source_id;
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
