package com.ayouknowwho.asmrdroid.model;

public class SampleRepository {
    private SampleDbHelper sampleDbHelper;
    private String opened;
    private String num_samples;
    private String corrupted;

    public SampleRepository() {
        // TODO: initialise the database and set sampleDb
        opened = "Repository not opened";
        num_samples = "Samples not counted";
        corrupted = "Corruption not found";
    }

    // Setters
    private void setOpened(String arg_opened) {
        opened = arg_opened;
    }

    private void setNum_samples(String arg_num_samples) {
        num_samples = arg_num_samples;
    }

    private void setCorrupted(String arg_corrupted) {
        corrupted = arg_corrupted;
    }

    // Getters
    public String getOpened() {
        return opened;
    }

    public String getNum_samples() {
        return num_samples;
    }

    public String getCorrupted() {
        return corrupted;
    }

    public int storeSample(Sample sample) {
        // TODO: store sample object to database
        return 0;
    }

    private Sample retrieveSample(String tag) {
        Sample sample = null;
        // TODO: get a sample with the given tag
        return sample;
    }

    public Sample[] retrieveSamples(String tag, int quantity) {
        Sample[] samples = new Sample[quantity];
        for (int i = 0; i < quantity; i++) {
            samples[i] = retrieveSample(tag);
        }
        return samples;
    }

    public int checkRepository() {
        boolean updateSuccess = false;
        // TODO: update repositoryState
        if (updateSuccess == true) {
            return 0;
        } else {
            return 1;
        }
    }

}
