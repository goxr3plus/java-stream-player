/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>. Also(warning!): 1)You are not allowed to sell this product to third party. 2)You can't change license and made it
 * like you are the owner,author etc. 3)All redistributions of source code files must contain all copyright notices that are currently in this file,
 * and this list of conditions without modification.
 */

package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayerException.PlayerException;
import javazoom.spi.PropertiesContainer;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.naming.OperationNotSupportedException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StreamPlayer is a class based on JavaSound API. It has been successfully tested under Java 10
 *
 * @author GOXR3PLUS (www.goxr3plus.co.nf)
 * @author JavaZOOM (www.javazoom.net)
 */
public class StreamPlayer implements StreamPlayerInterface, Callable<Void> {

	/**
	 * Class logger
	 */
	private Logger logger;

	// -------------------AUDIO----------------,-----

	private volatile Status status = Status.NOT_SPECIFIED;

	/**
	 * The data source
	 */
	private DataSource source;

	/** The audio input stream. */
	private volatile AudioInputStream audioInputStream;

	/** The encoded audio input stream. */
	private AudioInputStream encodedAudioInputStream;

	/** The audio file format. */
	private AudioFileFormat audioFileFormat;

	// -------------------LOCKS---------------------

	/**
	 * It is used for synchronization in place of audioInputStream
	 */
	private final Object audioLock = new Object();

	// -------------------VARIABLES---------------------

	private String mixerName;

	/** The current line buffer size. */
	private int currentLineBufferSize = -1;

	/** The line buffer size. */
	private int lineBufferSize = -1;

	/** The encoded audio length. */
	private int encodedAudioLength = -1;

	/**
	 * Speed Factor of the Audio
	 */
	private double speedFactor = 1;

	/** The Constant EXTERNAL_BUFFER_SIZE. */
	private static final int EXTERNAL_BUFFER_SIZE = 4096;

	/** The Constant SKIP_INACCURACY_SIZE. */
	// private static final int SKIP_INACCURACY_SIZE = 1200

	byte[] trimBuffer;

	// -------------------CLASSES---------------------

	/**
	 * This is starting a Thread for StreamPlayer to Run
	 */
	private final ExecutorService streamPlayerExecutorService;
	private Future<Void> future;


	/** Holds a list of Linteners to be notified about Stream PlayerEvents */
	private final ArrayList<StreamPlayerListener> listeners;

	/** The empty map. */
	private final Map<String, Object> emptyMap = new HashMap<>();

	// Properties when the File/URL/InputStream is opened.
	Map<String, Object> audioProperties;

	/**
	 * Responsible for the output SourceDataLine and the controls that depend on it.
	 */
	private Outlet outlet;

	/**
	 * Default parameter less Constructor. A default logger will be used.
	 */
	public StreamPlayer() {
		this(Logger.getLogger(StreamPlayer.class.getName()));

	}

	/**
	 * Constructor with a logger.
	 * @param logger The logger that will be used by the player
	 */
	public StreamPlayer(Logger logger) {
		this(logger,
				Executors.newSingleThreadExecutor(new ThreadFactoryWithNamePrefix("StreamPlayer")));
	}

	/**
	 * Constructor with settable logger and executor services.
	 * @param logger The logger that will be used by the player
	 * @param streamPlayerExecutorService Executor service for the stream player
	 */
	public StreamPlayer(Logger logger, ExecutorService streamPlayerExecutorService) {
		this.logger = logger;
		this.streamPlayerExecutorService = streamPlayerExecutorService;
		listeners = new ArrayList<>();
		outlet = new Outlet(logger);
		reset();
	}

	/**
	 * Freeing the resources.
	 */
	@Override
	public void reset() {

		// Close the stream
		synchronized (audioLock) {
			closeStream();
		}

		outlet.flushAndFreeDataLine();

		// AudioFile
		audioInputStream = null;
		audioFileFormat = null;
		encodedAudioInputStream = null;
		encodedAudioLength = -1;

		// Controls
		outlet.setGainControl(null);
		outlet.setPanControl(null);
		outlet.setBalanceControl(null);

		// Notify the Status
		status = Status.NOT_SPECIFIED;
		generateEvent(Status.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED, null);

	}

	/**
	 * Notify listeners about a BasicPlayerEvent.
	 *
	 * @param status event code.
	 * @param encodedStreamPosition in the stream when the event occurs.
	 * @param description the description
	 *
	 */
	private void  generateEvent(final Status status, final int encodedStreamPosition, final Object description) {
		new StreamPlayerEventLauncher(this, status, encodedStreamPosition, description, listeners).call();

	}

	/**
	 * Add a listener to be notified.
	 *
	 * @param streamPlayerListener the listener
	 */
	@Override
	public void addStreamPlayerListener(final StreamPlayerListener streamPlayerListener) {
		listeners.add(streamPlayerListener);
	}

