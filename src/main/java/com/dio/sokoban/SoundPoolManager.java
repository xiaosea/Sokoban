package com.dio.sokoban;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPoolManager {
	private static SoundPoolManager self;
	private SoundPool mSoundPool;
	private Context mContext;
	
	public static int SOUND_ID_BUTTON = 0;
	public static int SOUND_ID_LEVEL_UP = 1;
	
	public static SoundPoolManager getInstance(Context context) {
		if (self == null) {
			self = new SoundPoolManager(context);
		}
		return self;
	}
	
	private SoundPoolManager(Context context){
		mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		SOUND_ID_BUTTON = mSoundPool.load(context, R.raw.button, 1);
		SOUND_ID_LEVEL_UP = mSoundPool.load(context, R.raw.level_up, 1);
		mContext = context;
	}
	
	public void play(int soundId) {
    	boolean isSoundOn = SettingsActivity.isSoundOn(mContext);
    	if (isSoundOn) {
    		mSoundPool.play(soundId, 0.5f, 0.5f, 1, 0, 1f); 
		}
	}
	
	@TargetApi(8)
	public void autoPause() {
		mSoundPool.autoPause();
	}

	@TargetApi(8)
	public void autoResume() {
		mSoundPool.autoResume();
	}
	
	public void release() {
		mSoundPool.release();
		self = null;
	}
}
