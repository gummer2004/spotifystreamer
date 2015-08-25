package ca.thebyrnefamily.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gee on 11/07/2015.
 */
public class SpotifyArtist implements Parcelable {
    String name;
    String id;
    String url;

    public SpotifyArtist(String name, String id, String url){
        this.name=name;
        this.id=id;
        this.url=url;
    }

    private SpotifyArtist(Parcel in){
        name=in.readString();
        id=in.readString();
        url=in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<SpotifyArtist> CREATOR = new Parcelable.Creator<SpotifyArtist>(){
        @Override
        public SpotifyArtist createFromParcel(Parcel source) {
            return new SpotifyArtist(source);
        }

        @Override
        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }
    };
}
