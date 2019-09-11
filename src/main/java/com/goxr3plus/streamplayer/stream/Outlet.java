package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.*;
import java.util.logging.Logger;

public class Outlet {

    private final Logger logger;
    private FloatControl balanceControl;
    private FloatControl gainControl;
    private BooleanControl muteControl;
    private FloatControl panControl;
    private SourceDataLine sourceDataLine;

    public Outlet(Logger logger) {
        this.logger = logger;
    }


    public FloatControl getBalanceControl() {
        return balanceControl;
    }

    public FloatControl getGainControl() {
        return gainControl;
    }

    public BooleanControl getMuteControl() {
        return muteControl;
    }

    public FloatControl getPanControl() {
        return panControl;
    }

    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    public void setBalanceControl(FloatControl balanceControl) {
        this.balanceControl = balanceControl;
    }

    public void setGainControl(FloatControl gainControl) {
        this.gainControl = gainControl;
    }

    public void setMuteControl(BooleanControl muteControl) {
        this.muteControl = muteControl;
    }

    public void setPanControl(FloatControl panControl) {
        this.panControl = panControl;
    }

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

    void drainStopAndFreeDataLine() {
        // Free audio resources.
        if (sourceDataLine != null) {
            sourceDataLine.drain();
            sourceDataLine.stop();
            sourceDataLine.close();
            this.sourceDataLine = null;  // TODO: Is this necessary? Will it not be garbage collected?
        }
    }

     void stopAndFreeDataLine() {
        if (sourceDataLine != null) {
            sourceDataLine.flush();
            sourceDataLine.close();
            this.sourceDataLine = null; // TODO: Is this necessary? Will it not be garbage collected?
        }
    }

    void flushAndStop() {
        // Flush and stop the source data line
        if (sourceDataLine != null && sourceDataLine.isRunning()) {
            sourceDataLine.flush();
            sourceDataLine.stop();
        }
    }

    boolean isStartable() {
        return sourceDataLine != null && !sourceDataLine.isRunning();
    }
    void start() {
        sourceDataLine.start();
    }

    void open(AudioFormat audioFormat, int currentLineBufferSize) throws LineUnavailableException {
        logger.info("Entered OpenLine()!:\n");

        if (sourceDataLine != null) {
            sourceDataLine.open(audioFormat, currentLineBufferSize);

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
