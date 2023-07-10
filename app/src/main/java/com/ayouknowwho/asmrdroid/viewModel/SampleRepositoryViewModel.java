package com.ayouknowwho.asmrdroid.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ayouknowwho.asmrdroid.model.SampleRepository;

public class SampleRepositoryViewModel extends ViewModel {
    private final MutableLiveData<SampleRepository> sampleRepository =
            new MutableLiveData(new SampleRepository());

}
