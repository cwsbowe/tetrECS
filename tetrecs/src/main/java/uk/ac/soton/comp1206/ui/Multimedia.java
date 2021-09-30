package uk.ac.soton.comp1206.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class Multimedia {
    private MediaPlayer audioPlayer;
    private MediaPlayer musicPlayer;

    public void playAudio(Media media) {
        audioPlayer = new MediaPlayer(media);
        audioPlayer.play();
    }

    public void playMusic(Media media) {
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setAutoPlay(true);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    public void endMusic() {
        musicPlayer.stop();
    }

}
