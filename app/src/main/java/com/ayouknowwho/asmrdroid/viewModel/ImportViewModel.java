package com.ayouknowwho.asmrdroid.viewModel;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImportViewModel extends ViewModel {
    private final MutableLiveData<Uri> import_file_uri;

    public ImportViewModel() {
        this.import_file_uri = new MutableLiveData(Uri.parse("default.default.default"));
    }

    public MutableLiveData<Uri> getImport_file_live_uri() {
        return import_file_uri;
    }

    public void setImport_file_uri(Uri import_file_uri) {
        this.import_file_uri.postValue(import_file_uri);
    }
}
