package com.ayouknowwho.asmrdroid.viewModel;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class GenerateViewModel extends ViewModel {
    private Integer num_minutes_to_generate;
    private Uri external_files_dir;

    public GenerateViewModel() {
        this.external_files_dir = Uri.parse("default.default.default");
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
}
