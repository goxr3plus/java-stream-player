package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.configuration.IMockitoConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StreamPlayerEventTest {

    private StreamPlayer source;
    private Object description;
    private Status status;
    private int encodededStreamPosition;
    private StreamPlayerEvent event;

    @BeforeEach
    void setUp() {
        description = new Object();
        source = mock(StreamPlayer.class);
        status = Status.RESUMED;
        encodededStreamPosition = 12345;
        event = new StreamPlayerEvent(source, status, encodededStreamPosition, description);
    }

    @Test
    void itReturnsTheStatus() {
        assertEquals(status, event.getPlayerStatus());
    }

    @Test
    void itReturnsTheEncodedStreamPosition() {
        assertEquals(encodededStreamPosition, event.getEncodedStreamPosition());
    }

    @Test
    void itReturnsTheSource() {
        assertSame(source, event.getSource());
    }

    @Test
    void itReturnsTheDescription() {
        assertSame(description, event.getDescription());
    }

    @Test
    void itReturnsAString() {
        final String actual = event.toString();
        final String expected = "Source :="
                + source.toString()
                + " , Player Status := RESUMED , EncodedStreamPosition :=12345 , Description :="
                + description.toString();
        assertEquals(expected, actual);
    }


}