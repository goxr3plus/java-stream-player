package com.goxr3plus.streamplayer.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.goxr3plus.streamplayer.enums.Status;

/**
 * Tests of all or most of the public methods of StreamPlayer.
 * These unit tests are written primarily as documentation of the behavior and as example use case,
 * not as a part of test driven development.
 */
public class StreamPlayerMethodsTest {
    StreamPlayer player;
    private File audioFile;

    @BeforeEach
    void setup() {
        final Logger logger = mock(Logger.class);
        player = new StreamPlayer(logger);
        audioFile = new File("Logic - Ballin [Bass Boosted].mp3");
    }
    
    @Test
	void duration() throws StreamPlayerException {
		audioFile = new File("Logic - Ballin [Bass Boosted].mp3");
		player.open(audioFile);
		assertEquals(245, player.getDurationInSeconds());
		assertEquals(245000, player.getDurationInMilliseconds());
		assertNotNull(player.getDuration());
		assertEquals(245, player.getDuration().getSeconds());
		assertEquals(player.getDuration().toMillis(), player.getDurationInMilliseconds());

		audioFile = new File("kick.wav");
		player.open(audioFile);
		assertEquals(0, player.getDurationInSeconds());
		assertEquals(111, player.getDurationInMilliseconds());

		audioFile = new File("kick.mp3");
		player.open(audioFile);
		assertEquals(0, player.getDurationInSeconds());
		// Note: the result of calculating a .mp3's duration is different than that of a .wav file
		assertEquals(156, player.getDurationInMilliseconds());
	}

    @Test
    void balance() throws StreamPlayerException {
        // Setup
        final float wantedBalance = 0.5f;

        //Exercise
        player.open(audioFile);
        player.play();  // Necessary to be able to set the balance

        final float initialBalance = player.getBalance();
        player.setBalance(wantedBalance);
        player.stop();  // Probably not needed, but cleanup is good.
        final float actualBalance = player.getBalance();  // Can be made before or after stop()

        // Verify
        assertEquals(0, initialBalance);
        assertEquals(wantedBalance, actualBalance);
    }

    @Test
    void status() throws StreamPlayerException {
        // Setup
        final File audioFile = new File("Logic - Ballin [Bass Boosted].mp3");

        // Exercise
        final Status initialStatus = player.getStatus();

        player.open(audioFile);
        final Status statusAfterOpen = player.getStatus();

        player.stop();
        final Status statusAfterFirstStop = player.getStatus();

        player.play();
        final Status statusAfterPlay = player.getStatus();

        player.pause();
        final Status statusAfterPause = player.getStatus();

        player.seekTo(40);
        final Status statusAfterSeeking = player.getStatus();

        player.stop();
        final Status statusAfterSecondStop = player.getStatus();

        // Verify
        assertEquals(Status.NOT_SPECIFIED, initialStatus);
        assertEquals(Status.OPENED, statusAfterOpen);
        assertEquals(Status.STOPPED, statusAfterFirstStop);
        assertEquals(Status.PLAYING, statusAfterPlay);
        assertEquals(Status.PAUSED, statusAfterPause);
        assertEquals(Status.PAUSED, statusAfterSeeking);  // Still paused (or paused again)
        assertEquals(Status.STOPPED, statusAfterSecondStop);
    }

    @Test
    void gain() throws StreamPlayerException, InterruptedException {
        // Setup
        final double gain1_dB = 0.5;
        final double gain2 = 0.2;
        final double delta = 0.05;
        final boolean listen = false;

        // Exercise
        final float initialGain = player.getGainValue();
        player.open(audioFile);
        player.seekTo(30);
        player.play();
        player.setGain(gain1_dB);
        final float actualGain0 = player.getGainValue();
        if (listen) Thread.sleep(2000);
        final float actualGain1 = player.getGainValue();

        player.setGain(gain2);
        if (listen) Thread.sleep(2000);
        final float actualGain2 = player.getGainValue();

        player.setGain(gain1_dB);
        if (listen) Thread.sleep(2000);

        player.stop();

        // Verify
        assertEquals(0, initialGain);
        assertEquals(actualGain0, actualGain1);
        assertEquals(20.0 * Math.log10(gain1_dB), actualGain1, delta);

        // TODO: Consider changing the API. setGain() and getGainValue() have different scales.
        //  setGain(linear scale),
        //  whereas getGainValue() returns a logarithmic dB scale value. This is inconsistent.
    }

