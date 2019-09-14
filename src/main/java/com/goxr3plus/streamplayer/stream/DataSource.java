package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DataSource {
    private Object source;

    DataSource(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }


    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "DataSource with " + source.toString();
    }

    AudioFileFormat getAudioFileFormat() throws UnsupportedAudioFileException, IOException {
        AudioFileFormat format = null;
        if (source instanceof URL) {
            format = AudioSystem.getAudioFileFormat((URL) source);

        } else if (source instanceof File) {
            format = AudioSystem.getAudioFileFormat((File) source);

        } else if (source instanceof InputStream) {
            format = AudioSystem.getAudioFileFormat((InputStream) source);
        }
        return format;
    }

    AudioInputStream getAudioInputStream() throws UnsupportedAudioFileException, IOException {
        AudioInputStream stream = null;
        if (source instanceof URL) {
            stream = AudioSystem.getAudioInputStream((URL) source);

        } else if (source instanceof File) {
            stream = AudioSystem.getAudioInputStream((File) source);

        } else if (source instanceof InputStream) {
            stream = AudioSystem.getAudioInputStream((InputStream) source);
        }
        return stream;
    }

}
