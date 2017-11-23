package com.techjumper.corelib.utils.media;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseIntArray;

import com.techjumper.corelib.utils.Utils;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/8/4
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class SoundManager {

    private volatile static SoundManager sm;
    private SoundPool sp;
    private SparseIntArray mSArray;

    private SoundManager() {
        mSArray = new SparseIntArray();
        try {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            } else {
                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setMaxStreams(10);
                AudioAttributes aa = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                sp = builder.setAudioAttributes(aa).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SoundManager getInstance() {
        if (sm == null) {
            synchronized (SoundManager.class) {
                if (sm == null) {
                    sm = new SoundManager();
                }
            }
        }
        return sm;
    }

    public boolean load(int key, String assetsPath) {
        boolean result = false;
        try {
            mSArray.put(key, sp.load(Utils.appContext.getAssets().openFd(assetsPath), 1));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean hasKey(int key) {
        return mSArray.get(key, -5) != -5;
    }


    public void play(int key) {
        int soundId = mSArray.get(key);
        sp.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

}