    /**
     * Plays music if "listen" is true.
     * Varies the gain, and checks that it can be read back.
     * If listen is true, it plays for 2 seconds per gain level.
     *
     * @throws StreamPlayerException
     * @throws InterruptedException
     */
    @Test
    void logScaleGain() throws StreamPlayerException, InterruptedException {
        // Setup
        final boolean listen = false;  // Set to true to listen to the test.

        // Exercise

        player.open(audioFile);
        player.seekTo(30);
        player.play();

        assertGainCanBeSetTo(-10, listen);
        assertGainCanBeSetTo(-75, listen);
        assertGainCanBeSetTo(0, listen);
        assertGainCanBeSetTo(6, listen);

        player.stop();
    }

    private void assertGainCanBeSetTo(double gain, boolean listen) throws InterruptedException {
        final float atGain = playAtGain(listen, gain);
        assertEquals(gain, atGain, 0.01);
    }

    private float playAtGain(boolean listen, double gain) throws InterruptedException {
        player.setLogScaleGain(gain);
        if (listen) {
            Thread.sleep(2000);
        }
        return player.getGainValue();
    }

    /**
     * Test that the maximum gain is greater than the minimum gain. That is about all we can expect.
     * The actual values depend on the available {@link SourceDataLine}.
     * We don't know anything about its scale beforehand.
     * <p>
     * The player must be started before maximum and minimum gains can be queried.
     * <p>
     * // TODO: Is it really acceptable that we cannot check gain before the player is started?
     *
     * @throws StreamPlayerException
     */
    @Test
    void maximumGain() throws StreamPlayerException {

        player.open(audioFile);
        player.play();
        final float maximumGain = player.getMaximumGain();
        final float minimumGain = player.getMinimumGain();
        player.stop();

        assertTrue(minimumGain < maximumGain,
                String.format("Maximum gain (%.2f) should be greater than minimum gain (%.2f).",
                        maximumGain, minimumGain)
        );
    }

    @Test
    void totalBytes() throws StreamPlayerException, InterruptedException {
        int expectedLengthOfExampleAudioFile = 5877062;


        assertEquals(-1, player.getTotalBytes());

        player.open(audioFile);
        assertEquals(expectedLengthOfExampleAudioFile, player.getTotalBytes());

        player.play();
        assertEquals(expectedLengthOfExampleAudioFile, player.getTotalBytes());
    }

    @Test
    void stopped() {

        assertFalse(player.isStopped());

        player.stop();
        assertTrue(player.isStopped());
    }

    @Test
    void sourceDataLine() throws StreamPlayerException {
        assertNull(player.getSourceDataLine());

        player.open(audioFile);
        assertNotNull(player.getSourceDataLine());

        player.play();

        assertNotNull(player.getSourceDataLine());
    }

    @Test
    void playing() throws StreamPlayerException {

        assertFalse(player.isPlaying());

        player.open(audioFile);
        assertFalse(player.isPlaying());

        player.play();
        assertTrue(player.isPlaying());

        player.pause();
        assertFalse(player.isPlaying());
    }

    @Test
    void pausedOrPlaying() throws StreamPlayerException {

        assertFalse(player.isPausedOrPlaying());

        player.open(audioFile);
        assertFalse(player.isPausedOrPlaying());

        player.play();
        assertTrue(player.isPausedOrPlaying());

        player.pause();
        assertTrue(player.isPausedOrPlaying());

        player.stop();
        assertFalse(player.isPausedOrPlaying());
    }

    @Test
    void paused() throws StreamPlayerException {
        assertFalse(player.isPaused());

        player.open(audioFile);
        assertFalse(player.isPaused());

        player.play();
        assertFalse(player.isPaused());

        player.pause();
        assertTrue(player.isPaused());
    }

