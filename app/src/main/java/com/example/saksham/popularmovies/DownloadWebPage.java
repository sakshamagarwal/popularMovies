package com.example.saksham.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saksham on 18/12/15.
 */
public class DownloadWebPage extends AsyncTask<String, Void, JSONObject> {

    UseJsonData callback;

    public interface UseJsonData {
        void onResult(JSONObject object);
    }

    public DownloadWebPage(UseJsonData callback) {
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
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

        JSONObject moviesJson = null;

        try {
            moviesJson = new JSONObject(moviesJsonStr);
//            JSONArray moviesArray = moviesJson.getJSONArray("results");
//            String s = moviesArray.getJSONObject(0).getString("backdrop_path");
//            Log.e("movies_array", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return moviesJson;
    }

    @Override
    protected void onPostExecute(JSONObject movies) {
        callback.onResult(movies);
    }
}
