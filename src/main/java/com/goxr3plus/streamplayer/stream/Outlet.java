package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class Outlet {

    private FloatControl balanceControl;
    private FloatControl gainControl;
    private BooleanControl muteControl;
    private FloatControl panControl;

    /** The source data line. */
    private SourceDataLine sourceDataLine;

    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    public void setSourceDataLine(SourceDataLine sourceDataLine) {
        this.sourceDataLine = sourceDataLine;
    }


    public BooleanControl getMuteControl() {
        return muteControl;
    }

    public void setMuteControl(BooleanControl muteControl) {
        this.muteControl = muteControl;
    }


    public FloatControl getPanControl() {
        return panControl;
    }

    public void setPanControl(FloatControl panControl) {
        this.panControl = panControl;
    }

    public FloatControl getBalanceControl() {
        return balanceControl;
    }

    public void setBalanceControl(FloatControl balanceControl) {
        this.balanceControl = balanceControl;
    }

    public FloatControl getGainControl() {
        return gainControl;
    }

    public void setGainControl(FloatControl gainControl) {
        this.gainControl = gainControl;
    }
}
