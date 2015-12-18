package com.example.saksham.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private MovieAdapter movieAdapter;
    final String BASE_API_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=";
    final String API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    final String APPEND_KEY = "&api_key=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        movieAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        GridView gridView = (GridView)findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("selectedMovie", movieAdapter.getItem(i));
                startActivity(intent);
            }
        });
        String sort_pref = sharedPreferences.getString(getString(R.string.pref_sorting_key), getString(R.string.pref_sorting_popularity));
        String url = BASE_API_URL + sort_pref + APPEND_KEY + API_KEY;

        new DownloadMovieList().execute(url);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class DownloadMovieList extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                //moviesJsonStr =

            } catch (IOException e) {
                Log.e("DOWNLOAD_ERROR", "error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DOWNLOAD_ERROR", "Error closing stream", e);
                    }
                }
            }

            try {
                JSONObject moviesJson = new JSONObject(moviesJsonStr);
                JSONArray moviesJsonArray = moviesJson.getJSONArray("results");
                return getMoviesArray(moviesJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        private Movie[] getMoviesArray(JSONArray moviesJsonArray) throws JSONException {
            final String TITLE = "title";
            final String PLOT = "overview";
            final String RATING = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String POSTER_PATH = "poster_path";

            Movie[] movies = new Movie[moviesJsonArray.length()];

            for (int i = 0; i < moviesJsonArray.length(); ++i) {
                JSONObject movie_details = moviesJsonArray.getJSONObject(i);
                movies[i] = new Movie(
                        movie_details.getString(TITLE),
                        movie_details.getString(POSTER_PATH),
                        movie_details.getString(PLOT),
                        movie_details.getString(RATING),
                        movie_details.getString(RELEASE_DATE));
            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            movieAdapter.clear();
            for (Movie movie : result) {
                movieAdapter.add(movie);
            }

        }
    }

}
