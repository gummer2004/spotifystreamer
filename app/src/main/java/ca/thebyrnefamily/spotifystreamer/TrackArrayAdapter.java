package ca.thebyrnefamily.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Gee on 11/07/2015.
 */
public class TrackArrayAdapter extends ArrayAdapter<SpotifyTrack> {
    private final String LOG_TAG = ArtistArrayAdapter.class.getSimpleName();
    private Context myContext;
    private TextView tvTrack;
    private TextView tvAlbum;
    public TrackArrayAdapter(Activity context, ArrayList<SpotifyTrack> track){
        super(context, 0, track);
        myContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpotifyTrack track = getItem(position);

        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_tracks,parent, false);
        }
        tvTrack=(TextView) convertView.findViewById(R.id.list_item_track);
        tvAlbum=(TextView) convertView.findViewById(R.id.list_item_album);

        ImageView imgName= (ImageView) convertView.findViewById(R.id.list_item_thumbnail);

        tvTrack.setText(track.name);

        try {
            tvAlbum.setText(track.album);
            Picasso.with(myContext).load(track.image).into(imgName);
        } catch (IndexOutOfBoundsException e){
            ;
        }catch (NullPointerException e){
            ;
        }

        return convertView;
    }
}

