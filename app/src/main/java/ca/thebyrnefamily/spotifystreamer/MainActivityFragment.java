package ca.thebyrnefamily.spotifystreamer;

import android.content.Intent;

import android.os.AsyncTask;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistArrayAdapter mArtistAdapter;
    private ArrayList<SpotifyArtist> artistList;
    private SearchView mySearchView;
    private String myArtistStr;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState ==null || !savedInstanceState.containsKey("artists")){
            artistList = new ArrayList<SpotifyArtist>();
        }else{
            artistList = savedInstanceState.getParcelableArrayList("artists");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("artists",artistList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


       // ArrayList<SpotifyArtist> artistList= new ArrayList<SpotifyArtist>();

        mArtistAdapter = new ArtistArrayAdapter(getActivity(), artistList);



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(mArtistAdapter);

        mySearchView = (SearchView) rootView.findViewById(R.id.artistSearch);

        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                myArtistStr = s;
                //Toast.makeText(getActivity(),myArtistStr,Toast.LENGTH_SHORT).show();
                getArtists();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){

                Intent intent = new Intent(getActivity(), TopTracks.class)
                        .putExtra(Intent.EXTRA_TEXT, mArtistAdapter.getItem(position).id +"|"+mArtistAdapter.getItem(position).name);
                        startActivity(intent);
            }
        });


        return rootView;
    }

    private Void getArtists(){
        if(myArtistStr!=null){
            FetchArtistTask artistTask = new FetchArtistTask();
            artistTask.execute(myArtistStr);
        }


       return null;
    }


    public class FetchArtistTask extends AsyncTask<String, Void, ArrayList<Artist>>{
        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();


        @Override
        protected void onPostExecute(ArrayList<Artist> result) {
            String myUrl;
            if(result!= null && result.size()>0){
                mArtistAdapter.clear();
                for (Artist myArtist :result) {
                    try {
                        if (myArtist.images.size()>0){
                            myUrl = myArtist.images.get(myArtist.images.size()-1).url;
                        }else{
                            myUrl = "http://placehold.it/200x200?text=no+Image";
                        }
                        mArtistAdapter.add(new SpotifyArtist(
                                myArtist.name,
                                myArtist.id,
                                myUrl
                        ));

                    }catch (Exception e){
                        Log.e(LOG_TAG, "exception " + e.toString());
                    }
                }
            }else {
                Toast.makeText(getActivity(), R.string.no_artist_found , Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

        @Override
        protected ArrayList<Artist> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            //hardcode return limit to 10 for now
            Map<String,Object> queryOptions = new HashMap<String, Object>();
            queryOptions.put("limit","10");
            ArtistsPager results = spotify.searchArtists(params[0],queryOptions);
            ArrayList<Artist> myArtistList = new ArrayList<Artist>();


            for (Artist a: results.artists.items){
                myArtistList.add(a);
                Log.v(LOG_TAG, a.name);
            }


            return myArtistList;
        }
    }
}
