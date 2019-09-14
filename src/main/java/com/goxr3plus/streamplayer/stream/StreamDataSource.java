package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class StreamDataSource implements DataSource {

    private InputStream source;

    StreamDataSource(InputStream source) {
        this.source = source;
    }

    @Override
    public AudioFileFormat getAudioFileFormat() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioFileFormat(source);
    }

    @Override
    public AudioInputStream getAudioInputStream() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(source);
    }

    @Override
    public int getDurationInSeconds() {
        return -1;
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "StreamDataSource with " + source.toString();
    }

    @Override
    public boolean isFile() {
       return false;
   }
}
