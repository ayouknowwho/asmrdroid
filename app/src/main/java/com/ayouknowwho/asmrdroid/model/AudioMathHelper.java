package com.ayouknowwho.asmrdroid.model;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ThreadLocalRandom;

public class AudioMathHelper {
    private final static Integer MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8; // compatible with all JVMs
    private final static Integer TEST_ARRAY_SIZE = 1024;

    public static long ConvertPercentToNthFrame(Integer start_percent, long num_frames) {
        float frame_multiplier = (float) start_percent / (float) 100;
        long frame = (long) (num_frames * frame_multiplier);
        return frame;
    }

    public static Integer numFramesBufferCanFit(Integer buffer_size, Integer num_channels) {
        Integer frame_size = num_channels;
        Integer num_frames = buffer_size / frame_size;
        return num_frames;
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
        Integer num_frames = Math.round(sample_rate * seconds);
        return num_frames;
    }

    public static byte[] convertIntBufferToByteBuffer(int[] int_buffer) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(int_buffer.length * Integer.BYTES);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(int_buffer);
        return byteBuffer.array();
    }
}
