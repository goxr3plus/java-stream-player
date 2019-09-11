package com.goxr3plus.streamplayer.application;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerInterface;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

import java.util.Map;

class AnotherStreamPlayerListener implements StreamPlayerListener {

    private final String audioFileName;
    private StreamPlayerInterface streamPlayer;


    public AnotherStreamPlayerListener(String audioFileName, StreamPlayerInterface streamPlayer) {
        this.audioFileName = audioFileName;
        this.streamPlayer = streamPlayer;
    }

    /**
     * It is called when the StreamPlayer open(Object object) method is called.
     *
     * @param dataSource the data source
     * @param properties the properties
     */
    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {
        System.out.println("The StreamPlayer was opened.");
    }

    /**
     * Is called several times per second when StreamPlayer run method is
     * running.
     *
     * @param nEncodedBytes       the n encoded bytes
     * @param microsecondPosition the microsecond position
     * @param pcmData             the pcm data
     * @param properties          the properties
     */
    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {

        String extension = getExtension(audioFileName);


        long totalBytes = streamPlayer.getTotalBytes();
        if ("mp3".equals(extension) || "wav".equals(extension)) {

            // Calculate the progress until now
            double progress = (nEncodedBytes > 0 && totalBytes > 0)
                    ? ((double) nEncodedBytes  / (double)totalBytes )
                    : -1.0d;

            // TODO: Understand why the nEncodedBytes doesn't update each call of progress.

            System.out.println("Seconds  : " + (int) (microsecondPosition / 1000000) + " s " + "Progress: [ " + progress * 100 + " ] %");
            final String message = String.format("Time: %.1f s, Progress: %.2f %%, encoded %d of %d bytes.",
                    microsecondPosition / 1000000d,
                    progress * 100d,
                    nEncodedBytes,
                    totalBytes);
            System.out.println(message);
        }


    }

    /**
     * Is called every time the status of the StreamPlayer changes.
     *
     * @param event the event
     */
    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        // Player status
        final Status status = event.getPlayerStatus();

        // Do different things depending on the status.
        // See XR3PLAYER https://github.com/goxr3plus/XR3Player for advanced examples

    }

    private String getExtension(String audioFileName) {
        return audioFileName.split("\\.(?=[^.]+$)")[1];
    }

}
