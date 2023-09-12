package com.ayouknowwho.asmrdroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ayouknowwho.asmrdroid.interfaces.FileImportStarter;
import com.ayouknowwho.asmrdroid.interfaces.FilePicker;
import com.ayouknowwho.asmrdroid.interfaces.GenerateAudioStarter;
import com.ayouknowwho.asmrdroid.model.ArraySizeException;
import com.ayouknowwho.asmrdroid.model.Sample;
import com.ayouknowwho.asmrdroid.model.WavFile;
import com.ayouknowwho.asmrdroid.model.WavFileException;
import com.ayouknowwho.asmrdroid.model.AudioMathHelper;
import com.ayouknowwho.asmrdroid.viewModel.AudioRepositoryViewModel;
import com.ayouknowwho.asmrdroid.viewModel.GenerateViewModel;
import com.ayouknowwho.asmrdroid.viewModel.ImportViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements FilePicker, FileImportStarter, GenerateAudioStarter {

    final static int PICK_FILE_REQUEST_CODE = 1;
    final static String[] SUPPORTED_IMPORT_MIME_TYPES = {"audio/wav", "audio/x-wav"};


    private ImportViewModel importViewModel;
    private AudioRepositoryViewModel audioRepositoryViewModel;
    private GenerateViewModel generateViewModel;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view models
        importViewModel = new ViewModelProvider(this).get(ImportViewModel.class);
        audioRepositoryViewModel = new ViewModelProvider(this).get(AudioRepositoryViewModel.class);
        generateViewModel = new ViewModelProvider(this).get(GenerateViewModel.class);

        // Prepare work thread
        executorService = Executors.newSingleThreadExecutor();
        Runnable looperPrepareRunnable = new LooperPrepareRunnable();
        executorService.execute(looperPrepareRunnable);

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
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, SUPPORTED_IMPORT_MIME_TYPES);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }


    public void importFileToRepository() {
        Runnable importRunnable = new ImportFileRunnable();
        executorService.execute(importRunnable);
    }


    public void generateSamplesFromAllImportedFiles() {
        Runnable generateRunnable = new GenerateRunnable();
        executorService.execute(generateRunnable);
    }


    public void generateSamplesForFileIndex(Integer file_index) {
        // TODO: Open File from file_index as an audio object, we currently assume wav
        String inFilename = audioRepositoryViewModel.getFilenameFromFileIndex(file_index);

        Integer source_id = audioRepositoryViewModel.getIdFromFileIndex(file_index);
        String tag = audioRepositoryViewModel.getTagFromFileIndex(file_index);
        File inFile;
        WavFile inWavFile = null;

        Log.i("Sample Generation","Generating samples for " + inFilename);

        try {
            // Open File as WavFile
            inFile = getFileStreamPath(inFilename);
            inWavFile = WavFile.openWavFile(inFile);
        } catch (java.io.FileNotFoundException e) {
            Toast.makeText(this.getApplicationContext(), "File listed in database not found, recommend new empty database.", Toast.LENGTH_SHORT).show();
            return;
        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(), "IO Error when opening file listed in database.", Toast.LENGTH_SHORT).show();
            return;
        } catch (WavFileException e) {
            Toast.makeText(this.getApplicationContext(), "WAV Error when opening file listed in database.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get information from WavFile
        Integer num_channels = inWavFile.getNumChannels();
        long num_frames = inWavFile.getNumFrames();
        long sample_rate = inWavFile.getSampleRate();
        Integer bits_per_sample = inWavFile.getValidBits();
        // TODO: If bits per sample is above 32, use a long[] buffer

        // Get values from ViewModel
        final Integer START_PERCENT = generateViewModel.getStartPercent();
        final Integer END_PERCENT = generateViewModel.getEndPercent();
        final float min_sample_length_s = generateViewModel.getMinSampleLengthS();
        final float max_sample_length_s = generateViewModel.getMaxSampleLengthS();
        final Integer min_sample_frames = AudioMathHelper.convertSecondsToFrames(min_sample_length_s, sample_rate);
        final Integer max_sample_frames = AudioMathHelper.convertSecondsToFrames(max_sample_length_s, sample_rate);

        // Create a buffer of large size to quickly read ahead in the WavFile
        Integer temp_buffer_size = AudioMathHelper.getTempArraySize();
        int[] temp_buffer = new int[temp_buffer_size];

        // Move read head to start position
        long start_frame = AudioMathHelper.ConvertPercentToNthFrame(START_PERCENT, num_frames);
        long current_frame = 0;
        Integer temp_buffer_size_in_frames = AudioMathHelper.numFramesBufferCanFit(temp_buffer_size, num_channels);
        try {
            while (current_frame < start_frame) {
                long frame_shortfall = start_frame - current_frame;
                if (frame_shortfall > temp_buffer_size_in_frames) {
                    inWavFile.readFrames(temp_buffer, temp_buffer_size_in_frames);
                    current_frame += temp_buffer_size_in_frames;
                } else {
                    inWavFile.readFrames(temp_buffer, (int) frame_shortfall);
                    current_frame += frame_shortfall;
                    Log.i("Sample Generation","Reached start position in file " + inFilename);
                }
            }
        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(), "IO Error when reading to start position in WAV file.", Toast.LENGTH_SHORT).show();
            return;
        } catch (WavFileException e) {
            Toast.makeText(this.getApplicationContext(), "WAV Error when reading to start position in WAV file.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Read from START_PERCENT to END_PERCENT with random length chunks between
        // MIN_LENGTH_MS and MAX_LENGTH_MS, store each chunk as a sample
        long end_frame = AudioMathHelper.ConvertPercentToNthFrame(END_PERCENT, num_frames);
        Integer sample_count = 0;
        Log.i("Sample Generation","Generating samples from frame " + Long.toString(current_frame));
        try {
            while (current_frame < end_frame) {
                long frame_shortfall = end_frame - current_frame;
                if (frame_shortfall < min_sample_frames) {
                    break;
                } else {
                    Integer sample_length_frames = AudioMathHelper.getRandomSampleLengthInFrames(min_sample_frames, max_sample_frames, frame_shortfall);
                    Log.i("Sample Generation","Generating sample of length " + sample_length_frames);

                    // Create a buffer to hold the frames
                    Integer buffer_size;
                    try {
                        buffer_size = AudioMathHelper.requiredArraySize(sample_length_frames, num_channels, bits_per_sample);
                    } catch (ArraySizeException e) {
                        Toast.makeText(this.getApplicationContext(), "MAX LENGTH too long, will not fit in array.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int[] buffer = new int[buffer_size];

                    // Read into the buffer and then convert to a byte array
                    inWavFile.readFrames(buffer, sample_length_frames);
                    byte[] audio_data = AudioMathHelper.convertIntBufferToByteBuffer(buffer);

                    // Create a new Sample and store it
                    Sample sample = new Sample(source_id, tag, num_channels, num_frames, bits_per_sample, sample_rate, audio_data);
                    audioRepositoryViewModel.storeSample(sample);

                    // Update the counters
                    current_frame += sample_length_frames;
                    sample_count++;
                }
            }
        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(), "IO Error when reading from WAV file to buffer.", Toast.LENGTH_SHORT).show();
            return;
        } catch (WavFileException e) {
            Toast.makeText(this.getApplicationContext(), "WAV Error when reading from WAV file to buffer.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this.getApplicationContext(),sample_count.toString() + " samples generated for " + inFilename, Toast.LENGTH_SHORT).show();
    }


    public void generateAudioFile() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // TODO: Assumes a valid uri is passed except default.default.default
                // TODO: Currently generates from all imported files
                Looper.prepare();
                // Check samples are generated
                generateSamplesFromAllImportedFiles();

                // Get data from ViewModel and check valid
                final Integer num_minutes_to_generate = generateViewModel.getNum_minutes_to_generate();
                final Uri external_files_directory = generateViewModel.getExternal_files_dir();
                if (external_files_directory.toString() == "default.default.default"){
                    Toast.makeText(MainActivity.super.getApplicationContext(), "Destination Error.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // New variables and conversion
                final LocalDateTime now = LocalDateTime.now();
                final String filename = now.toString() + ".mp3";
                final Uri generate_file_uri = Uri.withAppendedPath(external_files_directory, filename);
                final String generate_file_destination = generate_file_uri.getPath();



                Toast.makeText(MainActivity.super.getApplicationContext(), String.join(" ", "Exporting to", generate_file_destination), Toast.LENGTH_SHORT).show();
        /*
        // Start Streams
        InputStream is = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        int originalSize = 4096;

        // Copy data from input to output
        try {
            // bos = new BufferedOutputStream(new FileOutputStream(outDestination));
            File output_file = new File(generate_file_destination);
            FileOutputStream fos = new FileOutputStream(output_file);
            bos = new BufferedOutputStream(fos);
            byte[] buf = new byte[originalSize];
            bis.read(buf);
            int writeCount = 0;
            do {
                bos.write(buf);
                writeCount++;
            } while (bis.read(buf) != -1);
            Toast.makeText(this.getApplicationContext(), String.valueOf(writeCount * originalSize) + "kB File Generated.", Toast.LENGTH_SHORT).show();
        } catch (java.io.FileNotFoundException e) {
            Toast.makeText(this.getApplicationContext(), "Output file not found.", Toast.LENGTH_SHORT).show();
            return;
        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(), "IO Error.", Toast.LENGTH_SHORT).show();
            return;
        } */
            }
        });
    }

    private String getUniqueFilename(Uri uri) {
        // TODO: Implement, this currently returns a non-unique destination
        String uriString = uri.getPath();
        String filenameString = uriString.substring(uriString.lastIndexOf(File.separator) + 1);
        filenameString = filenameString.replace(" ", "");
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
                importViewModel.setImport_file_uri(uri);
            }
        }
    }


    public class LooperPrepareRunnable implements Runnable {
        public void run() {
            Looper.prepare();
        }
    }


    public class ImportFileRunnable implements Runnable {
        public void run(){
            // TODO: Assumes a valid uri is passed except default.default.default

            // Get the Uri
            final Uri file_uri = importViewModel.getImport_file_live_uri().getValue();
            final String file_uri_string = file_uri.toString();
            if (file_uri_string == "default.default.default"){
                Toast.makeText(MainActivity.this, "File not chosen.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Toast.makeText(MainActivity.this, String.join(" ", "Importing", file_uri_string), Toast.LENGTH_SHORT).show();
            }

            // Start Streams
            InputStream is;
            BufferedInputStream bis;
            BufferedOutputStream bos;
            String outDestination = getUniqueFilename(file_uri);
            int read_chunk_size;

            // Initialize input stream
            try {
                is = getContentResolver().openInputStream(file_uri);
                bis = new BufferedInputStream(is);
                read_chunk_size = is.available();
            } catch (java.io.FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "Input file not found.", Toast.LENGTH_SHORT).show();
                return;
            } catch (java.io.IOException e) {
                Toast.makeText(MainActivity.this, "IO Error while opening streams.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Copy data from input to output
            try {
                // bos = new BufferedOutputStream(new FileOutputStream(outDestination));
                bos = new BufferedOutputStream(MainActivity.this.openFileOutput(outDestination, MODE_PRIVATE));
                byte[] buf = new byte[read_chunk_size];
                bis.read(buf);
                int writeCount = 0;
                do {
                    bos.write(buf);
                    writeCount++;
                } while (bis.read(buf) != -1);
                Toast.makeText(MainActivity.this, String.valueOf(writeCount * read_chunk_size) + "kB File Imported.", Toast.LENGTH_SHORT).show();
            } catch (java.io.FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "Output file not found.", Toast.LENGTH_SHORT).show();
                return;
            } catch (java.io.IOException e) {
                Toast.makeText(MainActivity.this, "IO Error while copying data.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert file reference into database
            audioRepositoryViewModel.storeAudioFile(outDestination);

            // Reset import file destination
            importViewModel.setImport_file_uri(Uri.parse("default.default.default"));

            // Close streams
            try {
                bos.close();
                bis.close();
                is.close();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "IO Error while closing streams.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class GenerateRunnable implements Runnable {
        public void run() {
            Integer num_files = audioRepositoryViewModel.getNum_files();
            for (Integer file_index = 0; file_index < num_files; file_index++) {
                if (!audioRepositoryViewModel.sampleExistsForFileIndex(file_index)) {
                    generateSamplesForFileIndex(file_index);
                }
            }
            Toast.makeText(MainActivity.this, "All imported files have had samples extracted.", Toast.LENGTH_SHORT).show();
        }
    }
}