	/**
	 * Remove registered listener.
	 *
	 * @param streamPlayerListener the listener
	 */
	@Override
	public void removeStreamPlayerListener(final StreamPlayerListener streamPlayerListener) {
		if (listeners != null)
			listeners.remove(streamPlayerListener);

	}

	/**
	 * Open the specific object which can be File,URL or InputStream.
	 *
	 * @param object the object [File or URL or InputStream ]
	 *
	 * @throws StreamPlayerException the stream player exception
	 * @deprecated Use one of {@link #open(File)}, {@link #open(URL)} or {@link #open(InputStream)} instead.
	 *
	 */
	@Override
	@Deprecated
	public void open(final Object object) throws StreamPlayerException {

		logger.info(() -> "open(" + object + ")\n");
		if (object == null)
			return;

		try {
			source = DataSource.newDataSource(object);
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
		}
		initAudioInputStream();
	}

	/**
	 * Open the specified file for playback.
	 *
	 * @param file the file to be played
	 * @throws StreamPlayerException the stream player exception
	 */
	@Override
	public void open(File file) throws StreamPlayerException {

		logger.info(() -> "open(" + file + ")\n");
		source = new FileDataSource(file);
		initAudioInputStream();
	}

	/**
	 * Open the specified location for playback.
	 *
	 * @param url the location to be played
	 * @throws StreamPlayerException the stream player exception
	 */
	@Override
	public void open(URL url) throws StreamPlayerException {
		logger.info(() -> "open(" + url + ")\n");
		source = new UrlDataSource(url);
		initAudioInputStream();
	}

	/**
	 * Open the specified stream for playback.
	 *
	 * @param stream the stream to be played
	 * @throws StreamPlayerException the stream player exception
	 */
	@Override
	public void open(InputStream stream) throws StreamPlayerException {
		logger.info(() -> "open(" + stream + ")\n");
		source = new StreamDataSource(stream);
		initAudioInputStream();
	}

	/**
	 * Create AudioInputStream and AudioFileFormat from the data source.
	 *
	 * @throws StreamPlayerException the stream player exception
	 */
	private void initAudioInputStream() throws StreamPlayerException {
		try {

			logger.info("Entered initAudioInputStream\n");

			// Reset
			reset();

			// Notify Status
			status = Status.OPENING;
			generateEvent(Status.OPENING, getEncodedStreamPosition(), source);

			// Audio resources from file||URL||inputStream.
			audioInputStream = source.getAudioInputStream();

			// Audio resources from file||URL||inputStream.
			audioFileFormat = source.getAudioFileFormat();

			// Create the Line
			createLine();

			// Determine Properties
			determineProperties();

			// Generate Open Event
			status = Status.OPENED;
			generateEvent(Status.OPENED, getEncodedStreamPosition(), null);

		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			logger.log(Level.INFO, e.getMessage(), e);
			throw new StreamPlayerException(e);
		}

		logger.info("Exited initAudioInputStream\n");
	}


	/**
	 * Determines Properties when the File/URL/InputStream is opened.
	 */
	private void determineProperties() {
		logger.info("Entered determineProperties()!\n");

		// Add AudioFileFormat properties.
		// Expect if it is null(something bad happened).
		if (audioFileFormat == null)
			return;

		if (!(audioFileFormat instanceof TAudioFileFormat))
			audioProperties = new HashMap<>();
		else {
			// Tritonus SPI compliant audio file format.
			audioProperties = audioFileFormat.properties();

			// Clone the Map because it is not mutable.
			audioProperties = deepCopy(audioProperties);

		}

		// Add JavaSound properties.
		if (audioFileFormat.getByteLength() > 0)
			audioProperties.put("audio.length.bytes", audioFileFormat.getByteLength());
		if (audioFileFormat.getFrameLength() > 0)
			audioProperties.put("audio.length.frames", audioFileFormat.getFrameLength());
		if (audioFileFormat.getType() != null)
			audioProperties.put("audio.type", audioFileFormat.getType());

		// AudioFormat properties.
		final AudioFormat audioFormat = audioFileFormat.getFormat();
		if (audioFormat.getFrameRate() > 0)
			audioProperties.put("audio.framerate.fps", audioFormat.getFrameRate());
		if (audioFormat.getFrameSize() > 0)
			audioProperties.put("audio.framesize.bytes", audioFormat.getFrameSize());
		if (audioFormat.getSampleRate() > 0)
			audioProperties.put("audio.samplerate.hz", audioFormat.getSampleRate());
		if (audioFormat.getSampleSizeInBits() > 0)
			audioProperties.put("audio.samplesize.bits", audioFormat.getSampleSizeInBits());
		if (audioFormat.getChannels() > 0)
			audioProperties.put("audio.channels", audioFormat.getChannels());
		// Tritonus SPI compliant audio format.
		if (audioFormat instanceof TAudioFormat)
			audioProperties.putAll(audioFormat.properties());

		// Add SourceDataLine
		audioProperties.put("basicplayer.sourcedataline", outlet.getSourceDataLine());

		// Keep this final reference for the lambda expression
		final Map<String, Object> audioPropertiesCopy = audioProperties; // TODO: Remove, it's meaningless.

		// Notify all registered StreamPlayerListeners
		listeners.forEach(listener -> listener.opened(source.getSource(), audioPropertiesCopy));

		logger.info("Exited determineProperties()!\n");

	}

