package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
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
public class StreamPlayerFutureImprovementTest {
    StreamPlayer player;
    private File audioFile;

    @BeforeEach
    void setup() {
        final Logger logger = mock(Logger.class);
        player = new StreamPlayer(logger);
        audioFile = new File("Logic - Ballin [Bass Boosted].mp3");
    }

    /**
     * This test fails if it's permitted to add a null to the StreamPlayer listener list.
     */
    @Test
    void addStreamPlayerListener_dontAcceptNull() {
        // Currently, we can add a null to the list of stream player listeners.
        // Should that really be allowed?
        assertThrows(Exception.class, () -> player.addStreamPlayerListener(null));

        fail("Test not done");
    }


    @Test
    @DisplayName("When play() is called without first calling open(), an exception is thrown")
    void playingUnopenedSourceThrowsException() {

        assertThrows(Exception.class, () -> player.play());
    }

    @Test
    void seekBytes() throws StreamPlayerException {
        player.open(audioFile);
        player.play();
        int positionByte1 = player.getPositionByte();

        player.seekBytes(100);
        int positionByte2 = player.getPositionByte();

        assertTrue( positionByte2 > positionByte1);

        // TODO: It seems that getPositionByte doesn't work.
        //  It isn't called from within this project, except for in this test.
        //  It is however called by XR3Player. If XR3Player needs this method, it must be tested
        //  within this project. The method relies on a map, which doesn't seem to be updated by play()
    }

}
