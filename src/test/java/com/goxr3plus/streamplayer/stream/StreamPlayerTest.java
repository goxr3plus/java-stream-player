package com.goxr3plus.streamplayer.stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StreamPlayerTest {

    /**
     * This test was written to demonstrate some testing techniques.
     * As it is now, it's not deterministic. It passes sometimes and fails sometimes. Such test
     * is not acceptable in a test suite that must always pass. Therefore it's disabled
     * but kept, so that the test (and the production code) can be improved and then enabled.
     *
     * When the test is improved such that it is worthy of production code, it should be renamed
     * with it's new purpose.
     */
    @Test
    @DisplayName("Demonstration of spying")
    @Disabled("This test is unreliable. It fails sometimes, for a reason that is hard to understand.")
    void demonstrationOfSpying() throws StreamPlayerException {

        // By using a mocked logger instead of a real one, we get rid of annoying logging messages in the unit test.
        final Logger logger = mock(Logger.class);

        final File audioFile = new File("Logic - Ballin [Bass Boosted].mp3");

        // Setup the spy
        final StreamPlayer streamPlayer = new StreamPlayer(logger);
        final StreamPlayer spy = spy(streamPlayer);

        // Execute & verify

        // Call open, via the spy
        spy.open(audioFile);

        // verify that getEncodedStreamPosition is called exactly two times
        verify(spy, times(2)).getEncodedStreamPosition();

        // Call play, via the spy
        spy.play();

        // Verify that getEncodedStreamPosition is now called 3 times (the 2 previous times + one more time)
        verify(spy, times(3)).getEncodedStreamPosition();

        spy.stop();
        // Verify that there are in total 4 calls of getEncodedStreamPosition after the player is stopped.
        verify(spy, times(4)).getEncodedStreamPosition();

        // We can only spy on public methods.
        // TODO: Look into initAudioInputStream, and check if we really need to call getEncodedStreamPosition() twice.
    }
}