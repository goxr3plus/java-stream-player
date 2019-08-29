package com.goxr3plus.streamplayer.application;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

public class AnotherMain {
    public static void main(String[] args) {

        final StreamPlayer streamPlayer = new StreamPlayer();
        final AnotherDemoApplication application = new AnotherDemoApplication(streamPlayer);
        application.start();

    }

}
