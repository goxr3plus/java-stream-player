package com.goxr3plus.streamplayer.application;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerInterface;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

import java.io.File;
import java.util.Map;

/**
 * @author GOXR3PLUS
 *
 */
public class AnotherDemoApplication  {

	private final String audioFileName = "Logic - Ballin [Bass Boosted].mp3";

	private StreamPlayerInterface streamPlayer;
	private StreamPlayerListener listener;

	public AnotherDemoApplication(StreamPlayerInterface streamPlayer) {
		this.streamPlayer = streamPlayer;
		this.listener = new AnotherStreamPlayerListener(audioFileName, streamPlayer);

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
			streamPlayer.open(new File(audioFileName));

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
