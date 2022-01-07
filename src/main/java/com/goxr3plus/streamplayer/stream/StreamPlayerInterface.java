package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;

import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public interface StreamPlayerInterface {
    /**
     * Freeing the resources.
     */
    void reset();

    /**
     * Add a listener to be notified.
     *
     * @param streamPlayerListener the listener
     */
    void addStreamPlayerListener(StreamPlayerListener streamPlayerListener);

    /**
     * Remove registered listener.
     *
     * @param streamPlayerListener the listener
     */
    void removeStreamPlayerListener(StreamPlayerListener streamPlayerListener);

    /**
     * Open the specified object which can be File,URL or InputStream.
     *
     * @param object the object [File or URL or InputStream ]
     *
     * @throws StreamPlayerException the stream player exception
     * @deprecated Use one of {@link #open(File)}, {@link #open(URL)} or {@link #open(InputStream)} instead.
     */
    @Deprecated
    void open(Object object) throws StreamPlayerException;

    /**
     * Open the specified file for playback.
     *
     * @param file the file to be played
     *
     * @throws StreamPlayerException the stream player exception
     */
    void open(File file) throws StreamPlayerException;

    /**
     * Open the specified location for playback.
     *
     * @param url the location to be played
     *
     * @throws StreamPlayerException the stream player exception
     */
    void open(URL url) throws StreamPlayerException;

    /**
     * Open the specified stream for playback.
     *
     * @param stream the stream to be played
     *
     * @throws StreamPlayerException the stream player exception
     */
    void open(InputStream stream) throws StreamPlayerException;

    /**
     * Change the Speed Rate of the Audio , this variable affects the Sample Rate ,
     * for example 1.0 is normal , 0.5 is half the speed and 2.0 is double the speed
     * Note that you have to restart the audio for this to take effect
     *
     * @param speedFactor speedFactor
     */
    void setSpeedFactor(double speedFactor);

    /**
     * Starts the play back.
     *
     * @throws StreamPlayerException the stream player exception
     */
    void play() throws StreamPlayerException;

    /**
     * Pauses the play back.<br>
     * <p>
     * Player Status = PAUSED. * @return False if failed(so simple...)
     *
     * @return true, if successful
     */
    boolean pause();

    /**
     * Stops the play back.<br>
     * <p>
     * Player Status = STOPPED.<br>
     * Thread should free Audio resources.
     */
    void stop();

    /**
     * Resumes the play back.<br>
     * <p>
     * Player Status = PLAYING*
     *
     * @return False if failed(so simple...)
     */
    boolean resume();

    /**
     * Skip bytes in the File input stream. It will skip N frames matching to bytes,
     * so it will never skip given bytes len
     *
     * @param bytes the bytes
     *
     * @return value bigger than 0 for File and value = 0 for URL and InputStream
     *
     * @throws StreamPlayerException the stream player exception
     */
    long seekBytes(long bytes) throws StreamPlayerException;

    /**
     * Skip x seconds of audio
     * See  {@link #seekBytes(long)}
     *
     * @param seconds Seconds to Skip
     */
    //todo not finished needs more validations
    long seekSeconds(int seconds) throws StreamPlayerException;

    /**
     * Go to X time of the Audio
     * See  {@link #seekBytes(long)}
     *
     * @param seconds Seconds to Skip
     */
    long seekTo(int seconds) throws StreamPlayerException;

    int getDurationInSeconds();
    
    long getDurationInMilliseconds();
    
    Duration getDuration();

    /**
     * Calculates the current position of the encoded audio based on <br>
     * <b>nEncodedBytes = encodedAudioLength -
     * encodedAudioInputStream.available();</b>
     *
     * @return The Position of the encoded stream in term of bytes
     */
    int getEncodedStreamPosition();

    /**
     * Return SourceDataLine buffer size.
     *
     * @return -1 maximum buffer size.
     */
    int getLineBufferSize();

    /**
     * Return SourceDataLine current buffer size.
     *
     * @return The current line buffer size
     */
    int getLineCurrentBufferSize();

    /**
     * Returns all available mixers.
     *
     * @return A List of available Mixers
     */
    List<String> getMixers();

    /**
     * Returns Gain value.
     *
     * @return The Gain Value
     */
    float getGainValue();

    /**
     * Returns maximum Gain value.
     *
     * @return The Maximum Gain Value
     */
    float getMaximumGain();

    /**
     * Returns minimum Gain value.
     *
     * @return The Minimum Gain Value
     */
    float getMinimumGain();

    /**
     * Returns Pan precision.
     *
     * @return The Precision Value
     */
    float getPrecision();

    /**
     * Returns Pan value.
     *
     * @return The Pan Value
     */
    float getPan();

    /**
     * Return the mute Value(true || false).
     *
     * @return True if muted , False if not
     */
    boolean getMute();

    /**
     * Return the balance Value.
     *
     * @return The Balance Value
     */
    float getBalance();

    /**
     * Return the total size of this file in bytes.
     *
     * @return encodedAudioLength
     */
    long getTotalBytes();

    /**
     * Gets the source data line.
     *
     * @return The SourceDataLine
     */
    SourceDataLine getSourceDataLine();

    /**
     * This method will return the status of the player
     *
     * @return The Player Status
     */
    Status getStatus();

    /**
     * Set SourceDataLine buffer size. It affects audio latency. (the delay between
     * line.write(data) and real sound). Minimum value should be over 10000 bytes.
     *
     * @param size -1 means maximum buffer size available.
     */
    void setLineBufferSize(int size);

    /**
     * Set the name of the mixer. This should be called before opening a Line.
     *
     * @param mixerName the name
     */
    void setMixerName(String mixerName);

    /**
     * Sets Pan value. Line should be opened before calling this method. Linear
     * scale : -1.0 ... +1.0
     *
     * @param fPan the new pan
     */
    void setPan(double fPan);

    /**
     * Sets Gain value. Line should be opened before calling this method. Linear
     * scale 0.0 ... 1.0 Threshold Coef. : 1/2 to avoid saturation.
     *
     * @param fGain The new gain value
     */
    void setGain(double fGain);

    void setLogScaleGain(double logScaleGain);

    /**
     * Set the mute of the Line. Note that mute status does not affect gain.
     *
     * @param mute True to mute the audio of False to unmute it
     */
    void setMute(boolean mute);

    /**
     * Represents a control for the relative balance of a stereo signal between two
     * stereo speakers. The valid range of values is -1.0 (left channel only) to 1.0
     * (right channel only). The default is 0.0 (centered).
     *
     * @param fBalance the new balance
     */
    void setBalance(float fBalance);

    /**
     * Changes specific values from equalizer.
     *
     * @param array the array
     * @param stop the stop
     */
    void setEqualizer(float[] array, int stop);

    /**
     * Changes a value from equalizer.
     *
     * @param value the value
     * @param key the key
     */
    void setEqualizerKey(float value, int key);

    /**
     * @return The Speech Factor of the Audio
     */
    double getSpeedFactor();

    /**
     * Checks if is unknown.
     *
     * @return If Status==STATUS.UNKNOWN.
     */
    boolean isUnknown();

    /**
     * Checks if is playing.
     *
     * @return <b>true</b> if player is playing ,<b>false</b> if not.
     */
    boolean isPlaying();

    /**
     * Checks if is paused.
     *
     * @return <b>true</b> if player is paused ,<b>false</b> if not.
     */
    boolean isPaused();

    /**
     * Checks if is paused or playing.
     *
     * @return <b>true</b> if player is paused/playing,<b>false</b> if not
     */
    boolean isPausedOrPlaying();

    /**
     * Checks if is stopped.
     *
     * @return <b>true</b> if player is stopped ,<b>false</b> if not
     */
    boolean isStopped();

    /**
     * Checks if is opened.
     *
     * @return <b>true</b> if player is opened ,<b>false</b> if not
     */
    boolean isOpened();

    /**
     * Checks if is seeking.
     *
     * @return <b>true</b> if player is seeking ,<b>false</b> if not
     */
    boolean isSeeking();
}
