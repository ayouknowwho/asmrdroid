package com.ayouknowwho.asmrdroid.model;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class AudioMathHelper {
    private final static Integer MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8; // compatible with all JVMs
    private final static Integer TEST_ARRAY_SIZE = 16 * 1024;

    public static long ConvertPercentToNthFrame(Integer start_percent, long num_frames) {
        float frame_multiplier = (float) start_percent / (float) 100;
        return (long) (num_frames * frame_multiplier);
    }

    public static Integer numFramesBufferCanFit(Integer buffer_size, Integer num_channels) {
        return (buffer_size / num_channels);
    }

    public static Integer numBuffersToFitFrames(long start_frame, long end_frame, Integer num_frames_per_buffer) {
        long num_frames_to_fit = end_frame - start_frame;
        Integer num_buffers = 0;
        while (num_frames_to_fit > 0) {
            num_buffers++;
            num_frames_to_fit = num_frames_to_fit - num_frames_per_buffer;
        }
        return num_buffers;
    }

    public static Integer requiredArraySize(Integer num_frames, Integer num_channels, long sample_rate) throws ArraySizeException {
        final long array_size = num_frames * sample_rate * num_channels;
        if (array_size > MAX_ARRAY_SIZE) {
            throw new ArraySizeException("Cannot fit in single array.");
        }
        return Math.toIntExact(array_size);
    }

    public static Integer getTempArraySize() {
        return TEST_ARRAY_SIZE;
    }

    public static Integer getRandomSampleLengthInFrames(Integer min_frames, Integer max_frames, long frame_shortfall) {
        // Assumes frame_shortfall is greater than min frames.
        if (frame_shortfall > max_frames) {
            return ThreadLocalRandom.current().nextInt(min_frames, max_frames + 1);
        }
        else {
            return Math.toIntExact(frame_shortfall);
        }
    }

    public static Integer convertSecondsToFrames(float seconds, long sample_rate) {
        return Math.round(sample_rate * seconds);
    }

    public static long numFramesInXMinutes(long sample_rate, Integer num_minutes_to_generate) {
        final Integer NUM_SECONDS_PER_MINUTE = 60;
        return (sample_rate * num_minutes_to_generate * NUM_SECONDS_PER_MINUTE);
    }

    public static Sample convertedSample(Sample inSample, long target_sample_rate, Integer target_bits_per_sample, Integer target_num_channels) {
        // TODO: currently returns the same sample it is given
        Integer source_id = inSample.getSource_id();
        String tag = inSample.getTag();
        Integer old_num_channels = inSample.getNum_channels();
        Integer old_bits_per_channel = inSample.getBits_per_sample();
        long old_sample_rate = inSample.getSample_rate();
        Integer num_frames = inSample.getNum_frames();
        double[] old_audio_data = inSample.getAudio_data();
        // int[] old_audio_data_ints = convertByteBufferToIntBuffer(old_audio_data);

        if (old_num_channels < target_num_channels) {
            // TODO: duplicate channels
        } else if (old_num_channels > target_num_channels) {
            // TODO: delete extra channels
        }

        if (!Objects.equals(old_bits_per_channel, target_bits_per_sample)) {
            // TODO: rescale samples
        }

        if (old_sample_rate != target_sample_rate) {
            // TODO: "resample" each new sample
        }

        double[] new_audio_data = old_audio_data;

        Sample convertedSample = new Sample(source_id, tag, target_num_channels, num_frames, target_bits_per_sample, target_sample_rate, new_audio_data);
        return convertedSample;
    }
}
