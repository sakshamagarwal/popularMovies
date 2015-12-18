package com.example.saksham.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by saksham on 18/12/15.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    final String BASE_IMAGE_URL = getContext().getString(R.string.image_base_url);

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext())
                .load(BASE_IMAGE_URL + movie.getPosterPath())
                .into(iconView);

        /*TextView versionNameView = (TextView) convertView.findViewById(R.id.flavor_text);
        versionNameView.setText(androidFlavor.versionName
                + " - " + androidFlavor.versionNumber );*/

        return convertView;
    }
}
