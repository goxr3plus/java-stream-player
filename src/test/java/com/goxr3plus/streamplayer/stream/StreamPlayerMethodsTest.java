package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
        final double gain1 = 0.99;
        final double gain2 = 0.2;
        final double delta = 0.05;
        final boolean listen = false;

        // Exercise
        final float initialGain = player.getGainValue();
        player.open(audioFile);
        player.seekTo(30);
        player.play();
        player.setGain(gain1);
        final float actualGain1First = player.getGainValue();
        if (listen) Thread.sleep(2000);
        final float actualGain1 = player.getGainValue();

        player.setGain(gain2);
        if (listen) Thread.sleep(2000);
        final float actualGain2 = player.getGainValue();

        player.setGain(gain1);
        if (listen) Thread.sleep(2000);

        player.stop();

        // Verify
        assertEquals(0, initialGain);
        assertEquals(actualGain1First, actualGain1);
        assertEquals(gain1, actualGain1, delta);  // TODO: Investigate probable bug.
        //  fail("Test not done");
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
        final boolean listen = true;

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
    void totalBytes() {
        player.getTotalBytes();

        fail("Test not done");
    }

    @Test
    void stopped() {
        player.isStopped();

        fail("Test not done");
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
    void playing() {
        final boolean playing = player.isPlaying();

        assertFalse(playing);

        fail("Test not done");
    }

    @Test
    void pausedOrPlaying() {
        player.isPausedOrPlaying();

        fail("Test not done");
    }

    @Test
    void paused() {
        player.isPaused();

        fail("Test not done");
    }

    @Test
    void addStreamPlayerListener_dontAcceptNull() {
        assertThrows(Exception.class, () -> player.addStreamPlayerListener(null));

        fail("Test not done");
    }

    @Test
    void addStreamPlayerListener() {
        final StreamPlayerListener listener = mock(StreamPlayerListener.class);
        player.addStreamPlayerListener(listener);

        fail("Test not done");  // TODO: CHeck that the listener is actually added
    }

    @Test
    void mute() {
        player.getMute();
        player.setMute(false);

        fail("Test not done");
    }

    @Test
    void speedFactor() {
        player.getSpeedFactor();
        player.setSpeedFactor(1000);

        fail("Test not done");
    }

    @Test
    void equalizer() {
        player.setEqualizer(null, 0);

        fail("Test not done");
    }

    @Test
    void play() throws StreamPlayerException {
        player.play();

        fail("Test not done");
    }

    @Test
    void resume() {
        player.resume();

        fail("Test not done");
    }

    @Test
    void pause() {
        player.pause();

        fail("Test not done");
    }

    @Test
    void stop() {
        player.stop();

        fail("Test not done");
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

        double outsideRange = 1.1;
        player.setPan(outsideRange);
        assertEquals(pan, player.getPan(), delta);
    }

    @Test
    void unknown() {
        player.isUnknown();

        fail("Test not done");
    }

    @Test
    void open() throws StreamPlayerException {
        player.open(null);

        fail("Test not done");
    }

    @Test
    void mixers() {
        player.getMixers();

        fail("Test not done");
    }

    @Test
    void seekBytes() throws StreamPlayerException {
        player.seekBytes(0);

        fail("Test not done");
    }


    // The methods tested below aren't used elsewhere in this project, nor in XR3Player

    @Test
    void lineBufferSize() {
        player.getLineBufferSize();
        player.setLineBufferSize(0);
        fail("Test not done");
    }

    @Test
    void lineCurrentBufferSize() {
        player.getLineCurrentBufferSize();

        fail("Test not done");
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
    void precision() {
        player.getPrecision();

        fail("Test not done");
    }

    @Test
    void opened() {
        player.isOpened();

        fail("Test not done");
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
    void seekTo() throws StreamPlayerException {
        player.seekTo(1000);

        fail("Test not done");
    }

    @Test
    void equalizerKey() {
        player.setEqualizerKey(0, 0);

        fail("Test not done");
    }


}
