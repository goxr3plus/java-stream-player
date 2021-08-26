package com.goxr3plus.streamplayer.application;

import com.goxr3plus.streamplayer.stream.StreamPlayerInterface;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

import java.io.File;

/**
 * @author GOXR3PLUS
 *
 */
public class AnotherDemoApplication  {

	private static final String AUDIO_FILE_NAME = "Logic - Ballin [Bass Boosted].mp3";

	private final StreamPlayerInterface streamPlayer;
	private final StreamPlayerListener listener;

	public AnotherDemoApplication(StreamPlayerInterface streamPlayer) {
		this.streamPlayer = streamPlayer;
		this.listener = new AnotherStreamPlayerListener(AUDIO_FILE_NAME, streamPlayer);

	}


	void start() {
		try {

			// Register to the Listeners
		 	streamPlayer.addStreamPlayerListener(listener);

			// Open a File
			// open(new File("...")) //..Here must be the file absolute path
			// open(INPUTSTREAM)
			// open(AUDIOURL)

			// Example
			streamPlayer.open(new File(AUDIO_FILE_NAME));

			//Seek by bytes
			//seekBytes(500000L);

			//Seek +x seconds starting from the current position
			streamPlayer.seekSeconds(15); // forward 15 seconds
			streamPlayer.seekSeconds(15); // forward 15 seconds again

			/* Seek starting from the begginning of the audio */
			//seekTo(200);

			// Play it
			streamPlayer.play();
			//pause();

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}




	private String getExtension(String audioFileName) {
		return audioFileName.split("\\.(?=[^.]+$)")[1];
	}


//	public static void main(final String[] args) {
//		new AnotherDemoApplication();
//	}

}
