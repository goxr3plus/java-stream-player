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
        // We can't allow nulls in the list of listeners, because they will cause NullPointerExceptions.
        // One way to handle it is to require that an exception is thrown immediately when we
        // try to add the null.
        assertThrows(NullPointerException.class, () -> player.addStreamPlayerListener(null));

        // An alternative way would be to use some kind of null annotation, to disallow
        // nulls being passed at compile time.
    }


    @Test
    @DisplayName("When play() is called without first calling open(), an exception is thrown")
    void playingUnopenedSourceThrowsException() {

        assertThrows(Exception.class, () -> player.play());
    }

}
