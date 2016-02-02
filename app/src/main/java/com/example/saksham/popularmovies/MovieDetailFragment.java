package com.example.saksham.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by saksham on 2/2/16.
 */
public class MovieDetailFragment extends Fragment {

    Movie_Item selectedMovie;
    LinearLayout parent;

    public static final String SELECTED_MOVIE = "selected";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            selectedMovie = args.getParcelable(SELECTED_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        parent = (LinearLayout)rootView.findViewById(R.id.movie_details_linearlayout);


        TextView title = (TextView)rootView.findViewById(R.id.title);
        TextView plot = (TextView)rootView.findViewById(R.id.plot);
        TextView releaseDate = (TextView)rootView.findViewById(R.id.release_date);
        TextView rating = (TextView)rootView.findViewById(R.id.rating);
        ImageView poster = (ImageView)rootView.findViewById(R.id.movie_poster);
        Button addToFavourite = (Button)rootView.findViewById(R.id.add_favourite);

        addToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                if (preferences.contains(selectedMovie.getId())) {
                    Toast.makeText(getActivity(), "Movie already in favourites", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(selectedMovie.getId(), selectedMovie.getTitle());
                    editor.commit();
                }
            }
        });

        if (selectedMovie != null) {
            title.setText(selectedMovie.getTitle());
            plot.setText(selectedMovie.getPlot());
            releaseDate.setText("Release Date: " + selectedMovie.getReleaseDate());
            rating.setText("User Rating: " + selectedMovie.getUserRating());
            Picasso.with(getActivity()).load(getString(R.string.image_base_url) + selectedMovie.getPosterPath()).into(poster);

            download_reviews();
            download_trailer_url();
        }
        return rootView;

    }

    private void download_reviews() {
        String reviews_url = getString(R.string.BASE_API_URL) +
                getString(R.string.MOVIE) +
                selectedMovie.getId() +
                getString(R.string.REVIEWS) +
                getString(R.string.API_KEY);

        new DownloadWebPage(new DownloadWebPage.UseJsonData() {
            @Override
            public void onResult(JSONObject object) {
                if (object == null) {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONArray results = object.getJSONArray("results");
                    for (int i = 0; i < results.length(); ++i) {
                        RelativeLayout review_child = (RelativeLayout)getActivity().getLayoutInflater().inflate(R.layout.review_item, null);
                        TextView content = (TextView)review_child.findViewById(R.id.review_content);
                        TextView author = (TextView)review_child.findViewById(R.id.author);
                        content.setText(results.getJSONObject(i).getString("content"));
                        author.setText(results.getJSONObject(i).getString("author"));
                        parent.addView(review_child);

                    }
//                    String author = results.getJSONObject(0).getString("author");
//                    Toast.makeText(MovieDetailsActivity.this, author, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(reviews_url);

    }

    private void download_trailer_url() {
        String videos_url = getString(R.string.BASE_API_URL) +
                getString(R.string.MOVIE) +
                selectedMovie.getId() +
                getString(R.string.TRAILER) +
                getString(R.string.API_KEY);

        new DownloadWebPage(new DownloadWebPage.UseJsonData() {
            @Override
            public void onResult(JSONObject object) {
                if (object == null) {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONArray results = object.getJSONArray("results");
                    for (int i = 0; i < results.length(); ++i) {
                        Button b = new Button(getActivity());
                        b.setText(results.getJSONObject(i).getString("name"));
                        final String trailer_url = results.getJSONObject(i).getString("key");
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (trailer_url == null) {
                                    download_trailer_url();
                                    Toast.makeText(getActivity(), "Trailer url not ready yet", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer_url)));
                            }
                        });
                        parent.addView(b);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(videos_url);
    }
}