    @Test
    void addStreamPlayerListener() throws StreamPlayerException, InterruptedException {
        // Setup
        final StreamPlayerListener listener = mock(StreamPlayerListener.class);

        ArgumentCaptor<Object> dataSourceCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Map> propertiesCaptor1 = ArgumentCaptor.forClass(Map.class);

        // Execute
        player.addStreamPlayerListener(listener);
        player.open(audioFile);
        player.play();
        Thread.sleep(30);

        // Verify
        verify(listener).opened(dataSourceCaptor.capture(), propertiesCaptor1.capture());
        Object value = dataSourceCaptor.getValue();
        assertTrue(value instanceof File);

        Map<String, Object> value11 = propertiesCaptor1.getValue();

        assertTrue(value11.containsKey("basicplayer.sourcedataline"));

        verify(listener, times(4)).statusUpdated(any());

        verify(listener, times(1)).opened(any(), any());

        verify(listener, atLeast(4)).progress(anyInt(), anyLong(), any(), any());
        verify(listener, atMost(30)).progress(anyInt(), anyLong(), any(), any());

        // TODO: Make separate tests for the different calls made to the listener
        // TODO: Do we need to test the values passed to these methods?

    }

    @Test
    void mute() throws StreamPlayerException {
        // TODO: How can mute be tested, without too much assumptions about the actual implementation?
        //  A manual test would involve listening.


        assertFalse(player.getMute());
        player.open(audioFile);
        player.play();
        player.setMute(true);
        assertTrue(player.getMute());
        player.setMute(false);
        assertFalse(player.getMute());

    }

    @Test
    void speedFactor() throws StreamPlayerException, InterruptedException {
        assertEquals(player.getSpeedFactor(), 1);

        double fast = 1;
        player.setSpeedFactor(fast);
        assertEquals(fast, player.getSpeedFactor());

        double slow = 0.5;
        player.open(audioFile);
        player.play();
        player.setSpeedFactor(slow);
        Thread.sleep(50);
        assertEquals(slow, player.getSpeedFactor());

        // TODO: Find a way to verify that the speed factor actually works. That it can be read back is no proof.
        //  I might be possible to play a short sequence of known length, and measure the time it takes.
        //  But things that take time are generally not advisable in unit tests.


    }

    @Test
    void equalizer() {
        player.setEqualizer(null, 0);
        // TODO: Find out what the intention of setEqualizer() is, and make a test for that assumption.
    }

    @Test
    void play() throws StreamPlayerException, InterruptedException {
        // Setup
        player.open(audioFile);

        // Pre-validate
        assertFalse(player.isPlaying());

        // Execute
        player.play();

        // Verify
        assertTrue(player.isPlaying());

        // TODO: Find way to verify that the player is actually playing, that doesn't need listening.
        //  The method might look at the playing position, but it must be fairly quick.
    }

    @Test
    void resume() throws StreamPlayerException {
        assertFalse(player.isPlaying());

        player.open(audioFile);
        assertFalse(player.isPlaying());

        player.play();
        assertTrue(player.isPlaying());

        player.pause();
        assertFalse(player.isPlaying());


        player.resume();
        assertTrue(player.isPlaying());
    }

    @Test
    void pause() throws StreamPlayerException {

        // Setup
        player.open(audioFile);
        player.play();
        // Pre-validate
        assertFalse(player.isPaused());

        // Execute
        player.pause();

        // Verify
        assertTrue(player.isPaused());

    }

    @Test
    void stop() {

        assertFalse(player.isStopped());

        player.stop();

        assertTrue(player.isStopped());

        // TODO: Find a way to verify that playback is stopped by running the stop method.
        //  The isStopped() method is not enough.
    }

    @Test
    void pan() throws StreamPlayerException {
        double delta = 1e-6;
        final float initialPan = player.getPan();
        assertEquals(0, initialPan);

        player.open(audioFile);
        player.play();

        double pan = -0.9;
        player.setPan(pan);
        assertEquals(pan, player.getPan(), delta);

        // If we set the pan outside the permitted range, it will not change
        // The permitted range is undefined.
        double outsideRange = 1.1;
        player.setPan(outsideRange);
        assertEquals(pan, player.getPan(), delta);

        float precision = player.getPrecision();
        assertNotEquals(0, precision);
        double expected = 128.0;  // Possibly platform dependent. Tested on a Mac with Intellij.
        assertEquals(expected, 1.0/precision, 2.0);
    }

