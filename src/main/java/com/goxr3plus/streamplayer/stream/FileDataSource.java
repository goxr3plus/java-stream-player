package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class FileDataSource extends DataSource {

    File source;

    FileDataSource(File source) {
        super(source);
        this.source = source;
    }

    @Override
    public AudioFileFormat getAudioFileFormat() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioFileFormat(this.source);
    }
}
