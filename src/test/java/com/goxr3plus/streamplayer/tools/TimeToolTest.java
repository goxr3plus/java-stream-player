package com.goxr3plus.streamplayer.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimeToolTest {

    /**
     * @return a stream of arguments for the test. Each argument have the same form.
     *         They can contain any type of objects.
     */
    private static Stream<Arguments> timesForTest() {
        return Stream.of(
                Arguments.of(0, "00:00", "00s"),
                Arguments.of(3661, "61:01", "01h:01m:01")
        );
    }

    @ParameterizedTest
    @MethodSource("timesForTest")
    void getTimeEditedOnHours(int seconds, String expectedOnHours, String ignored) {
        final String actual = TimeTool.getTimeEditedOnHours(seconds);
        assertEquals(expectedOnHours, actual);
    }

    @ParameterizedTest
    @MethodSource("timesForTest")
    void getTimeEdited(int seconds, String ignored, String timeEdited) {
        final String actual = TimeTool.getTimeEdited(seconds);
        assertEquals(timeEdited, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "0, '.0' ",
            "1, '.0' ",
            "999, '.9' ",
            "1001, '.0' ",  // Slightly over 1 second  TODO: check if this is a bug,
            "600001, '.0' " // Slightly over 10 minutes TODO: check if this is a bug
    })
    void millisecondsToTime(long millis, String expected) {
        final String actual = TimeTool.millisecondsToTime(millis);
        assertEquals(expected, actual);

    }

    @Test
    void durationInSeconds() {
        final int duration = TimeTool.durationInSeconds("aName", null);
        assertEquals(-1, duration);
        // TODO: Wouldn't it be better if an exception was thrown?
    }

    @Test
    void durationInMilliseconds() {
        final long duration = TimeTool.durationInMilliseconds("aName", null);
        assertEquals(-1L, duration);
        // TODO: Wouldn't it be better if an exception was thrown?
    }
}