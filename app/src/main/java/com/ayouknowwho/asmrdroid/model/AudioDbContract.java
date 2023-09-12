package com.ayouknowwho.asmrdroid.model;

public final class AudioDbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private void SampleDbContract() {}

    public static class AudioFiles {
        public static final String TABLE_NAME = "audio_files";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_TAG = "tag";
        public static final String COLUMN_NAME_FILENAME = "filename";
    }

    public static class Samples {
        public static final String TABLE_NAME = "samples";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_SOURCE_ID = "source_ID";
        public static final String COLUMN_NAME_TAG = "tag";

        public static final String COLUMN_NAME_NUM_CHANNELS = "num_channels";
        public static final String COLUMN_NAME_NUM_FRAMES = "num_frames";
        public static final String COLUMN_NAME_BITS_PER_SAMPLE = "bits_per_sample";
        public static final String COLUMN_NAME_SAMPLE_RATE = "sample_rate";
        public static final String COLUMN_NAME_AUDIO_DATA = "audio_data";
    }
}
