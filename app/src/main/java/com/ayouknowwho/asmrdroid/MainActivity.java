package com.ayouknowwho.asmrdroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.ayouknowwho.asmrdroid.viewModel.AudioRepositoryViewModel;
import com.ayouknowwho.asmrdroid.viewModel.GenerateViewModel;
import com.ayouknowwho.asmrdroid.viewModel.ImportViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements FilePicker, FileImportStarter {

    final static int PICK_FILE_REQUEST_CODE = 1;
    final static int IMPORT_FILE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up view models
        ImportViewModel importViewModel = new ViewModelProvider(this).get(ImportViewModel.class);
        AudioRepositoryViewModel audioRepositoryViewModel = new ViewModelProvider(this).get(AudioRepositoryViewModel.class);
        GenerateViewModel generateViewModel = new ViewModelProvider(this).get(GenerateViewModel.class);

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


    public void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }


    public void importFile() {
        // TODO: Assumes a valid uri is passed except default.default.default

        // Import ViewModels
        ImportViewModel importViewModel = new ViewModelProvider(this).get(ImportViewModel.class);
        AudioRepositoryViewModel audioRepositoryViewModel = new ViewModelProvider(this).get(AudioRepositoryViewModel.class);

        // Get the Uri
        final Uri file_uri = importViewModel.getImport_file_uri();
        final String file_uri_string = file_uri.toString();
        if (file_uri_string == "default.default.default"){
            Toast.makeText(this.getApplicationContext(), "File not chosen.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Toast.makeText(this.getApplicationContext(), String.join(" ", "Importing", file_uri_string), Toast.LENGTH_SHORT).show();
        }

        // Start Streams
        InputStream is = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String outDestination = getUniqueFilename(file_uri);
        int originalSize;

        // Initialize input stream
        try {
            is = getContentResolver().openInputStream(file_uri);
            bis = new BufferedInputStream(is);
            originalSize = is.available();
        } catch (java.io.FileNotFoundException e) {
            Toast.makeText(this.getApplicationContext(), "Input file not found.", Toast.LENGTH_SHORT).show();
            return;
        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(), "IO Error.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Copy data from input to output
        try {
            // bos = new BufferedOutputStream(new FileOutputStream(outDestination));
            bos = new BufferedOutputStream(this.openFileOutput(outDestination, MODE_PRIVATE));
            byte[] buf = new byte[originalSize];
            bis.read(buf);
            int writeCount = 0;
            do {
                bos.write(buf);
                writeCount++;
            } while (bis.read(buf) != -1);
            Toast.makeText(this.getApplicationContext(), String.valueOf(writeCount * originalSize) + "kB File Imported.", Toast.LENGTH_SHORT).show();
        } catch (java.io.FileNotFoundException e) {
            Toast.makeText(this.getApplicationContext(), "Output file not found.", Toast.LENGTH_SHORT).show();
            return;
        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(), "IO Error.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert file reference into database
        audioRepositoryViewModel.storeAudioFile(outDestination);

    }


    private String getUniqueFileDestination(Uri uri) {
        // TODO: Not currently used
        // TODO: Implement, this currently returns a non-unique destination
        // String fileDestinationString = getFilesDir() + File.separator + uri.getLastPathSegment();
        String fileDestinationString = getFilesDir() + File.separator + "1.m4a";
        // Uri fileDestinationUri = Uri.parse(fileDestinationString);
        return fileDestinationString;
    }


    private String getUniqueFilename(Uri uri) {
        // TODO: Implement, this currently returns a non-unique destination
        String uriString = uri.getPath();
        String filenameString = uriString.substring(uriString.lastIndexOf(File.separator) + 1);
        return filenameString;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_FILE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                ImportViewModel mainViewModel = new ViewModelProvider(this).get(ImportViewModel.class);
                mainViewModel.setImport_file_uri(uri);
            }
            /* TODO: This changes the ViewModel but the TextView on the ImportFragment only picks it up onCreateView, not onActivityResult */
        }
        else if (requestCode == IMPORT_FILE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            ImportViewModel importViewModel = new ViewModelProvider(this).get(ImportViewModel.class);
            Toast.makeText(this, "File Imported.", Toast.LENGTH_SHORT).show();
            // Reset the ViewModel Uri
            importViewModel.setImport_file_uri(Uri.parse("default.default.default"));
        }
    }
}