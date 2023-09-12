package com.ayouknowwho.asmrdroid.viewModel;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class GenerateViewModel extends ViewModel {
    private final Integer START_PERCENT = 50;
    private final Integer END_PERCENT = 90;
    private final float MIN_SAMPLE_LENGTH_S = (float) 0.4;
    private final float MAX_SAMPLE_LENGTH_S = 2;
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
}
