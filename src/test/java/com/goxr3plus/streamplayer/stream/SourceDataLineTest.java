package com.goxr3plus.streamplayer.stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.logging.Logger;

import static java.lang.Math.log10;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