	/**
	 * Initiating Audio resources from AudioSystem.<br>
	 *
	 * @throws LineUnavailableException the line unavailable exception
	 * @throws StreamPlayerException
	 */
	private void initLine() throws LineUnavailableException, StreamPlayerException {

		logger.info("Initiating the line...");

		if (outlet.getSourceDataLine() == null)
			createLine();
		if (!outlet.getSourceDataLine().isOpen()) {
			currentLineBufferSize = lineBufferSize >= 0 ? lineBufferSize : outlet.getSourceDataLine().getBufferSize();
			openLine(audioInputStream.getFormat(), currentLineBufferSize);
		} else {
			AudioFormat format = audioInputStream == null ? null : audioInputStream.getFormat();
			if (!outlet.getSourceDataLine().getFormat().equals(format)) { // TODO: Check if bug, does equals work as intended?
				outlet.getSourceDataLine().close();
				currentLineBufferSize = lineBufferSize >= 0 ? lineBufferSize : outlet.getSourceDataLine().getBufferSize();
				openLine(audioInputStream.getFormat(), currentLineBufferSize);
			}
		}
	}

	/** The frame size. */
	// private int frameSize

	/**
	 * Change the Speed Rate of the Audio , this variable affects the Sample Rate ,
	 * for example 1.0 is normal , 0.5 is half the speed and 2.0 is double the speed
	 * Note that you have to restart the audio for this to take effect
	 *
	 * @param speedFactor speedFactor
	 */
	@Override
	public void setSpeedFactor(final double speedFactor) {
		this.speedFactor = speedFactor;

	}

	/**
	 * Inits a DateLine.<br>
	 * <p>
	 * From the AudioInputStream, i.e. from the sound file, we fetch information
	 * about the format of the audio data. These information include the sampling
	 * frequency, the number of channels and the size of the samples. There
	 * information are needed to ask JavaSound for a suitable output line for this
	 * audio file. Furthermore, we have to give JavaSound a hint about how big the
	 * internal buffer for the line should be. Here, we say
	 * AudioSystem.NOT_SPECIFIED, signaling that we don't care about the exact size.
	 * JavaSound will use some default value for the buffer size.
	 *
	 * @throws LineUnavailableException the line unavailable exception
	 * @throws StreamPlayerException
	 */
	private void createLine() throws LineUnavailableException, StreamPlayerException {

		logger.info("Entered CreateLine()!:\n");

		if (outlet.getSourceDataLine() != null)
			logger.warning("Warning Source DataLine is not null!\n");
		else {
			final AudioFormat sourceFormat = audioInputStream.getFormat();

			logger.info(() -> "Create Line : Source format : " + sourceFormat + "\n");

			// Calculate the Sample Size in bits
			int nSampleSizeInBits = sourceFormat.getSampleSizeInBits();
			if (sourceFormat.getEncoding() == AudioFormat.Encoding.ULAW || sourceFormat.getEncoding() == AudioFormat.Encoding.ALAW
                    || nSampleSizeInBits != 8)
				nSampleSizeInBits = 16;

			final AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				(float) (sourceFormat.getSampleRate() * speedFactor), nSampleSizeInBits, sourceFormat.getChannels(),
				nSampleSizeInBits / 8 * sourceFormat.getChannels(), sourceFormat.getSampleRate(), false);

			// int frameSize = sourceFormat.getChannels() * (nSampleSizeInBits / 8)

			logger.info(() -> "Sample Rate =" + targetFormat.getSampleRate() + ",Frame Rate="
				+ targetFormat.getFrameRate() + ",Bit Rate=" + targetFormat.getSampleSizeInBits()
				+ "Target format: " + targetFormat + "\n");

			// Keep a reference on encoded stream to progress notification.
			encodedAudioInputStream = audioInputStream;
			try {
				// Get total length in bytes of the encoded stream.
				encodedAudioLength = encodedAudioInputStream.available();
			} catch (final IOException e) {
				logger.warning("Cannot get m_encodedaudioInputStream.available()\n" + e);
			}

			// Create decoded Stream
			audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
			final DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, audioInputStream.getFormat(),
				AudioSystem.NOT_SPECIFIED);
			if (!AudioSystem.isLineSupported(lineInfo))
				throw new StreamPlayerException(PlayerException.LINE_NOT_SUPPORTED);

			// ----------About the mixer
			if (mixerName == null)
				// Primary Sound Driver
				mixerName = getMixers().get(0);

