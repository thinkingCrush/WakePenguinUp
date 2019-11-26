package kr.Thinkingcrush.WakePenguinUp.Tool;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import kr.Thinkingcrush.WakePenguinUp.R;

public class SoundPlay {


    public void playSound(final Context context) {
        try {
            MediaPlayer  mediaPlayer = MediaPlayer.create(context,R.raw.sound_lock);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.start();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
