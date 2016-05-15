package com.beta.mal.play.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.beta.mal.play.Movies;
import com.beta.mal.play.R;
import com.squareup.picasso.Picasso;

public class Adapter extends CursorAdapter {

    Context mContext;
    String baseURL = "http://image.tmdb.org/t/p/w342/";
    int mNumber = 0;

    ///////////////////////////////////////////////////////////////////
    public Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;

    }

    ///////////////////////////////////////////////////////////////////
    public static String getSortBy(Context context) {
        return Movies.getSortBy();
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
        mNumber++;
        Log.d("Photon", "Element number " + mNumber + " is displayed");
        return view;
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String image = cursor.getString(MoviesView.COLUMN_POSTER_PATH);
        String imageURL = baseURL + image;
        ImageView movieImageView = (ImageView) view.findViewById(R.id.MoviePoster);
        Picasso.with(context).load(imageURL).into(movieImageView);
        Log.d("Photon", "Binding image to ImageView");
    }


}
