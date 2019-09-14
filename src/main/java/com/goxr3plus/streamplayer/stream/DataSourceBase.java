package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.AudioType;
import com.goxr3plus.streamplayer.tools.TimeTool;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class DataSourceBase implements DataSource {
    protected Object source;

    DataSourceBase(Object source) {
        this.source = source;
    }


    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "DataSource with " + source.toString();
    }

    @Override
    public int getDurationInSeconds() {

        // Audio resources from file||URL||inputStream.
        if (source instanceof File) {
            return TimeTool.durationInSeconds(((File) source).getAbsolutePath(), AudioType.FILE);
        } else if (source instanceof URL) { //todo
            return -1;
        } else if (source instanceof InputStream) { //todo
            return -1;
        }

        return -1;

    }

     @Override
     public boolean isFile() {
        return source instanceof File;
    }

}
