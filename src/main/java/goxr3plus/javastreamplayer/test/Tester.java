package main.java.goxr3plus.javastreamplayer.test;
/**
 * 
 */

import java.io.File;
import java.util.Map;

import main.java.goxr3plus.javastreamplayer.stream.StreamPlayer;
import main.java.goxr3plus.javastreamplayer.stream.StreamPlayerEvent;
import main.java.goxr3plus.javastreamplayer.stream.StreamPlayerException;
import main.java.goxr3plus.javastreamplayer.stream.StreamPlayerListener;

/**
 * @author GOXR3PLUS
 *
 */
public class Tester extends StreamPlayer implements StreamPlayerListener {
	
	/**
	 * Constructor
	 */
	public Tester() {
		
		try {
			
			// Register to the Listeners
			addStreamPlayerListener(this);
			
			// Open a File
			//open(new File("...")) //..Here must be the file absolute path 
			//open(INPUTSTREAM)
			//open(AUDIOURL)
			
			//Example
			open(new File("C:\\Users\\GOXR3PLUS\\Desktop\\10 Heavy BBOY Tracks That Will Give You More Energy and Power!.mp3")); //..Here must be the file absolute path 
			
			// Play it
			play();
			
		} catch (StreamPlayerException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see streamplayer.StreamPlayerListener#opened(java.lang.Object,
	 * java.util.Map)
	 */
	@Override
	public void opened(Object dataSource , Map<String,Object> properties) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see streamplayer.StreamPlayerListener#progress(int, long, byte[],
	 * java.util.Map)
	 */
	@Override
	public void progress(int nEncodedBytes , long microsecondPosition , byte[] pcmData , Map<String,Object> properties) {
		
		System.out.println("Encoded Bytes : " + nEncodedBytes);
		
		//Current time position in seconds:) by GOXR3PLUS STUDIO
		//This is not the more precise way ... in XR3Player i am using different techniques .
		//Just for demostration purposes :)
		//I will add more advanced techniques with milliseconds , microseconds , hours and minutes soon
		System.out.println("Current time is : " + (int) ( microsecondPosition / 1000000 ) + " seconds");		
	}
	
	/*
	 * (non-Javadoc)
	 * @see streamplayer.StreamPlayerListener#statusUpdated(streamplayer.
	 * StreamPlayerEvent)
	 */
	@Override
	public void statusUpdated(StreamPlayerEvent event) {
		System.out.println(event.getPlayerStatus());
	}
	
	public static void main(String[] args) {
		new Tester();
	}
	
}
