# Java-stream-player
Java Audio Controller Library with (seek,start,stop,pause,play,restart features)

This is a continuation and full improvement of [JavaZoom BasicPlayer](http://www.javazoom.net/jlgui/api.html)

[![Latest Version](https://img.shields.io/github/release/goxr3plus/java-stream-player.svg?style=flat-square)](https://github.com/goxr3plus/java-stream-player/releases)
[![HitCount](http://hits.dwyl.io/goxr3plus/java-stream-player.svg)](http://hits.dwyl.io/goxr3plus/java-stream-player)
<a href="https://patreon.com/preview/8adae1b75d654b2899e04a9e1111f0eb" title="Donate to this project using Patreon"><img src="https://img.shields.io/badge/patreon-donate-yellow.svg" alt="Patreon donate button" /></a>
<a href="https://www.paypal.me/GOXR3PLUSCOMPANY" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>


### What audio formats it supports?
It supports **WAV, AU, AIFF, MP3, OGG VORBIS, FLAC, MONKEY's AUDIO and SPEEX audio formats** , using some external libraries . Although more will be added in future releases.


### Step 1. Add the JitPack repository to your build file
https://jitpack.io/private#goxr3plus/java-stream-player
``` XML
<repositories>
	<repository>
	   <id>jitpack.io</id>
	   <url>https://jitpack.io</url>
        </repository>
</repositories>
```

###  Step 2. Add the dependency
``` XML
<dependency>
   <groupId>com.github.goxr3plus</groupId>
   <artifactId>java-stream-player</artifactId>
   <version>9.0.3</version>
</dependency>
```

Example usage :

``` JAVA
import java.io.File;
import java.util.Map;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;

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

			//Seek by bytes
			//seekBytes(500000L);

			//Seek +x seconds starting from the current position
			seekSeconds(15);
			seekSeconds(15);

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
		final String extension = "mp3"; //THE SAMPLE Audio i am using is .MP3 SO ... :)

		long totalBytes = getTotalBytes();
		if ("mp3".equals(extension) || "wav".equals(extension)) {

			// Calculate the progress until now
			double progress = (nEncodedBytes > 0 && totalBytes > 0)
				? (nEncodedBytes * 1.0f / totalBytes * 1.0f)
				: -1.0f;
//			System.out.println(progress*100+"%");

			System.out.println("Seconds  : " + (int) (microsecondPosition / 1000000) + " s " + "Progress: [ " + progress * 100 + " ] %");


			// .WHATEVER MUSIC FILE*
		} else {
			//System.out.println("Current time is : " + (int) (microsecondPosition / 1000000) + " seconds");
		}


	}

	@Override
	public void statusUpdated(final StreamPlayerEvent streamPlayerEvent) {

		// Player status
		final Status status = streamPlayerEvent.getPlayerStatus();
		//System.out.println(streamPlayerEvent.getPlayerStatus());

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
```


---

### Looking for a ffmpeg wrapper in Java ?
> Check this -> [jave2](https://github.com/a-schild/jave2) , currently updating it :)

---

### Originally being developed for [XR3Player Application](https://github.com/goxr3plus/XR3Player)

Are you curious on how to make spectrum analysers in Java? Well the below tutorials plus the above code are the solution :)

I hope you enjoy these tutorials and actually you can watch the youtube videos about them below:


[![Java Spectrum Analyser Tutorial](http://img.youtube.com/vi/lwlioga8Row/0.jpg)](https://www.youtube.com/watch?v=lwlioga8Row)

