package com.ayouknowwho.asmrdroid.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.ayouknowwho.asmrdroid.model.AudioRepository;
import com.ayouknowwho.asmrdroid.model.Sample;

public class AudioRepositoryViewModel extends AndroidViewModel {

    private final MutableLiveData<AudioRepository> audioRepository =
            new MutableLiveData(new AudioRepository(this.getApplication()));

    private MutableLiveData<Boolean> updated;

    public String get_default_tag() {
        return DEFAULT_TAG;
    }

    private final String DEFAULT_TAG = "default";

    public AudioRepositoryViewModel(@NonNull Application application) {
        super(application);
        updated = new MutableLiveData(false);
    }

    public String getOpened() {
        return audioRepository.getValue().getOpened();
    }

    public Integer getNum_files() {
        return audioRepository.getValue().getFiles_count();
    }

    public Integer getNum_samples() {
        return audioRepository.getValue().getSamples_count();
    }

    public String getCorrupted() {
        return audioRepository.getValue().getCorrupted();
    }

    public void storeAudioFile(String filename, String tag) {
        audioRepository.getValue().storeAudioFile(filename, tag);
        updated.postValue(true);
    }
    public void storeSample(Sample sample) {
        audioRepository.getValue().storeSample(sample);
        updated.postValue(true);
    }
    public boolean sampleExistsForFileIndex(Integer index) {
        return audioRepository.getValue().sampleExistsForFileIndex(index);
    }

    public String getFilenameFromFileIndex(Integer index) {
        return audioRepository.getValue().getFilenameFromFileIndex(index);
    }

    public Integer getIdFromFileIndex(Integer index) {
        return audioRepository.getValue().getIdFromFileIndex(index);
    }

    public void emptyRepository() {
        audioRepository.getValue().emptyRepository();
        updated.postValue(true);
    }

    public MutableLiveData<Boolean> getUpdated() {
        return updated;
    }

    public String getTagFromFileIndex(Integer fileIndex) {
        return audioRepository.getValue().getTagFromFileIndex(fileIndex);
    }

    public Sample retrieveRandomSampleByTag(String tag) {
        return audioRepository.getValue().retrieveRandomSampleByTag(tag);
    }
}