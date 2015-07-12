package ca.thebyrnefamily.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {
    TrackArrayAdapter mTrackAdapter;
    ArrayList<SpotifyTrack> trackList;
    String artistName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        String artist_id;
        super.onCreate(savedInstanceState);
        if(savedInstanceState ==null || !savedInstanceState.containsKey("tracks")){
            trackList = new ArrayList<SpotifyTrack>();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String[] values = intent.getStringExtra(Intent.EXTRA_TEXT).split("\\|");
                artist_id = values[0];
                artistName = values[1];
                FetchTopTenTask myTopTen = new FetchTopTenTask();
                myTopTen.execute(artist_id);
            }
        }else{
            trackList = savedInstanceState.getParcelableArrayList("tracks");

        }
        if(savedInstanceState==null || !savedInstanceState.containsKey("artist")){
            ;
        }else{
            artistName = savedInstanceState.getString("artist");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("tracks",trackList);
        outState.putString("artist",artistName);
        super.onSaveInstanceState(outState);

    }

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mTrackAdapter = new TrackArrayAdapter(getActivity(), trackList);
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTrackAdapter);

        ((TopTracks) getActivity()).getSupportActionBar().setTitle("Top Ten Tracks");
        ((TopTracks) getActivity()).getSupportActionBar().setSubtitle(artistName);

        return rootView;
    }


    public class FetchTopTenTask extends AsyncTask<String, Void, ArrayList<Track>> {
        private final String LOG_TAG = FetchTopTenTask.class.getSimpleName();

        @Override
        protected void onPostExecute(ArrayList<Track> result) {
            String myImage;
            if(result!= null && result.size()>0){
                mTrackAdapter.clear();
                for (Track myTrack :result){
                    try{
                        if (myTrack.album.images.size()>0){
                            myImage = myTrack.album.images.get(myTrack.album.images.size()-1).url;
                        }else{
                            myImage = "http://placehold.it/200x200?text=no+Image";
                        }
                        mTrackAdapter.add(new SpotifyTrack(
                                artistName,
                                myTrack.name,
                                myTrack.album.name,
                                myImage,
                                myTrack.preview_url
                        ));
                    }catch (Exception e){
                        Log.e(LOG_TAG, "exception " + e.toString());
                    }
                }
            }else{
                Toast.makeText(getActivity(), R.string.no_tracks , Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
        @Override
        protected ArrayList<Track> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Log.v(LOG_TAG, params[0]);
            // Hardcode country option for now
            Map<String,Object> queryOptions = new HashMap<String, Object>();
            queryOptions.put("country","CA");
            Tracks results = spotify.getArtistTopTrack(params[0],queryOptions);
            ArrayList<Track> myTopTracks = new ArrayList<>();

            for (Track a : results.tracks) {
                myTopTracks.add(a);
                Log.v(LOG_TAG, a.name);
            }
            return myTopTracks;
        }
    }
}