package com.ayouknowwho.asmrdroid.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ayouknowwho.asmrdroid.model.SampleRepository;

public class SampleRepositoryViewModel extends ViewModel {
    private Context context;
    public SampleRepositoryViewModel(Context context) {
        this.context = context;
    }
    private final MutableLiveData<SampleRepository> sampleRepository =
            new MutableLiveData(new SampleRepository(context));

}