			// Continue
			final Mixer mixer = getMixer(mixerName);
			if (mixer == null) {
				outlet.setSourceDataLine((SourceDataLine) AudioSystem.getLine(lineInfo));
				mixerName = null;
			} else {
				logger.info("Mixer: " + mixer.getMixerInfo());
				outlet.setSourceDataLine((SourceDataLine) mixer.getLine(lineInfo));
			}

			outlet.setSourceDataLine((SourceDataLine) AudioSystem.getLine(lineInfo));

			// --------------------------------------------------------------------------------
			logger.info(() -> "Line : " + outlet.getSourceDataLine());
			logger.info(() -> "Line Info : " + outlet.getSourceDataLine().getLineInfo());
			logger.info(() -> "Line AudioFormat: " + outlet.getSourceDataLine().getFormat() + "\n");
			logger.info("Exited CREATELINE()!:\n");
		}
	}

	/**
	 * Open the line.
	 *
	 * @throws LineUnavailableException the line unavailable exception
	 * @param audioFormat
	 * @param currentLineBufferSize
	 */
	private void openLine(AudioFormat audioFormat, int currentLineBufferSize) throws LineUnavailableException {
		outlet.open(audioFormat, currentLineBufferSize);
	}

	/**
	 * Starts the play back.
	 *
	 * @throws StreamPlayerException the stream player exception
	 */
	@Override
	public void play() throws StreamPlayerException {
		if (status == Status.STOPPED)
			initAudioInputStream();
		if (status != Status.OPENED)
			return;

		// Shutdown previous Thread Running
		awaitTermination();

		// Open SourceDataLine.
		try {
			initLine();
		} catch (final LineUnavailableException ex) {
			throw new StreamPlayerException(PlayerException.CAN_NOT_INIT_LINE, ex);
		}

		// Open the sourceDataLine
		if (outlet.isStartable()) {
			outlet.start();

			// Proceed only if we have not problems
			logger.info("Submitting new StreamPlayer Thread");
			Future<Void> submit = streamPlayerExecutorService.submit(this);

			// Update the status
			status = Status.PLAYING;
			generateEvent(Status.PLAYING, getEncodedStreamPosition(), null);
		}
	}

	/**
	 * Pauses the play back.<br>
	 * <p>
	 * Player Status = PAUSED. * @return False if failed(so simple...)
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean pause() {
		if (outlet.getSourceDataLine() == null || status != Status.PLAYING)
			return false;
		status = Status.PAUSED;
		logger.info("pausePlayback() completed");
		generateEvent(Status.PAUSED, getEncodedStreamPosition(), null);
		return true;
	}

	/**
	 * Stops the play back.<br>
	 * <p>
	 * Player Status = STOPPED.<br>
	 * Thread should free Audio resources.
	 */
	@Override
	public void stop() {
		if (status == Status.STOPPED)
			return;
		if (isPlaying())
			pause();
		status = Status.STOPPED;
		// generateEvent(Status.STOPPED, getEncodedStreamPosition(), null);
		logger.info("StreamPlayer stopPlayback() completed");
	}

	/**
	 * Resumes the play back.<br>
	 * <p>
	 * Player Status = PLAYING*
	 *
	 * @return False if failed(so simple...)
	 */
	@Override
	public boolean resume() {
		if (outlet.getSourceDataLine() == null || status != Status.PAUSED)
			return false;
		outlet.start();
		status = Status.PLAYING;
		generateEvent(Status.RESUMED, getEncodedStreamPosition(), null);
		logger.info("resumePlayback() completed");
		return true;

	}

	/**
	 * Await for the termination of StreamPlayerExecutorService Thread
	 */
	private void awaitTermination() {
		if (future != null && !future.isDone()) {
			try {
				// future.get() [Don't use this cause it may hang forever and ever...]

				// Wait ~1 second and then cancel the future
				final Thread delay = new Thread(() -> {
					try {
						for (int i = 0; i < 50; i++) {
							if (!future.isDone())
								Thread.sleep(20);
							else
								break;
							logger.log(Level.INFO, "StreamPlayer Future is not yet done...");
						}

					} catch (final InterruptedException ex) {
						Thread.currentThread().interrupt();
						logger.log(Level.INFO, ex.getMessage(), ex);
					}
				});

				// Start the delay Thread
				delay.start();
				// Join until delay Thread is finished
				delay.join();

			} catch (final InterruptedException ex) {
				Thread.currentThread().interrupt();
				logger.log(Level.WARNING, ex.getMessage(), ex);
			} finally {
				// Harmless if task already completed
				future.cancel(true); // interrupt if running
			}
		}
	}

	/**
	 * Skip bytes in the File input stream. It will skip N frames matching to bytes,
	 * so it will never skip given bytes len
	 *
	 * @param bytes the bytes
	 *
	 * @return value bigger than 0 for File and value = 0 for URL and InputStream
	 *
	 * @throws StreamPlayerException the stream player exception
	 */
	@Override
	public long seekBytes(final long bytes) throws StreamPlayerException {
		long totalSkipped = 0;

		// If it is File
		if (source.isFile()) {

			// Check if the requested bytes are more than totalBytes of Audio
			final long bytesLength = getTotalBytes();
			logger.log(Level.INFO, "Bytes: " + bytes + " BytesLength: " + bytesLength);
			if ((bytesLength <= 0) || (bytes >= bytesLength)) {
				generateEvent(Status.EOM, getEncodedStreamPosition(), null);
				return totalSkipped;
			}

			logger.info(() -> "Bytes to skip : " + bytes);
			final Status previousStatus = status;
			status = Status.SEEKING;

			try {
				synchronized (audioLock) {
					generateEvent(Status.SEEKING, AudioSystem.NOT_SPECIFIED, null);
					initAudioInputStream();
					if (audioInputStream != null) {

						long skipped;
						// Loop until bytes are really skipped.
						while (totalSkipped < bytes) { // totalSkipped < (bytes-SKIP_INACCURACY_SIZE)))
							skipped = audioInputStream.skip(bytes - totalSkipped);
							if (skipped == 0)
								break;
							totalSkipped += skipped;
							logger.info("Skipped : " + totalSkipped + "/" + bytes);
							if (totalSkipped == -1)
								throw new StreamPlayerException(
									PlayerException.SKIP_NOT_SUPPORTED);

							logger.info("Skeeping:" + totalSkipped);
						}
					}
				}
				generateEvent(Status.SEEKED, getEncodedStreamPosition(), null);
				status = Status.OPENED;
				if (previousStatus == Status.PLAYING)
					play();
				else if (previousStatus == Status.PAUSED) {
					play();
					pause();
				}

			} catch (final IOException ex) {
				logger.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return totalSkipped;
	}

	/**
	 * Skip x seconds of audio
	 * See  {@link #seekBytes(long)}
	 *
	 * @param seconds Seconds to Skip
	 */
	@Override
	//todo not finished needs more validations
	public long seekSeconds(int seconds) throws StreamPlayerException {
		int durationInSeconds = this.getDurationInSeconds();

		//Validate
		validateSeconds(seconds, durationInSeconds);

		//Calculate Bytes
		long totalBytes = getTotalBytes();
		double percentage = (seconds * 100) / durationInSeconds;
		long bytes = (long) (totalBytes * (percentage / 100));

		return seekBytes(this.getEncodedStreamPosition() + bytes);
	}

//	/**
//	 * Skip seconds of audio based on the pattern
//	 * See  {@link #seek(long)}
//	 *
//	 * @param pattern A string in the format (HH:MM:SS) WHERE h = HOURS , M = minutes , S = seconds
//	 */
//	public void seek(String pattern) throws StreamPlayerException {
//		long bytes = 0;
//
//		seek(bytes);
//	}

	/**
	 * Go to X time of the Audio
	 * See  {@link #seekBytes(long)}
	 *
	 * @param seconds Seconds to Skip
	 */
	@Override
	public long seekTo(int seconds) throws StreamPlayerException {
		int durationInSeconds = this.getDurationInSeconds();

		//Validate
		validateSeconds(seconds, durationInSeconds);

		//Calculate Bytes
		long totalBytes = getTotalBytes();
		double percentage = (seconds * 100) / durationInSeconds;
		long bytes = (long) (totalBytes * (percentage / 100));

		return seekBytes(bytes);
	}


	private void validateSeconds(int seconds, int durationInSeconds) {
		if (seconds < 0) {
			throw new UnsupportedOperationException("Trying to skip negative seconds ");
		} else if (seconds >= durationInSeconds) {
			throw new UnsupportedOperationException("Trying to skip with seconds {" + seconds + "} > maximum {" + durationInSeconds + "}");
		}
	}


	@Override
	public int getDurationInSeconds() {
		return source.getDurationInSeconds();
	}

	/**
	 * Main loop.
	 * <p>
	 * Player Status == STOPPED || SEEKING = End of Thread + Freeing Audio
	 * Resources.<br>
	 * Player Status == PLAYING = Audio stream data sent to Audio line.<br>
	 * Player Status == PAUSED = Waiting for another status.
	 */
	@Override
	public Void call() {
		int nBytesRead = 0;
		final int audioDataLength = EXTERNAL_BUFFER_SIZE;
		final ByteBuffer audioDataBuffer = ByteBuffer.allocate(audioDataLength);
		audioDataBuffer.order(ByteOrder.LITTLE_ENDIAN);

		// Lock stream while playing.
		synchronized (audioLock) {
			// Main play/pause loop.
			while ((nBytesRead != -1) && status != Status.STOPPED && status != Status.NOT_SPECIFIED
				&& status != Status.SEEKING) {

				try {
					// Playing?
					if (status == Status.PLAYING) {

						// System.out.println("Inside Stream Player Run method")
						int toRead = audioDataLength;
						int totalRead = 0;

						// Reads up a specified maximum number of bytes from audio stream
						// wtf i have written here omg //to fix! cause it is complicated
						for (; toRead > 0 && (nBytesRead = audioInputStream.read(audioDataBuffer.array(), totalRead,
							toRead)) != -1; toRead -= nBytesRead, totalRead += nBytesRead)

							// Check for under run
							if (outlet.getSourceDataLine().available() >= outlet.getSourceDataLine().getBufferSize())
								logger.info(() -> "Underrun> Available=" + outlet.getSourceDataLine().available()
									+ " , SourceDataLineBuffer=" + outlet.getSourceDataLine().getBufferSize());

						// Check if anything has been read
						if (totalRead > 0) {
							trimBuffer = audioDataBuffer.array();
							if (totalRead < trimBuffer.length) {
								trimBuffer = new byte[totalRead];
								// Copies an array from the specified source array, beginning at the specified
								// position, to the specified position of the destination array
								// The number of components copied is equal to the length argument.
								System.arraycopy(audioDataBuffer.array(), 0, trimBuffer, 0, totalRead);
							}

							// Writes audio data to the mixer via this source data line
							outlet.getSourceDataLine().write(trimBuffer, 0, totalRead);

							// Compute position in bytes in encoded stream.
							final int nEncodedBytes = getEncodedStreamPosition();

							// Notify all registered Listeners
							listeners.forEach(listener -> {
								if (audioInputStream instanceof PropertiesContainer) {
									// Pass audio parameters such as instant
									// bit rate, ...
									listener.progress(nEncodedBytes, outlet.getSourceDataLine().getMicrosecondPosition(),
										trimBuffer, ((PropertiesContainer) audioInputStream).properties());
								} else
									// Pass audio parameters
									listener.progress(nEncodedBytes, outlet.getSourceDataLine().getMicrosecondPosition(),
										trimBuffer, emptyMap);
							});

						}

					} else if (status == Status.PAUSED) {
						// Flush and stop the source data line
						outlet.flushAndStop();
						goOutOfPause();

					}
				} catch (final IOException ex) {
					logger.log(Level.WARNING, "\"Decoder Exception: \" ", ex);
					status = Status.STOPPED;
					generateEvent(Status.STOPPED, getEncodedStreamPosition(), null);
				}
			}
			// Free audio resources.
			outlet.drainStopAndFreeDataLine();

			// Close stream.
			closeStream();

			// Notification of "End Of Media"
			if (nBytesRead == -1)
				generateEvent(Status.EOM, AudioSystem.NOT_SPECIFIED, null);

		}
		// Generate Event
		status = Status.STOPPED;
		generateEvent(Status.STOPPED, AudioSystem.NOT_SPECIFIED, null);

		// Log
		logger.info("Decoding thread completed");

		return null;
	}

	private void goOutOfPause() {
		try {
			while (status == Status.PAUSED) {
				Thread.sleep(50);
			}
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
			logger.warning("Thread cannot sleep.\n" + ex);
		}
	}

	/**
	 * Calculates the current position of the encoded audio based on <br>
	 * <b>nEncodedBytes = encodedAudioLength -
	 * encodedAudioInputStream.available();</b>
	 *
	 * @return The Position of the encoded stream in term of bytes
	 */
	@Override
	public int getEncodedStreamPosition() {
		int position = -1;
		if (source.isFile() && encodedAudioInputStream != null)
			try {
				position = encodedAudioLength - encodedAudioInputStream.available();
			} catch (final IOException ex) {
				logger.log(Level.WARNING, "Cannot get m_encodedaudioInputStream.available()", ex);
				stop();
			}
		return position;
	}

	/**
	 * Close stream.
	 */
	private void closeStream() {
		try {
			if (audioInputStream != null) {
				audioInputStream.close();
				logger.info("Stream closed");
			}
		} catch (final IOException ex) {
			logger.warning("Cannot close stream\n" + ex);
		}
	}

	/**
	 * Return SourceDataLine buffer size.
	 *
	 * @return -1 maximum buffer size.
	 */
	@Override
	public int getLineBufferSize() {
		return lineBufferSize;
	}

	/**
	 * Return SourceDataLine current buffer size.
	 *
	 * @return The current line buffer size
	 */
	@Override
	public int getLineCurrentBufferSize() {
		return currentLineBufferSize;
	}

	/**
	 * Returns all available mixers.
	 *
	 * @return A List of available Mixers
	 */
	@Override
	public List<String> getMixers() {
		final List<String> mixers = new ArrayList<>();

		// Obtains an array of mixer info objects that represents the set of
		// audio mixers that are currently installed on the system.
		final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		Arrays.stream(mixerInfos).forEach(mInfo -> {
			// line info
			final Line.Info lineInfo = new Line.Info(SourceDataLine.class);
			final Mixer mixer = AudioSystem.getMixer(mInfo);

			// if line supported
			if (mixer.isLineSupported(lineInfo))
				mixers.add(mInfo.getName());

		});

		return mixers;
	}

	/**
	 * Returns the mixer with this name.
	 *
	 * @param name the name
	 *
	 * @return The Mixer with that name
	 */
	private Mixer getMixer(final String name) {
		Mixer mixer = null;

		// Obtains an array of mixer info objects that represents the set of
		// audio mixers that are currently installed on the system.
		final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		if (name != null)
            for (Mixer.Info mixerInfo : mixerInfos)
                if (mixerInfo.getName().equals(name)) {
                    mixer = AudioSystem.getMixer(mixerInfo);
                    break;
                }
		return mixer;
	}

	/**
	 * Returns Gain value.
	 *
	 * @return The Gain Value
	 */
	@Override
	public float getGainValue() {
		return outlet.getGainValue();
    }

	/**
	 * Returns maximum Gain value.
	 *
	 * @return The Maximum Gain Value
	 */
	@Override
	public float getMaximumGain() {
		return !outlet.hasControl(FloatControl.Type.MASTER_GAIN, outlet.getGainControl()) ? 0.0F : outlet.getGainControl().getMaximum();

	}

	/**
	 * Returns minimum Gain value.
	 *
	 * @return The Minimum Gain Value
	 */
	@Override
	public float getMinimumGain() {

		return !outlet.hasControl(FloatControl.Type.MASTER_GAIN, outlet.getGainControl()) ? 0.0F : outlet.getGainControl().getMinimum();

	}

	/**
	 * Returns Pan precision.
	 * <p>
	 * Obtains the resolution or granularity of the control, in the units that the control measures.
	 * The precision is the size of the increment between discrete valid values for this control,
	 * over the set of supported floating-point values.
	 *
	 * @return The Precision Value for the pan control, if it exists, otherwise 0.0.
	 */
	@Override
	public float getPrecision() {
		return !outlet.hasControl(FloatControl.Type.PAN, outlet.getPanControl())
				? 0
				: outlet.getPanControl().getPrecision();

	}

	/**
	 * Returns Pan value.
	 *
	 * @return The Pan Value
	 */
	@Override
	public float getPan() {
		return !outlet.hasControl(FloatControl.Type.PAN, outlet.getPanControl()) ? 0.0F : outlet.getPanControl().getValue();

	}

	/**
	 * Return the mute Value(true || false).
	 *
	 * @return True if muted , False if not
	 */
	@Override
	public boolean getMute() {
		return outlet.hasControl(BooleanControl.Type.MUTE, outlet.getMuteControl()) && outlet.getMuteControl().getValue();
	}

	/**
	 * Return the balance Value.
	 *
	 * @return The Balance Value
	 */
	@Override
	public float getBalance() {
		return !outlet.hasControl(FloatControl.Type.BALANCE, outlet.getBalanceControl()) ? 0f : outlet.getBalanceControl().getValue();
	}

	/****
	 * Return the total size of this file in bytes.
	 *
	 * @return encodedAudioLength
	 */
	@Override
	public long getTotalBytes() {
		return encodedAudioLength;
	}

	/**
	 * @return BytePosition
	 */
	@Override
	public int getPositionByte() {
		final int positionByte = AudioSystem.NOT_SPECIFIED;
		if (audioProperties != null) {
			if (audioProperties.containsKey("mp3.position.byte"))
				return (Integer) audioProperties.get("mp3.position.byte");
			if (audioProperties.containsKey("ogg.position.byte"))
				return (Integer) audioProperties.get("ogg.position.byte");
		}
		return positionByte;
	}

	/** The source data line. */
	public Outlet getOutlet() {
		return outlet;
	}

	/**
	 * This method will return the status of the player
	 *
	 * @return The Player Status
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/**
	 * Deep copy of a Map.
	 *
	 * @param map The Map to be Copied
	 *
	 * @return the map that is an exact copy of the given map
	 */
	private Map<String, Object> deepCopy(final Map<String, Object> map) {
		final HashMap<String, Object> copier = new HashMap<>();
		if (map != null)
			map.keySet().forEach(key -> copier.put(key, map.get(key)));
		return copier;
	}

	/**
	 * Set SourceDataLine buffer size. It affects audio latency. (the delay between
	 * line.write(data) and real sound). Minimum value should be over 10000 bytes.
	 *
	 * @param size -1 means maximum buffer size available.
	 */
	@Override
	public void setLineBufferSize(final int size) {
		lineBufferSize = size;
	}

	/**
	 * Sets Pan value. Line should be opened before calling this method. Linear
	 * scale : -1.0 ... +1.0
	 *
	 * @param fPan the new pan
	 */
	@Override
	public void setPan(final double fPan) {

		if (!outlet.hasControl(FloatControl.Type.PAN, outlet.getPanControl()) || fPan < -1.0 || fPan > 1.0)
			return;
		logger.info(() -> "Pan : " + fPan);
		outlet.getPanControl().setValue((float) fPan);
		generateEvent(Status.PAN, getEncodedStreamPosition(), null);

	}

	/**
	 * Sets Gain value. Line should be opened before calling this method. Linear
	 * scale 0.0 ... 1.0 Threshold Coef. : 1/2 to avoid saturation.
	 *
	 * @param fGain The new gain value
	 */
	@Override
	public void setGain(final double fGain) {
		if (isPlaying() || isPaused() && outlet.hasControl(FloatControl.Type.MASTER_GAIN, outlet.getGainControl())) {
            final double logScaleGain = 20 * Math.log10(fGain);
			outlet.getGainControl().setValue((float) logScaleGain);
        }
	}

	@Override
	public void setLogScaleGain(final double logScaleGain) {
		if (isPlaying() || isPaused() && outlet.hasControl(FloatControl.Type.MASTER_GAIN, outlet.getGainControl())) {
			outlet.getGainControl().setValue((float) logScaleGain);
		}
	}

	/**
	 * Set the mute of the Line. Note that mute status does not affect gain.
	 *
	 * @param mute True to mute the audio of False to unmute it
	 */
	@Override
	public void setMute(final boolean mute) {
		if (outlet.hasControl(BooleanControl.Type.MUTE, outlet.getMuteControl()) && outlet.getMuteControl().getValue() != mute)
			outlet.getMuteControl().setValue(mute);
	}

	/**
	 * Represents a control for the relative balance of a stereo signal between two
	 * stereo speakers. The valid range of values is -1.0 (left channel only) to 1.0
	 * (right channel only). The default is 0.0 (centered).
	 *
	 * @param fBalance the new balance
	 */
	@Override
	public void setBalance(final float fBalance) {
		if (outlet.hasControl(FloatControl.Type.BALANCE, outlet.getBalanceControl()) && fBalance >= -1.0 && fBalance <= 1.0)
			outlet.getBalanceControl().setValue(fBalance);
		else
			try {
				throw new StreamPlayerException(PlayerException.BALANCE_CONTROL_NOT_SUPPORTED);
			} catch (final StreamPlayerException ex) {
				logger.log(Level.WARNING, ex.getMessage(), ex);
			}
	}

	/**
	 * Changes specific values from equalizer.
	 *
	 * @param array the array
	 * @param stop the stop
	 */
	@Override
	public void setEqualizer(final float[] array, final int stop) {
		if (!isPausedOrPlaying() || !(audioInputStream instanceof PropertiesContainer))
			return;
		// Map<?, ?> map = ((PropertiesContainer) audioInputStream).properties()
		final float[] equalizer = (float[]) ((PropertiesContainer) audioInputStream).properties().get("mp3.equalizer");
        if (stop >= 0) System.arraycopy(array, 0, equalizer, 0, stop);

	}

	/**
	 * Changes a value from equalizer.
	 *
	 * @param value the value
	 * @param key the key
	 */
	@Override
	public void setEqualizerKey(final float value, final int key) {
		if (!isPausedOrPlaying() || !(audioInputStream instanceof PropertiesContainer))
			return;
		// Map<?, ?> map = ((PropertiesContainer) audioInputStream).properties()
		final float[] equalizer = (float[]) ((PropertiesContainer) audioInputStream).properties().get("mp3.equalizer");
		equalizer[key] = value;

	}

	/**
	 * @return The Speech Factor of the Audio
	 */
	@Override
	public double getSpeedFactor() {
		return this.speedFactor;
	}

	/**
	 * Checks if is unknown.
	 *
	 * @return If Status==STATUS.UNKNOWN.
	 */
	@Override
	public boolean isUnknown() {
		return status == Status.NOT_SPECIFIED;
	}

	/**
	 * Checks if is playing.
	 *
	 * @return <b>true</b> if player is playing ,<b>false</b> if not.
	 */
	@Override
	public boolean isPlaying() {
		return status == Status.PLAYING;
	}

	/**
	 * Checks if is paused.
	 *
	 * @return <b>true</b> if player is paused ,<b>false</b> if not.
	 */
	@Override
	public boolean isPaused() {
		return status == Status.PAUSED;
	}

	/**
	 * Checks if is paused or playing.
	 *
	 * @return <b>true</b> if player is paused/playing,<b>false</b> if not
	 */
	@Override
	public boolean isPausedOrPlaying() {
		return isPlaying() || isPaused();
	}

	/**
	 * Checks if is stopped.
	 *
	 * @return <b>true</b> if player is stopped ,<b>false</b> if not
	 */
	@Override
	public boolean isStopped() {
		return status == Status.STOPPED;
	}

	/**
	 * Checks if is opened.
	 *
	 * @return <b>true</b> if player is opened ,<b>false</b> if not
	 */
	@Override
	public boolean isOpened() {
		return status == Status.OPENED;
	}

	/**
	 * Checks if is seeking.
	 *
	 * @return <b>true</b> if player is seeking ,<b>false</b> if not
	 */
	@Override
	public boolean isSeeking() {
		return status == Status.SEEKING;
	}

	Logger getLogger() {
		return logger;
	}

	@Override
	public SourceDataLine getSourceDataLine() {
		return outlet.getSourceDataLine();
	}
}
