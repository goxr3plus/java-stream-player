package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.BooleanControl;
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
}
