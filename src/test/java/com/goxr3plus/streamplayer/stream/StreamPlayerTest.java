package com.goxr3plus.streamplayer.stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StreamPlayerTest {

    @Test
    @DisplayName("Demonstration of spying")
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