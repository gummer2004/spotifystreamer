package ca.thebyrnefamily.spotifystreamer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import ca.thebyrnefamily.spotifystreamer.Service.MediaPlayerService;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment implements MediaPlayerService.OnMediaPlayerServiceListener{
    private final String LOG_TAG = PlayerFragment.class.getSimpleName();
    private int mCurrentTrack;
    private ArrayList<SpotifyTrack> myTracks;

    private ImageView mAlbumImage;
    private TextView mArtistName;
    private TextView mAlbumName;
    private TextView mTrackName;
    private static TextView mSeekMinValue;
    private static TextView mSeekMaxValue;
    private static ImageButton playBtn;
    private static ImageButton pauseBtn;
    private static Boolean mPaused;
    private int mCurrentPostion;
    private static SeekBar mSeekBar;
    private Handler seekHandler = new Handler();
    private int msec = 1000;
    private Intent playIntent = null;
    private static MediaPlayerService mMediaPlayerService;
    private static boolean isBound=false;

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MediaBinder binder = (MediaPlayerService.MediaBinder) service;
            mMediaPlayerService = binder.getService();
            isBound=true;
            play();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound=false;

        }
    };



    @Override
    public void onStart() {
        super.onStart();

        if(playIntent==null){

            playIntent = new Intent(getActivity(), MediaPlayerService.class);
            getActivity().startService(playIntent);
            getActivity().bindService(playIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (isBound) {
            Log.v(LOG_TAG, "unbinding Service");
            getActivity().unbindService(mConnection);
        }
    }

    public PlayerFragment() {
        mPaused=false;
        mCurrentPostion=0;
    }

    Runnable run = new Runnable(){
        @Override
        public void run() {
                 seekUpdation();
        }
    };
    public void seekUpdation(){
        if ((isBound)&&(!mPaused)) {
           mSeekBar.setProgress(mMediaPlayerService.getCurrentPosition() / msec);
        }
        if (mMediaPlayerService.isRunning()) {
            seekHandler.postDelayed(run, 1000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        myTracks = intent.getParcelableArrayListExtra("MyTracks");
        mCurrentTrack= intent.getIntExtra(Intent.EXTRA_TEXT,0);

        View rootView= inflater.inflate(R.layout.fragment_player, container, false);
        mAlbumImage = (ImageView) rootView.findViewById(R.id.albumImage);
        mArtistName = (TextView) rootView.findViewById(R.id.artistName);
        mAlbumName = (TextView) rootView.findViewById(R.id.albumName);
        mTrackName = (TextView) rootView.findViewById(R.id.trackName);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.nowPlaying);
        mSeekMinValue= (TextView) rootView.findViewById(R.id.minSeekValue);
        mSeekMaxValue= (TextView) rootView.findViewById(R.id.maxSeekValue);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentPostion = progress * msec;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayerService.scrubTo(mCurrentPostion);
            }
        });


        ImageButton nextBtn = (ImageButton) rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playNext();
            }
        });
        ImageButton previousBtn = (ImageButton) rootView.findViewById(R.id.previousBtn);
        previousBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                playPrevious();
            }
        });
        playBtn = (ImageButton) rootView.findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                play();
            }
        });
        pauseBtn = (ImageButton) rootView.findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pause();
            }
        });

        updateMyView(mCurrentTrack);

       // play();


        return rootView;
    }

    private void play(){
        playBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
        Log.v(LOG_TAG,"media play");
        if (mPaused){
            mPaused=false;
            resume();
            return;
        }


        try {
            mMediaPlayerService.playTrack(myTracks.get(mCurrentTrack).preview);
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Error " + e.toString());
        }
    }
    private void resume(){
        mMediaPlayerService.resume();

    }
    private void playNext(){
        if (mCurrentTrack<myTracks.size()-1){
            mCurrentTrack++;
            mPaused=false;
            updateMyView(mCurrentTrack);
            play();
        }else{
            Toast.makeText(getActivity(),"End of List",Toast.LENGTH_SHORT).show();
        }
    }

    private void pause(){
        pauseBtn.setVisibility(View.GONE);
        playBtn.setVisibility(View.VISIBLE);
        mPaused=true;
        mCurrentPostion = mMediaPlayerService.getCurrentPosition();
        mMediaPlayerService.pause();
        Log.v(LOG_TAG, "media pause");
    }
    private void playPrevious(){
        if (mCurrentTrack>0){
            mCurrentTrack--;
            mPaused=false;
            updateMyView(mCurrentTrack);
            play();
        }else{
            Toast.makeText(getActivity(), "Already at start of List",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMyView(int i){
        mArtistName.setText(myTracks.get(i).artist);
        mAlbumName.setText(myTracks.get(i).album);
        mTrackName.setText(myTracks.get(i).name);
        Picasso.with(getActivity()).load(myTracks.get(i).image).into(mAlbumImage);

        return ;
    }

    @Override
    public void onMediaCompleted() {
        playBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.GONE);
        mPaused=false;
        Log.v(LOG_TAG, "media completed");
    }

    @Override
    public void onMediaPlaying() {
        mSeekMaxValue.setText(String.valueOf(mMediaPlayerService.getDuration()/msec));
        mSeekMinValue.setText("0");
        mSeekBar.setMax(mMediaPlayerService.getDuration()/msec);
        seekUpdation();
        Log.v(LOG_TAG,"Media Playing");

    }

    @Override
    public void onMediaPaused() {
        Log.v(LOG_TAG,"Media Paused");

    }
}

