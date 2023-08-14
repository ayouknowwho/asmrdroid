package com.ayouknowwho.asmrdroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ayouknowwho.asmrdroid.viewModel.SampleRepositoryViewModel;
import com.ayouknowwho.asmrdroid.viewModel.MainViewModel;
import com.google.android.material.tabs.TabLayout;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements StartFilePicker{

    final static int PICK_AUDIO_FILE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up view model
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set up sample database view model
        /* SampleRepositoryViewModel sampleRepositoryViewModel = new ViewModelProvider.of(this, getContext()).get(SampleRepositoryViewModel.class); */

        // UI Creation
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        FragmentContainerView fragmentContainerView = (FragmentContainerView) findViewById(R.id.fragmentContainerView);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, HomeFragment.class, null)
                            .commit();
                }
                else if (tab.getPosition() == 1) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, ImportFragment.class, null)
                            .commit();
                }
                else if (tab.getPosition() == 2) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, GenerateFragment.class, null)
                            .commit();
                }
                else {
                    /* We shouldn't be able to get here */
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, HomeFragment.class, null)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }


    public void startFilePick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_FILE_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_AUDIO_FILE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
                mainViewModel.setImport_file_uri(uri);
            }
        }
    }


}