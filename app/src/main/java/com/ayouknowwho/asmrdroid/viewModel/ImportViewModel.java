package com.ayouknowwho.asmrdroid.viewModel;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class ImportViewModel extends ViewModel {
    private Uri import_file_uri;

    public ImportViewModel() {
        this.import_file_uri = Uri.parse("default.default.default");
    }

    public Uri getImport_file_uri() {
        return import_file_uri;
    }

    public void setImport_file_uri(Uri import_file_uri) {
        this.import_file_uri = import_file_uri;
    }
}
