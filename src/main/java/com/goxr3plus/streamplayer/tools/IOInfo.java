package com.goxr3plus.streamplayer.tools;

import org.apache.commons.io.FilenameUtils;

public class IOInfo {

	/**
	 * Returns the extension of file(without (.)) for example <b>(ai.mp3)->(mp3)</b>
	 * and to lowercase (Mp3 -> mp3)
	 *
	 * @param absolutePath The File absolute path
	 *
	 * @return the File extension
	 */
	public static String getFileExtension(final String absolutePath) {
		return FilenameUtils.getExtension(absolutePath).toLowerCase();

		// int i = path.lastIndexOf('.'); // characters contained before (.)
		//
		// if the name is not empty
		// if (i > 0 && i < path.length() - 1)
		// return path.substring(i + 1).toLowerCase()
		//
		// return null
	}

	/**
	 * Returns the name of the file for example if file path is <b>(C:/Give me
	 * more/no no/media.ogg)</b> it returns <b>(media.ogg)</b>
	 *
	 * @param absolutePath the path
	 *
	 * @return the File title+extension
	 */
	public static String getFileName(final String absolutePath) {
		return FilenameUtils.getName(absolutePath);

	}

	/**
	 * Returns the title of the file for example if file name is <b>(club.mp3)</b>
	 * it returns <b>(club)</b>
	 *
	 * @param absolutePath The File absolute path
	 *
	 * @return the File title
	 */
	public static String getFileTitle(final String absolutePath) {
		return FilenameUtils.getBaseName(absolutePath);
	}

}
