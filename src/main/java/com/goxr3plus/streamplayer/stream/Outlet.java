package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.*;
import java.util.logging.Logger;

/**
 * Owner of the SourceDataLine which is the output line of the player.
 * Also owns controls for the SourceDataLine.
 * Future goal is to move all handling of the SourceDataLine to here,
 * so that the StreamPlayer doesn't have to call {@link #getSourceDataLine()}.
 * Another goal is to remove some of the setter and getter methods of this class,
 * by moving all code that needs them to this class.
 */
public class Outlet {

    private final Logger logger;
    private FloatControl balanceControl;
    private FloatControl gainControl;
    private BooleanControl muteControl;
    private FloatControl panControl;
    private SourceDataLine sourceDataLine;

    /**
     * @param logger used to log messages
     */
    public Outlet(Logger logger) {
        this.logger = logger;
    }


    /**
     * @return the balance control of the {@link #sourceDataLine}
     */
    public FloatControl getBalanceControl() {
        return balanceControl;
    }

    /**
     * @return the gain control of the {@link #sourceDataLine}
     */
    public FloatControl getGainControl() {
        return gainControl;
    }

    /**
     * @return the mute control of the {@link #sourceDataLine}
     */
    public BooleanControl getMuteControl() {
        return muteControl;
    }

    /**
     * @return the pan control of the {@link #sourceDataLine}
     */
    public FloatControl getPanControl() {
        return panControl;
    }

    /**
     * @return the {@link #sourceDataLine}, which is the output audio signal of the player
     */
    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }


    /**
     * @param balanceControl to be set on the {@link #sourceDataLine}
     */
    public void setBalanceControl(FloatControl balanceControl) {
        this.balanceControl = balanceControl;
    }

    /**
     * @param gainControl to be set on the {@link #sourceDataLine}
     */
    public void setGainControl(FloatControl gainControl) {
        this.gainControl = gainControl;
    }

    /**
     * @param muteControl to be set on the {@link #sourceDataLine}
     */
    public void setMuteControl(BooleanControl muteControl) {
        this.muteControl = muteControl;
    }

    /**
     * @param panControl to be set on the {@link #sourceDataLine}
     */
    public void setPanControl(FloatControl panControl) {
        this.panControl = panControl;
    }

    /**
     * @param sourceDataLine representing the audio output of the player.
     *                       Usually taken from {@link AudioSystem#getLine(Line.Info)}.
     */
    public void setSourceDataLine(SourceDataLine sourceDataLine) {
        this.sourceDataLine = sourceDataLine;
    }


    /**
     * Check if the <b>Control</b> is Supported by m_line.
     *
     * @param control the control
     * @param component the component
     *
     * @return true, if successful
     */
    public boolean hasControl(final Control.Type control, final Control component) {
        return component != null && (sourceDataLine != null) && (sourceDataLine.isControlSupported(control));
    }

    /**
     * Returns Gain value.
     *
     * @return The Gain Value
     */
    public float getGainValue() {

        if (hasControl(FloatControl.Type.MASTER_GAIN, getGainControl())) {
            return getGainControl().getValue();
        } else {
            return 0.0F;
        }
    }

    /**
     * Stop the {@link #sourceDataLine} in a nice way.
     * Also nullify it. (Is that necessary?)
     */
    void drainStopAndFreeDataLine() {
        // Free audio resources.
        if (sourceDataLine != null) {
            sourceDataLine.drain();
            sourceDataLine.stop();
            sourceDataLine.close();
            this.sourceDataLine = null;  // TODO: Is this necessary? Will it not be garbage collected?
        }
    }

    /**
     * Flush and close the {@link #sourceDataLine} in a nice way.
     * Also nullify it. (Is that necessary?)
     */
     void flushAndFreeDataLine() {
        if (sourceDataLine != null) {
            sourceDataLine.flush();
            sourceDataLine.close();
            this.sourceDataLine = null; // TODO: Is this necessary? Will it not be garbage collected?
        }
    }

    /**
     * Flush and stop the {@link #sourceDataLine}, if it's running.
     */
    void flushAndStop() {
        // Flush and stop the source data line
        if (sourceDataLine != null && sourceDataLine.isRunning()) { // TODO: Risk for NullPointerException?
            sourceDataLine.flush();
            sourceDataLine.stop();
        }
    }

    /**
     * @return true if the {@link #sourceDataLine} is startable.
     */
    boolean isStartable() {
        return sourceDataLine != null && !sourceDataLine.isRunning();
    }


    /**
     * Start the {@link #sourceDataLine}
     */
    void start() {
        sourceDataLine.start();
    }

    /**
     * Open the {@link #sourceDataLine}.
     * Also create controls for it.
     * @param format The wanted audio format.
     * @param bufferSize the desired buffer size for the {@link #sourceDataLine}
     * @throws LineUnavailableException
     */
    void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
        logger.info("Entered OpenLine()!:\n");

        if (sourceDataLine != null) {
            sourceDataLine.open(format, bufferSize);

            // opened?
            if (sourceDataLine.isOpen()) {

                // Master_Gain Control?
                if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
                    setGainControl((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN));
                else setGainControl(null);

                // PanControl?
                if (sourceDataLine.isControlSupported(FloatControl.Type.PAN))
                    setPanControl((FloatControl) sourceDataLine.getControl(FloatControl.Type.PAN));
                else setPanControl(null);

                // Mute?
                BooleanControl muteControl1 = sourceDataLine.isControlSupported(BooleanControl.Type.MUTE)
                        ? (BooleanControl) sourceDataLine.getControl(BooleanControl.Type.MUTE)
                        : null;
                setMuteControl(muteControl1);

                // Speakers Balance?
                FloatControl balanceControl = sourceDataLine.isControlSupported(FloatControl.Type.BALANCE)
                        ? (FloatControl) sourceDataLine.getControl(FloatControl.Type.BALANCE)
                        : null;
                setBalanceControl(balanceControl);
            }
        }
        logger.info("Exited OpenLine()!:\n");
    }

}
