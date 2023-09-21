package com.ayouknowwho.asmrdroid.viewModel;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class GenerateViewModel extends ViewModel {
    private final Integer START_PERCENT = 50;
    private final Integer END_PERCENT = 90;
    private final float MIN_SAMPLE_LENGTH_S = (float) 0.4;
    private final float MAX_SAMPLE_LENGTH_S = 1;

    public String getTargetFileType() {
        return TARGET_FILE_TYPE;
    }

    private final String TARGET_FILE_TYPE = "wav";
    private final Integer TARGET_NUM_CHANNELS = 2;
    private final long TARGET_SAMPLE_RATE = 44100;
    private final Integer TARGET_BITS_PER_SAMPLE = 24;
    private Integer num_minutes_to_generate;
    private Uri external_files_dir;

    public GenerateViewModel() {

        this.external_files_dir = Uri.parse("default.default.default");
        this.num_minutes_to_generate = 10;
    }

    public Integer getNum_minutes_to_generate() {
        return num_minutes_to_generate;
    }

    public void setNum_minutes_to_generate(Integer num_minutes_to_generate) {
        this.num_minutes_to_generate = num_minutes_to_generate;
    }

    public Uri getExternal_files_dir() {
        return external_files_dir;
    }

    public void setExternal_files_dir(Uri external_files_dir) {
        this.external_files_dir = external_files_dir;
    }

    public Integer getStartPercent() {
        return START_PERCENT;
    }

    public Integer getEndPercent() {
        return END_PERCENT;
    }

    public float getMinSampleLengthS() {
        return MIN_SAMPLE_LENGTH_S;
    }

    public float getMaxSampleLengthS() {
        return MAX_SAMPLE_LENGTH_S;
    }

    public Integer getTargetNumChannels() {
        return TARGET_NUM_CHANNELS;
    }

    public long getTargetSampleRate() {
        return TARGET_SAMPLE_RATE;
    }

    public Integer getTargetBitsPerSample() {
        return TARGET_BITS_PER_SAMPLE;
    }
}
