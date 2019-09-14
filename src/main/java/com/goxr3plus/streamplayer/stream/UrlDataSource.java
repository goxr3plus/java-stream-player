package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class UrlDataSource extends DataSource {

    UrlDataSource(URL source) {
        super(source);
    }

    @Override
    public AudioFileFormat getAudioFileFormat() throws UnsupportedAudioFileException, IOException {
        return super.getAudioFileFormat();
    }
}
