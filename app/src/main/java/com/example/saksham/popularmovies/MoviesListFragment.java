package com.example.saksham.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by saksham on 28/1/16.
 */
public class MoviesListFragment extends Fragment /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {

    final String ID = "id";
    final String TITLE = "title";
    final String PLOT = "overview";
    final String RATING = "vote_average";
    final String RELEASE_DATE = "release_date";
    final String POSTER_PATH = "poster_path";
    private MovieAdapter movieAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie_Item>());
        GridView gridView = (GridView)rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((Callback) getActivity()).onItemSelected((Movie_Item) adapterView.getItemAtPosition(i));

            }
        });
        String sort_pref = sharedPreferences.getString(getString(R.string.pref_sorting_key), getString(R.string.pref_sorting_popularity));
        updateMovies(sort_pref);
        return rootView;
    }

    public void updateMovies(String sort_pref) {
        //Toast.makeText(getActivity(), getString(R.string.pref_sorting_key, R.string.pref_sorting_favourites), Toast.LENGTH_LONG).show();
        if (sort_pref.equals(getString(R.string.pref_sorting_favourites))) {
            Toast.makeText(getActivity(), "Showing Favourites", Toast.LENGTH_LONG).show();
            loadFavouriteMovies();
        } else {
            String url = getString(R.string.BASE_API_URL) +
                    getString(R.string.DISCOVER) +
                    sort_pref + getString(R.string.APPEND_KEY) + getString(R.string.API_KEY);

            new DownloadWebPage(new DownloadWebPage.UseJsonData() {
                @Override
                public void onResult(JSONObject object) {
                    if (object == null) {
                        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                    }

                    try {
                        JSONArray moviesJsonArray = object.getJSONArray("results");
                        Movie_Item[] movies = getMoviesArray(moviesJsonArray);
                        movieAdapter.clear();
                        for (Movie_Item movie : movies) {
                            movieAdapter.add(movie);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(url);
        }
    }

    private void loadFavouriteMovies() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Map<String,?> keys = preferences.getAll();
        movieAdapter.clear();
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String url = getString(R.string.BASE_API_URL) + getString(R.string.MOVIE) + entry.getKey().toString() + "?api_key=" + getString(R.string.API_KEY);
            new DownloadWebPage(new DownloadWebPage.UseJsonData() {
                @Override
                public void onResult(JSONObject object) {
                    if (object == null) {
                        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                    }

                    try {
                        String title = object.getString(TITLE);
                        String poster_path = object.getString(POSTER_PATH);
                        movieAdapter.add(
                                new Movie_Item(
                                        object.getString(ID),
                                        object.getString(TITLE),
                                        object.getString(POSTER_PATH),
                                        object.getString(PLOT),
                                        object.getString(RATING),
                                        object.getString(RELEASE_DATE))
                        );

                        JSONArray moviesJsonArray = object.getJSONArray("results");
                        Movie_Item[] movies = getMoviesArray(moviesJsonArray);
                        movieAdapter.clear();
                        for (Movie_Item movie : movies) {
                            movieAdapter.add(movie);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(url);
        }
    }

    private Movie_Item[] getMoviesArray(JSONArray moviesJsonArray) throws JSONException {


        Movie_Item[] movies = new Movie_Item[moviesJsonArray.length()];

        for (int i = 0; i < moviesJsonArray.length(); ++i) {
            JSONObject movie_details = moviesJsonArray.getJSONObject(i);
            movies[i] = new Movie_Item(
                    movie_details.getString(ID),
                    movie_details.getString(TITLE),
                    movie_details.getString(POSTER_PATH),
                    movie_details.getString(PLOT),
                    movie_details.getString(RATING),
                    movie_details.getString(RELEASE_DATE));
        }
        return movies;
    }

    public interface Callback {
        void onItemSelected(Movie_Item movie);
    }

}
