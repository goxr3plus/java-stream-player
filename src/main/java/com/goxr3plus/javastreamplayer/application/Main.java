package com.goxr3plus.javastreamplayer.application;
/**
 *
 */

import java.io.File;
import java.util.Map;

import com.goxr3plus.javastreamplayer.stream.Status;
import com.goxr3plus.javastreamplayer.stream.StreamPlayer;
import com.goxr3plus.javastreamplayer.stream.StreamPlayerListener;
import com.goxr3plus.javastreamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.javastreamplayer.stream.StreamPlayerException;

/**
 * @author GOXR3PLUS
 *
 */
public class Main extends StreamPlayer implements StreamPlayerListener {

	private final String audioAbsolutePath = "Logic - Ballin [Bass Boosted].mp3";

	/**
	 * Constructor
	 */
	public Main() {

		try {

			// Register to the Listeners
			addStreamPlayerListener(this);

			// Open a File
			// open(new File("...")) //..Here must be the file absolute path
			// open(INPUTSTREAM)
			// open(AUDIOURL)

			// Example
			open(new File(audioAbsolutePath));
			// Play it
			play();

		} catch (final StreamPlayerException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void opened(final Object dataSource, final Map<String, Object> properties) {

	}

	@Override
	public void progress(final int nEncodedBytes, final long microsecondPosition, final byte[] pcmData,
		final Map<String, Object> properties) {

		System.out.println("Encoded Bytes : " + nEncodedBytes);

		// Current time position in seconds:) by GOXR3PLUS STUDIO
		// This is not the more precise way ...
		// in XR3Player i am using different techniques .
		//https://github.com/goxr3plus/XR3Player
		// Just for demostration purposes :)
		// I will add more advanced techniques with milliseconds , microseconds , hours
		// and minutes soon

		// .MP3 OR .WAV
		final String extension = "mp3"; //THE SAMPLE Audio i am using is .MP3 SO ... :)

		long totalBytes = getTotalBytes();
		if ("mp3".equals(extension) || "wav".equals(extension)) {

			// Calculate the progress until now
			double progress = (nEncodedBytes > 0 && totalBytes > 0)
				? (nEncodedBytes * 1.0f / totalBytes * 1.0f)
				: -1.0f;
			// System.out.println(progress*100+"%")

			System.out.println("Seconds  : " + (int) (microsecondPosition / 1000000) + " s " + "Progress: [ " + progress * 100 + " ] %");
			System.out.println();

			// .WHATEVER MUSIC FILE*
		} else
			System.out.println("Current time is : " + (int) (microsecondPosition / 1000000) + " seconds");

	}

	@Override
	public void statusUpdated(final StreamPlayerEvent streamPlayerEvent) {

		// Player status
		final Status status = streamPlayerEvent.getPlayerStatus();
		System.out.println(streamPlayerEvent.getPlayerStatus());

		//Examples

		if (status == Status.OPENED) {

		} else if (status == Status.OPENING) {

		} else if (status == Status.RESUMED) {

		} else if (status == Status.PLAYING) {

		} else if (status == Status.STOPPED) {

		} else if (status == Status.SEEKING) {

		} else if (status == Status.SEEKED) {

		}

		//etc... SEE XR3PLAYER https://github.com/goxr3plus/XR3Player for advanced examples
	}

	public static void main(final String[] args) {
		new Main();
	}

}
