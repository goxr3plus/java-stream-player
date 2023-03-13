package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.time.Duration;

public interface DataSource {

    Object getSource(); // TODO: Try to make this method not needed.

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
    String toString();

    /**
     * @return the format of the source data
     * @throws UnsupportedAudioFileException if the file type is unsupported
     * @throws IOException if there is a runtime problem with IO.
     */
    AudioFileFormat getAudioFileFormat() throws UnsupportedAudioFileException, IOException;

    /**
     * @return a stream representing the input data, regardless of source.
     * @throws UnsupportedAudioFileException if the file type is unsupported
     * @throws IOException if there is a runtime problem with IO.
     */
    AudioInputStream getAudioInputStream() throws UnsupportedAudioFileException, IOException;

    /**
     * @return The duration of the source data in seconds, or -1 if duration is unavailable.
     */
    int getDurationInSeconds();
    
    /**
     * @return The duration of the source data in milliseconds, or -1 if duration is unavailable.
     */
    long getDurationInMilliseconds();
    
    /**
     * @return The duration of the source data in a {@code java.time.Duration} instance, or null if unavailable
     */
    Duration getDuration();

    /**
     * @return true if the DataSource is a FileDataSource,
     * which happens if the source used to create it is a File
     */
    boolean isFile();
}
