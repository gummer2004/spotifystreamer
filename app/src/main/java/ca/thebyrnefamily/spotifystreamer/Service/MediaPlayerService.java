package ca.thebyrnefamily.spotifystreamer.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.security.Provider;

import ca.thebyrnefamily.spotifystreamer.PlayerFragment;

/**
 * Created by Gee on 8/23/2015.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{
    private final String LOG_TAG = Service.class.getSimpleName();
    private MediaPlayerService mInstance = null;
    private MediaPlayer mMediaPlayer = null;
    private String mUrl, mAlbumName, mSongTitle;
    private final IBinder mBinder= new MediaBinder();
    private int mCurrentPosition;
    private Boolean mIsPaused=false;
    public OnMediaPlayerServiceListener mCallback;
    private Handler seekHandler = new Handler();


    @Override
    public void onCreate(){
        super.onCreate();
        mCallback = new PlayerFragment();
        mMediaPlayer = new MediaPlayer();
        initializeMedia();
    }

    private void initializeMedia(){
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);


    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        mCallback.onMediaCompleted();
        mp.reset();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mp.start();
        mCallback.onMediaPlaying();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

   // @Override
   // public boolean onUnbind(Intent intent){
   //     mMediaPlayer.stop();
   //     mMediaPlayer.release();
   //     return false;
   // }

    public void playTrack(String url){
        if (mMediaPlayer.isPlaying()||mIsPaused){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        try{
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
            mIsPaused=false;
        }catch (Exception e){
            Log.e(LOG_TAG, "Error " + e.toString());
        }
    }
    public void scrubTo(int location){
        mCurrentPosition = location;
        resume();
    }
    public void pause(){
        mCurrentPosition = mMediaPlayer.getCurrentPosition();
        mIsPaused=true;
        mMediaPlayer.pause();
    }
    public void reset(){
        mMediaPlayer.reset();
    }
    public void resume(){
        mMediaPlayer.seekTo(mCurrentPosition);
        mMediaPlayer.start();
        mCallback.onMediaPlaying();
    }
    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }
    public int getDuration (){
        return mMediaPlayer.getDuration();
    }
    public boolean isRunning(){
        return mMediaPlayer.isPlaying();
    }
    public class MediaBinder extends Binder {
        public MediaPlayerService getService(){
            return MediaPlayerService.this;
        }
    }


    public interface OnMediaPlayerServiceListener{
        void onMediaCompleted();
        void onMediaPlaying();
        void onMediaPaused();
    }

}
