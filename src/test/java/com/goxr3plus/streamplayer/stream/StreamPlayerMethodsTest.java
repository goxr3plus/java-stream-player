package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
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

        // If we set the pan outside the permitted range, it will not change
        // The permitted range is undefined.
        double outsideRange = 1.1;
        player.setPan(outsideRange);
        assertEquals(pan, player.getPan(), delta);

        float precision = player.getPrecision();
        assertNotEquals(0, precision);
        assertEquals(3f, 1.0/precision);
    }

    @Test
    void unknown() {
        player.isUnknown();

        fail("Test not done");
    }

    @Test
    void open() throws StreamPlayerException {
        File file = null;
        player.open(file);

        fail("Test not done");
    }

    @Test
    void mixers() {
        List<String> mixers = player.getMixers();
        // TODO: Make this method player.getMixers() private, remove it from the interface.
        //  There is nothing that can be done with the information outside the private scope.
    }

    @Test
    void seekBytes() throws StreamPlayerException {
        player.open(audioFile);
        int positionByte1 = player.getPositionByte();

        player.seekBytes(100);
        int positionByte2 = player.getPositionByte();

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
        int positionByte1 = player.getPositionByte();
        assertNotEquals(AudioSystem.NOT_SPECIFIED, positionByte1, "If we cannot check the position, how can we verify seek?");

        // Execute
        player.seekTo(10);

        // Verify
        int positionByte2 = player.getPositionByte();
        assertNotEquals(positionByte2, positionByte1);

        fail("Test not done");
    }

    @Test
    void equalizerKey() {
        player.setEqualizerKey(0, 0);

        fail("Test not done");
    }


}
