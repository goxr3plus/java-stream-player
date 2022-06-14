package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class StreamPlayerEventTest {

    private Object description;
    private Status status;
    private int encodedStreamPosition;
    private StreamPlayerEvent event;

    @BeforeEach
    void setUp() {
        description = new Object();
        status = Status.RESUMED;
        encodedStreamPosition = 12345;
        event = new StreamPlayerEvent(status, encodedStreamPosition, description);
    }

    @Test
    void itReturnsTheStatus() {
        assertEquals(status, event.getPlayerStatus());
    }

    @Test
    void itReturnsTheEncodedStreamPosition() {
        assertEquals(encodedStreamPosition, event.getEncodedStreamPosition());
    }

    @Test
    void itReturnsTheDescription() {
        assertSame(description, event.getDescription());
    }

    @Test
    void itReturnsAString() {
        final String actual = event.toString();
        final String expected = "Player Status := RESUMED , EncodedStreamPosition :=12345 , Description :="
                + description.toString();
        assertEquals(expected, actual);
    }
}