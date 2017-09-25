package com.example.pc.movies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by PC on 9/30/2016.
 */
public class Movie  implements Parcelable {
    public String poster_path;
    public String overview;
    public String original_title;
    public double vote_average;
    public int id;
    public String release_date;

    public Movie(Parcel input)
    {
        poster_path=input.readString();
        overview=input.readString();
        original_title=input.readString();
        vote_average=input.readDouble();
        id=input.readInt();
        release_date=input.readString();
    }

    public Movie() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(original_title);
        parcel.writeDouble(vote_average);
        parcel.writeInt(id);
        parcel.writeString(release_date);

    }
    @Expose
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
