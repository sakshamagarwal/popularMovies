package com.example.saksham.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saksham on 18/12/15.
 */
public class Movie_Item implements Parcelable {
    private String title, plot, user_rating, release_date, poster_path, id;

    public Movie_Item(String movieId, String movieTitle, String moviePosterPath, String moviePlot, String movieRating, String movieReleaseDate) {
        id = movieId;
        title = movieTitle;
        poster_path = moviePosterPath;
        plot = moviePlot;
        user_rating = movieRating;
        release_date = movieReleaseDate;
    }

    private Movie_Item(Parcel in) {
        id = in.readString();
        title = in.readString();
        plot = in.readString();
        user_rating = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getPlot() {
        return plot;
    }

    public String getUserRating() {
        return user_rating;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getId());
        parcel.writeString(getTitle());
        parcel.writeString(getPlot());
        parcel.writeString(getUserRating());
        parcel.writeString(getReleaseDate());
        parcel.writeString(getPosterPath());
    }

    public static final Creator<Movie_Item> CREATOR = new Creator<Movie_Item>() {

        @Override
        public Movie_Item createFromParcel(Parcel parcel) {
            return new Movie_Item(parcel);
        }

        @Override
        public Movie_Item[] newArray(int i) {
            return new Movie_Item[i];
        }
    };
}
