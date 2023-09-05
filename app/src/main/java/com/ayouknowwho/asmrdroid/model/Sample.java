package com.ayouknowwho.asmrdroid.model;

public class Sample {
    private Integer id;
    private Integer source_id;
    private String tag;
    private byte[] audio_data;

    public Sample(Integer id, String tag, Integer source_id, byte[] audio_data) {
        this.id = id;
        this.source_id = source_id;
        this.tag = tag;
        this.audio_data = audio_data;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSource_id() {
        return source_id;
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

    public void setSource_id(Integer source_id) {
        this.source_id = source_id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAudio_data(byte[] audio_data) {
        this.audio_data = audio_data;
    }
}
