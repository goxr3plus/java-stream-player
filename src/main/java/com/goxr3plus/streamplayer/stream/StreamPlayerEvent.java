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

/**
 * The Class StreamPlayerEvent.
 *
 * @author GOXR3PLUS (www.goxr3plus.co.nf)
 */
public class StreamPlayerEvent {

    /** The status. */
    private Status playerStatus = Status.NOT_SPECIFIED;

    /** The stream position. */
    private int encodedStreamPosition = -1;

    /** The description. */
    private Object description = null;

    /**
     * Constructor.
     *
     * @param status
     *            the status
     * @param encodededStreamPosition
     *            the stream position
     * @param description
     *            the description
     */
    public StreamPlayerEvent(Status status, int encodededStreamPosition, Object description) {
        this.playerStatus = status;
	this.encodedStreamPosition = encodededStreamPosition;
	this.description = description;
    }

    /**
     * Returns the Player Status
     *
     * @return The player Status (paused,playing,...)
     * @see Status
     */
    public Status getPlayerStatus() {
	return playerStatus;
    }

    /**
     * Returns the encoded stream position
     *
     * @return EncodedStreamPosition = the position of the encoded audio stream
     *         right now..
     */
    public int getEncodedStreamPosition() {
	return encodedStreamPosition;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public Object getDescription() {
	return description;
    }

    @Override
    public String toString() {
	return "Player Status := " + playerStatus + " , EncodedStreamPosition :="
		+ encodedStreamPosition + " , Description :=" + description;

    }

}
