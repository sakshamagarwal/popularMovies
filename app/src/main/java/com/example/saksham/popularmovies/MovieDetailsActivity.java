package com.example.saksham.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetailsActivity extends AppCompatActivity {
    //final String BASE_IMAGE_URL = this.getString(R.string.image_base_url);
    final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Movie selectedMovie = (Movie)getIntent().getExtras().getParcelable("selectedMovie");
        TextView title = (TextView)findViewById(R.id.title);
        TextView plot = (TextView)findViewById(R.id.plot);
        TextView releaseDate = (TextView)findViewById(R.id.release_date);
        TextView rating = (TextView)findViewById(R.id.rating);
        ImageView poster = (ImageView)findViewById(R.id.movie_poster);

        title.setText(selectedMovie.getTitle());
        plot.setText(selectedMovie.getPlot());
        releaseDate.setText("Release Date: " + selectedMovie.getReleaseDate());
        rating.setText("User Rating: " + selectedMovie.getUserRating());
        Picasso.with(this).load(BASE_IMAGE_URL + selectedMovie.getPosterPath()).into(poster);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
