package ca.thebyrnefamily.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gee on 11/07/2015.
 */
public class SpotifyTrack implements Parcelable{
    String artist;
    String name;
    String album;
    String image;
    String preview;


    public SpotifyTrack(String artist, String name, String album, String image,String preview){
        this.artist=artist;
        this.name=name;
        this.album=album;
        this.image=image;
        this.preview=preview;
    }

    private SpotifyTrack(Parcel in){
        artist=in.readString();
        name=in.readString();
        album=in.readString();
        image=in.readString();
        preview=in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist);
        dest.writeString(name);
        dest.writeString(album);
        dest.writeString(image);
        dest.writeString(preview);
    }
    public static final Parcelable.Creator<SpotifyTrack> CREATOR = new Parcelable.Creator<SpotifyTrack>(){
        @Override
        public SpotifyTrack createFromParcel(Parcel source) {
            return new SpotifyTrack(source);
        }

        @Override
        public SpotifyTrack[] newArray(int size) {
            return new SpotifyTrack[size];
        }
    };
}
