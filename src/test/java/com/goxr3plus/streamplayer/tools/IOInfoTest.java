package com.goxr3plus.streamplayer.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IOInfoTest {

    @ParameterizedTest
    @CsvSource({
            "/home/myself/player/files/someFile.JpEg, jpeg",
            "c:\\Users\\myself\\git\\someFile.JaVa, java"
    })
    @DisplayName("It returns the extension of the file, in lowercase")
    void itReturnsExtensionAsLowercase(String fullFilePath, String expectedExtension) {
        assertEquals(expectedExtension, IOInfo.getFileExtension(fullFilePath));
    }

    @ParameterizedTest
    @CsvSource({
            "/home/myself/player/files/someFile.JpEg, someFile.JpEg",
            "c:\\Users\\myself\\git\\someFile.JaVa, someFile.JaVa"
    })
    @DisplayName("It returns the filename from an absolute path")
    void itReturnsTheFilenameFromAbsolutePath(String absolutePath, String expectedName) {
        assertEquals(expectedName, IOInfo.getFileName(absolutePath));
    }

    @ParameterizedTest
    @CsvSource({
            "/home/myself/player/files/someFile.JpEg, someFile",
            "c:\\Users\\myself\\git\\someFile.JaVa, someFile"
    })
    @DisplayName("It returns the filename from an absolute path")
    void itReturnsTheBaseNameFromAbsolutePath(String absolutePath, String expectedName) {
        assertEquals(expectedName, IOInfo.getFileTitle(absolutePath));
    }
}