package com.ayouknowwho.asmrdroid.model;

import java.io.Serializable;

public class Sample implements Serializable {
    private Integer source_id;
    private String tag;
    private Integer num_channels;
    private Integer num_frames;
    private Integer bits_per_sample;
    private long sample_rate;
    private double[] audio_data;

    public Sample(Integer source_id, String tag, Integer num_channels, Integer num_frames, Integer bits_per_sample, long sample_rate, double[] audio_data) {
        this.source_id = source_id;
        this.tag = tag;
        this.num_channels = num_channels;
        this.num_frames = num_frames;
        this.bits_per_sample = bits_per_sample;
        this.sample_rate = sample_rate;
        this.audio_data = audio_data;
    }

    public Integer getSource_id() {
        return source_id;
    }

    public String getTag() {
        return tag;
    }

    public double[] getAudio_data() {
        return audio_data;
    }

    public void setSource_id(Integer source_id) {
        this.source_id = source_id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAudio_data(double[] audio_data) {
        this.audio_data = audio_data;
    }

    public Integer getNum_channels() {
        return num_channels;
    }

    public void setNum_channels(Integer num_channels) {
        this.num_channels = num_channels;
    }

    public Integer getNum_frames() {
        return num_frames;
    }

    public void setNum_frames(Integer num_frames) {
        this.num_frames = num_frames;
    }

    public Integer getBits_per_sample() {
        return bits_per_sample;
    }

    public void setBits_per_sample(Integer bits_per_sample) {
        this.bits_per_sample = bits_per_sample;
    }

    public long getSample_rate() {
        return sample_rate;
    }

    public void setSample_rate(long sample_rate) {
        this.sample_rate = sample_rate;
    }
}
