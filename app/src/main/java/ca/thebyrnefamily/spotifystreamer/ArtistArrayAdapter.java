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
 * Created by Gee on 10/07/2015.
 */
public class ArtistArrayAdapter extends ArrayAdapter<SpotifyArtist> {
    private final String LOG_TAG = ArtistArrayAdapter.class.getSimpleName();
    private Context myContext;
    private TextView tvName;
    public ArtistArrayAdapter(Activity context, ArrayList<SpotifyArtist> artist){
        super(context, 0, artist);
        myContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpotifyArtist artist = getItem(position);

        if (convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist,parent, false);
        }
        tvName=(TextView) convertView.findViewById(R.id.list_item_artist_textview);

        ImageView imgName= (ImageView) convertView.findViewById(R.id.list_item_cover);

            tvName.setText(artist.name);
        try {
            Picasso.with(myContext).load(artist.url).into(imgName);
        } catch (IndexOutOfBoundsException e){
            ;
        }catch (NullPointerException e){
            ;
        }

        return convertView;
    }
}
