package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class Outlet {

    private FloatControl balanceControl;
    private FloatControl gainControl;
    private BooleanControl muteControl;
    private FloatControl panControl;
    private SourceDataLine sourceDataLine;


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
        return component != null && (getSourceDataLine() != null) && (getSourceDataLine().isControlSupported(control));
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
        if (getSourceDataLine() != null) {
            getSourceDataLine().flush();
            getSourceDataLine().close();
            this.sourceDataLine = null; // TODO: Is this necessary? Will it not be garbage collected?
        }
    }

    void flushAndStop() {
        // Flush and stop the source data line
        if (getSourceDataLine() != null && getSourceDataLine().isRunning()) {
            getSourceDataLine().flush();
            getSourceDataLine().stop();
        }
    }

}
