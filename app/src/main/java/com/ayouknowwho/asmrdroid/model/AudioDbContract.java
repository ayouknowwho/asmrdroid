package com.ayouknowwho.asmrdroid.model;

public final class AudioDbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private void SampleDbContract() {}

    /* Inner class that defines the table contents */
    public static class Samples {
        public static final String TABLE_NAME = "samples";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_TAG = "tag";
        public static final String COLUMN_NAME_AUDIO_DATA = "audio_data";
    }

    public static class AudioFiles {
        public static final String TABLE_NAME = "audio_files";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_TAG = "tag";
        public static final String COLUMN_NAME_FILENAME = "filename";
    }

}
