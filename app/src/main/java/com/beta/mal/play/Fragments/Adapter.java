package com.beta.mal.play.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.beta.mal.play.Fragments.MoviesView;
import com.beta.mal.play.R;
import com.squareup.picasso.Picasso;

public class Adapter extends CursorAdapter{

    Context mContext;
    String baseURL = "http://image.tmdb.org/t/p/w342/";
    int mNumber = 0;


    public Adapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;

    }

    /*public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.grid_list_item, parent, false);
        //View view = inflater.inflate(R.layout.grid_box_item,parent,false);
        //View view = inflater.inflate(R.layout.grid_grid_item,parent,false);
        MovieViewHolder holder = new MovieViewHolder(view);
        return holder;
    }*/


    public static String getSortBy(Context context) {
       return "popular";
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
        mNumber++;
        Log.d("Photon","Element number "+mNumber+" is displayed");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String image = cursor.getString(MoviesView.COLUMN_POSTER_PATH);
        String imageURL = baseURL + image;
        ImageView movieImageView = (ImageView) view.findViewById(R.id.MoviePoster);
        Picasso.with(context).load(imageURL).into(movieImageView);

        /*TextView tilte = (TextView) view.findViewById(R.id.MovieTitle);
        tilte.setText(cursor.getString(Main_GridListView.COLUMN_TITLE));

        TextView releaseDate = (TextView) view.findViewById(R.id.MovieReleaseDate);
        releaseDate.setText(cursor.getString(Main_GridListView.COLUMN_RELEASE_DATE));*/


        Log.d("Photon","Binding image to ImageView");

    }


}
