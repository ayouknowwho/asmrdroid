package com.ayouknowwho.asmrdroid.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ayouknowwho.asmrdroid.model.AudioRepository;

public class AudioRepositoryViewModel extends AndroidViewModel {
    // Using AndroidViewModel gives us access to application context

    public AudioRepositoryViewModel(@NonNull Application application) {
        super(application);
    }

    private final MutableLiveData<AudioRepository> audioRepository =
            new MutableLiveData(new AudioRepository(this.getApplication()));

    public String getOpened() { return audioRepository.getValue().getOpened(); }
    public String getNum_files() { return audioRepository.getValue().getFiles_count(); }

    public String getNum_samples() {
        return audioRepository.getValue().getSamples_count();
    }

    public String getCorrupted() { return audioRepository.getValue().getCorrupted(); }
    public void storeAudioFile(String filename) {
        audioRepository.getValue().storeAudioFile(filename);
    }
    public void emptyRepository() {
        audioRepository.getValue().emptyRepository();
    }
}