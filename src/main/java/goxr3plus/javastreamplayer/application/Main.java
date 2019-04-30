package goxr3plus.javastreamplayer.application;
/**
 * 
 */

import java.io.File;
import java.util.Map;

import goxr3plus.javastreamplayer.stream.StreamPlayer;
import goxr3plus.javastreamplayer.stream.StreamPlayerEvent;
import goxr3plus.javastreamplayer.stream.StreamPlayerException;
import goxr3plus.javastreamplayer.stream.StreamPlayerListener;

/**
 * @author GOXR3PLUS
 *
 */
public class Main extends StreamPlayer implements StreamPlayerListener {

	private  final String audioAbsolutePath = "Logic - Ballin [Bass Boosted].mp3";

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see streamplayer.StreamPlayerListener#opened(java.lang.Object,
	 * java.util.Map)
	 */
	@Override
	public void opened(final Object dataSource, final Map<String, Object> properties) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see streamplayer.StreamPlayerListener#progress(int, long, byte[],
	 * java.util.Map)
	 */
	@Override
	public void progress(final int nEncodedBytes, final long microsecondPosition, final byte[] pcmData,
			final Map<String, Object> properties) {

		System.out.println("Encoded Bytes : " + nEncodedBytes);

		// Current time position in seconds:) by GOXR3PLUS STUDIO
		// This is not the more precise way ... in XR3Player i am using different
		// techniques .
		// Just for demostration purposes :)
		// I will add more advanced techniques with milliseconds , microseconds , hours
		// and minutes soon
		System.out.println("Current time is : " + (int) (microsecondPosition / 1000000) + " seconds");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see streamplayer.StreamPlayerListener#statusUpdated(streamplayer.
	 * StreamPlayerEvent)
	 */
	@Override
	public void statusUpdated(final StreamPlayerEvent event) {
		System.out.println(event.getPlayerStatus());
	}

	public static void main(final String[] args) {
		new Main();
	}

}
