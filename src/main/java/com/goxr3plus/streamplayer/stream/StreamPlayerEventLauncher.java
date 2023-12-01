/*
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

   Also(warning!):
 
  1)You are not allowed to sell this product to third party.
  2)You can't change license and made it like you are the owner,author etc.
  3)All redistributions of source code files must contain all copyright
     notices that are currently in this file, and this list of conditions without
     modification.
 */
package com.goxr3plus.streamplayer.stream;

import com.goxr3plus.streamplayer.enums.Status;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class StreamPlayerEventLauncher.
 *
 * @author GOXR3PLUS (www.goxr3plus.co.nf)
 */
public class StreamPlayerEventLauncher implements Callable<String> {

    private final Logger logger;
    /** The player state. */
    private Status playerState = Status.NOT_SPECIFIED;

    /** The stream position. */
    private int encodedStreamPosition = -1;

    /** The description. */
    private Object description = null;

    /** The listeners. */
    private List<StreamPlayerListener> listeners = null;

    /** The source. */
    private StreamPlayerInterface source = null;

    /**
     * Instantiates a new stream player event launcher.
     *
     * @param source                the source
     * @param playerStatus          the play state
     * @param encodedStreamPosition the stream position
     * @param description           the description
     * @param listeners             will be called when events happens
     * @param logger                Logger to use for logging
     */
    public StreamPlayerEventLauncher(StreamPlayerInterface source, Status playerStatus, int encodedStreamPosition, Object description,
                                     List<StreamPlayerListener> listeners, Logger logger) {
	this.source = source;
	this.playerState = playerStatus;
	this.encodedStreamPosition = encodedStreamPosition;
	this.description = description;
	this.listeners = listeners;
    this.logger = logger;
    }

    @Override
    public String call() {
	// Notify all the listeners that the state has been updated
	if (listeners != null) {
	    listeners.forEach(listener -> listener
		    .statusUpdated(new StreamPlayerEvent(playerState, encodedStreamPosition, description)));
	}
	logger.log(Level.INFO, "Stream player Status -> " + playerState);
	return "OK";
    }
}
