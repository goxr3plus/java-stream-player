package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.FloatControl;

public class Outlet {

    private FloatControl balanceControl;
    private FloatControl gainControl;
    private FloatControl panControl;


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
