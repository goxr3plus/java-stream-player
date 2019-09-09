package com.goxr3plus.streamplayer.stream;

import javax.sound.sampled.FloatControl;

public class Outlet {

    public FloatControl getGainControl() {
        return gainControl;
    }

    public void setGainControl(FloatControl gainControl) {
        this.gainControl = gainControl;
    }

    private FloatControl gainControl;
}
