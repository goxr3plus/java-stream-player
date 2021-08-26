package com.goxr3plus.streamplayer.application;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

import java.io.File;
import java.util.Map;

/**
 * @author GOXR3PLUS
 *
 */
public class DemoApplication extends StreamPlayer implements StreamPlayerListener {

	private static final String AUDIO_FILE_NAME = "Logic - Ballin [Bass Boosted].mp3";


	void start() {
		try {

			// Register to the Listeners
			addStreamPlayerListener(this);

			// Open a File
			// open(new File("...")) //..Here must be the file absolute path
			// open(INPUTSTREAM)
			// open(AUDIOURL)

			// Example
			open(new File(AUDIO_FILE_NAME));

			//Seek by bytes
			//seekBytes(500000L);

			//Seek +x seconds starting from the current position
			seekSeconds(15); // forward 15 seconds
			seekSeconds(15); // forward 15 seconds again

			/* Seek starting from the begginning of the audio */
			//seekTo(200);

			// Play it
			play();
			//pause();

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void opened(final Object dataSource, final Map<String, Object> properties) {

	}

	@Override
	public void progress(final int nEncodedBytes, final long microsecondPosition, final byte[] pcmData,final Map<String, Object> properties) {

//		System.out.println("Encoded Bytes : " + nEncodedBytes);

		// Current time position in seconds:) by GOXR3PLUS STUDIO
		// This is not the more precise way ...
		// in XR3Player i am using different techniques .
		//https://github.com/goxr3plus/XR3Player
		// Just for demostration purposes :)
		// I will add more advanced techniques with milliseconds , microseconds , hours
		// and minutes soon

		// .MP3 OR .WAV
		//THE SAMPLE Audio i am using is .MP3 SO ... :)
		String extension = getExtension(AUDIO_FILE_NAME);


		long totalBytes = getTotalBytes();
		if ("mp3".equals(extension) || "wav".equals(extension)) {

			// Calculate the progress until now
			double progress = (nEncodedBytes > 0 && totalBytes > 0)
				? (nEncodedBytes * 1.0f / totalBytes * 1.0f)
				: -1.0f;


			System.out.println("Seconds  : " + (int) (microsecondPosition / 1000000) + " s " + "Progress: [ " + progress * 100 + " ] %");


			// .WHATEVER MUSIC FILE*
		}


	}

	private String getExtension(String audioFileName) {
		return audioFileName.split("\\.(?=[^.]+$)")[1];
	}

	@Override
	public void statusUpdated(final StreamPlayerEvent streamPlayerEvent) {

		// Player status
		final Status status = streamPlayerEvent.getPlayerStatus();

		// Do different things depending on the status.
		// See XR3PLAYER https://github.com/goxr3plus/XR3Player for advanced examples
	}

	public static void main(final String[] args) {
		new DemoApplication();
	}

}
