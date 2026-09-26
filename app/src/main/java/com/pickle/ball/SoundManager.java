package com.pickle.ball;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundManager {

    private final SoundPool pool;
    private final int sndBounce;
    private final int sndScore;
    private final int sndServe;

    public SoundManager(Context context) {
        pool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                )
                .build();

        sndBounce = 0;
        sndScore  = 0;
        sndServe  = 0;
    }

    public void playBounce() { if (sndBounce != 0) pool.play(sndBounce, 0.7f, 0.7f, 1, 0, 1f); }
    public void playScore()  { if (sndScore  != 0) pool.play(sndScore,  1f,   1f,   1, 0, 1f); }
    public void playServe()  { if (sndServe  != 0) pool.play(sndServe,  0.8f, 0.8f, 1, 0, 1f); }

    public void release() { pool.release(); }
}
