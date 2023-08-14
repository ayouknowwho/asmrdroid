package com.ayouknowwho.asmrdroid.model;

public class Sample {
    private Integer id;
    private String tag;
    private byte[] audio_data;

    public Sample(Integer id, String tag, byte[] audio_data) {
        this.id = id;
        this.tag = tag;
        this.audio_data = audio_data;
    }

    public Integer getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public byte[] getAudio_data() {
        return audio_data;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAudio_data(byte[] audio_data) {
        this.audio_data = audio_data;
    }
}
