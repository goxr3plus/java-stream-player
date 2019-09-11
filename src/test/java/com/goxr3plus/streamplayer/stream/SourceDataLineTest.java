package com.goxr3plus.streamplayer.stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.util.logging.Logger;

import static java.lang.Math.log10;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.Mockito.mock;

public class SourceDataLineTest {

    StreamPlayer player;
    private File audioFile;

    @BeforeEach
    void setup() {
        final Logger logger = mock(Logger.class);
        player = new StreamPlayer(logger);
        audioFile = new File("Logic - Ballin [Bass Boosted].mp3");
    }

    @AfterEach
    void tearDown() {
        player.stop();
    }

    @Test
    void gain() throws StreamPlayerException, InterruptedException {
        // Setup
        final double gain1 = 0.83;
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
        assertEquals(20*log10(gain1), actualGain1, delta);  // TODO: Investigate probable bug.
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
        final boolean listen = false;

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
    void mute() throws StreamPlayerException {
        assertFalse(player.getMute());
        player.setMute(true);
        assertFalse(player.getMute());
        player.open(audioFile);
        player.setMute(true);
        assertFalse(player.getMute());

        player.play();
        player.setMute(true);
        assertTrue(player.getMute()); // setMute works only after play() has been called.


        player.setMute(false);
        assertFalse(player.getMute());
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
    void playAndPause() throws StreamPlayerException, InterruptedException {
        boolean listen = true;
        player.open(audioFile);
        player.play();
        player.seekTo(30);
        if (listen) Thread.sleep(200);

        player.pause();
        if (listen) Thread.sleep(100);

        player.resume();  // TODO: Examine what happens if play() is called instead.
        if (listen) Thread.sleep(200);
        //player.stop();

        // TODO: asserts and listen=false
    }
}
