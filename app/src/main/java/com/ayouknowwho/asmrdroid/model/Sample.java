package com.ayouknowwho.asmrdroid.model;

public class Sample {
    private String tag;
    private byte[] audio_data;

    // Constructor
    public Sample(String arg_tag, byte[] arg_audio_data) {
        tag = arg_tag;
        audio_data = arg_audio_data;
    }

    public String getTag() {
        return tag;
    }

    public byte[] getAudioData() {
        return audio_data;
    }
}