    @Test
    void unknown() {
        player.isUnknown();
        // This is a useless test of a useless method.
        // TODO: Remove player.isUnknown(). It's not used, and it's useless.
        //  There is already getStatus().
    }

    @Test
    void open() throws StreamPlayerException {
        File file = spy(audioFile);
        player.open(file);
        verify(file, atLeast(1)).getPath();

        // It's unclear what the contract of open() is; what we need it to do.
        // It's a pre-requisite for play(), but play() doesn't throw an
        // exception if open() is missing.
    }

    @Test
    void mixers() {
        List<String> mixers = player.getMixers();
        // TODO: Make this method player.getMixers() private, remove it from the interface.
        //  There is nothing that can be done with the information outside the private scope.
    }




    // The methods tested below aren't used elsewhere in this project, nor in XR3Player
    // TODO: Consider each of the tested methods below, to see if they can be removed from StreamPlayer.

    @Test
    void lineBufferSize() {
        player.getLineBufferSize();
        player.setLineBufferSize(0);
        fail("Test not done");
    }

    @Test
    void lineCurrentBufferSize() throws StreamPlayerException {
        // TODO: Document the purpose of getLineCurrentBufferSize(). What is it good for?
        //  Can it be removed? The method doesn't really return the current line buffer size,
        //  but a cached value, which might be the same thing. Hard to say.

        assertEquals(-1, player.getLineCurrentBufferSize(), "Initially, the buffer size is undefined, coded as -1.");

        player.open(audioFile);
        assertEquals(-1, player.getLineCurrentBufferSize(), "After the player is opened, the buffer size is undefined");

        player.play();
        assertEquals(2 * 44100, player.getLineCurrentBufferSize(), "After the play starts, the buffer size 1 second at CD sampling rate");
    }

    @Test
    void minimumGain() {
        player.getMinimumGain();

        fail("Test not done");
    }

    @Test
    void positionByte() {
        player.getPositionByte();

        fail("Test not done");
    }

    @Test
    void precision() throws StreamPlayerException {
        assertEquals(0f, player.getPrecision());

        player.open(audioFile);
        player.play();

        assertNotEquals(0f, player.getPrecision());
        // On one computer the precision = 1/128. There are no guarantees.
    }

    @Test
    void opened() throws StreamPlayerException {
        assertFalse(player.isOpened());

        player.open(audioFile);
        assertTrue(player.isOpened());
    }

    @Test
    void seeking() {
        player.isSeeking();

        fail("Test not done");
    }

    @Test
    void removeStreamPlayerListener() {
        player.removeStreamPlayerListener(null);

        fail("Test not done");
    }

    @Test
    void seekTo() throws StreamPlayerException, IOException, UnsupportedAudioFileException {

        // Some tests before we do the real tests
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(audioFile);


        // Setup
        player.open(audioFile);
        player.play();
        player.pause();
        int encodedStreamPosition1 = player.getEncodedStreamPosition();

        // Execute
        player.seekTo(10);

        // Verify
        int encodedStreamPosition2 = player.getEncodedStreamPosition();
        assertTrue(encodedStreamPosition2 > encodedStreamPosition1);

        // Execute: go backwards
        player.seekTo(5);

        // Verify: position goes backwards
        int encodedStreamPosition3 = player.getEncodedStreamPosition();
        assertTrue(encodedStreamPosition3 < encodedStreamPosition2);
    }

    @Test
    void equalizerKey() {
        player.setEqualizerKey(0, 0);

        fail("Test not done");
    }

    @Test
    void setMixer() throws StreamPlayerException {
        //Get all available mixers
        List<String> mixers = player.getMixers();

        //Use the last mixer (this is never the default)
        String mixer = mixers.get(mixers.size()-1);

        //Set the mixer
        player.setMixerName(mixer);

        //Create a line, this will either use the set mixer or set the name to null
        player.open(audioFile);

        assertEquals(mixer, player.getMixerName());
    }

}
