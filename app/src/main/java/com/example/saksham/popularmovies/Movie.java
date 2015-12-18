package com.example.saksham.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saksham on 18/12/15.
 */
public class Movie implements Parcelable {
    private String title, plot, user_rating, release_date, poster_path;

    public Movie(String movieTitle, String moviePosterPath, String moviePlot, String movieRating, String movieReleaseDate) {
        title = movieTitle;
        poster_path = moviePosterPath;
        plot = moviePlot;
        user_rating = movieRating;
        release_date = movieReleaseDate;
    }

    private Movie(Parcel in) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getTitle());
        parcel.writeString(getPlot());
        parcel.writeString(getUserRating());
        parcel.writeString(getReleaseDate());
        parcel.writeString(getPosterPath());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
