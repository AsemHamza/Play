package com.beta.mal.play.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beta.mal.play.Data.PlayContract;
import com.beta.mal.play.R;
import com.squareup.picasso.Picasso;


public class DetailsView extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {


    private static final String LOG_TAG = DetailsView.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    boolean fav;
    private Uri mUri;
    public static final String DETAIL_URI = "URI";
    String key;
    int movieID;
    String baseURL = "http://image.tmdb.org/t/p/w185/";
    private ShareActionProvider mShareActionProvider;
    String movieTitle;
    private static final String[] DETAIL_COLUMNS = {
            PlayContract.MovieEntry.TABLE_NAME + "." + PlayContract.MovieEntry._ID,
            PlayContract.MovieEntry.COLUMN_BACKDROP_PATH,
            PlayContract.MovieEntry.COLUMN_OVERVIEW,
            PlayContract.MovieEntry.COLUMN_RELEASE_DATE,
            PlayContract.MovieEntry.COLUMN_POSTER_PATH,
            PlayContract.MovieEntry.COLUMN_TITLE,
            PlayContract.MovieEntry.COLUMN_VIDEO,
            PlayContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            PlayContract.MovieEntry.COLUMN_FAV,
            PlayContract.ReviewEntry.COLUMN_REVIEW,
            PlayContract.ReviewEntry.COLUMN_AUTHOR,
            PlayContract.TrailerEntry.COLUMN_ADDRESS,
            PlayContract.TrailerEntry.COLUMN_MOVIE_NAME,
    };
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_BACKDROP_PATH = 1;
    public static final int COLUMN_OVERVIEW = 2;
    public static final int COLUMN_RELEASE_DATE = 3;
    public static final int COLUMN_POSTER_PATH = 4;
    public static final int COLUMN_TITLE = 5;
    public static final int COLUMN_VIDEO = 6;
    public static final int COLUMN_VOTE_AVERAGE = 7;
    public static final int COLUMN_FAV = 8;
    public static final int COLUMN_REVIEW = 9;
    public static final int COLUMN_AUTHOR = 10;
    public static final int COLUMN_ADDRESS = 11;
    public static final int COLUMN_NAME = 12;


    private ImageView mMoviePoster;
    private TextView mMovieTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mMovieOverview;

    private TextView mReviewTV;

    private TextView mName;
    private ImageView mVideo;
    private ImageView mFavorite;
    private ImageView mShare;

    ///////////////////////////////////////////////////////////////////
    public DetailsView() {
        setHasOptionsMenu(false);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailsView.DETAIL_URI);
        }
        Log.d("Photon", "DetailsView - onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ///////////////////////////////////////////////////////////////////
        //movie
        mMoviePoster = (ImageView) rootView.findViewById(R.id.detail_icon);
        mMovieTitle = (TextView) rootView.findViewById(R.id.title_textview);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_year_textview);
        mVoteAverage = (TextView) rootView.findViewById(R.id.vote_average_textview);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_overview_textview);
        mFavorite = (ImageView) rootView.findViewById(R.id.favorite_image_view);
        mFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                if (fav == false) {
                    ContentValues favorite = new ContentValues();
                    favorite.put(PlayContract.MovieEntry.COLUMN_FAV, 1);
                    getActivity().getContentResolver().update(PlayContract.MovieEntry.buildMoviesURL(),
                            favorite,
                            PlayContract.MovieEntry.TABLE_NAME +
                                    "." + PlayContract.MovieEntry.COLUMN_ID + " = ? ",
                            new String[]{Integer.toString(PlayContract.MovieEntry.getMovieIdFromUri(mUri))});
                    Log.d("Photon", "DetailsView - Favorite success");
                    mFavorite.setBackgroundResource(R.drawable.ic_favorite_white_18dp);
                } else if (fav == true) {
                    ContentValues favorite = new ContentValues();
                    favorite.put(PlayContract.MovieEntry.COLUMN_FAV, 0);
                    getActivity().getContentResolver().update(PlayContract.MovieEntry.buildMoviesURL(),
                            favorite,
                            PlayContract.MovieEntry.TABLE_NAME +
                                    "." + PlayContract.MovieEntry.COLUMN_ID + " = ? ",
                            new String[]{Integer.toString(PlayContract.MovieEntry.getMovieIdFromUri(mUri))});
                    Log.d("Photon", "DetailsView - deFavorite success");
                    mFavorite.setBackgroundResource(R.drawable.ic_favorite_outline_white_18dp);
                }
            }
        });
        ///////////////////////////////////////////////////////////////////
        //review
        mReviewTV = (TextView) rootView.findViewById(R.id.list_item_review);
        ///////////////////////////////////////////////////////////////////
        //video
        mName = (TextView) rootView.findViewById(R.id.list_item_trailer);
        mVideo = (ImageView) rootView.findViewById(R.id.list_item_video);
        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(getActivity(), key);

            }
        });
        mShare = (ImageView) rootView.findViewById(R.id.share_image_view);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createShareVideoURL();

            }
        });

        return rootView;
    }

    ///////////////////////////////////////////////////////////////////
    public static void watchYoutubeVideo(Context context, String videoID) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID));
            context.startActivity(i);
        }
    }

    ///////////////////////////////////////////////////////////////////
    private void createShareVideoURL() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, movieTitle + "  "
                + "http://www.youtube.com/watch?v=" + key);
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (mUri != null) {
            return new CursorLoader(getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            movieTitle = data.getString(COLUMN_TITLE);
            mMovieTitle.setText(movieTitle);
            movieID = data.getInt(COLUMN_ID);
            String image = data.getString(COLUMN_BACKDROP_PATH);
            String imageURL = baseURL + image;
            Picasso.with(getActivity()).load(imageURL).into(mMoviePoster);
            String movieDate = data.getString(COLUMN_RELEASE_DATE);
            mReleaseDate.setText(movieDate.substring(0, 4));
            String voteAverage = data.getString(COLUMN_VOTE_AVERAGE);
            mVoteAverage.setText(voteAverage + "/10");
            String movieOverview = data.getString(COLUMN_OVERVIEW);
            mMovieOverview.setText(movieOverview);
            int favorite = data.getInt(COLUMN_FAV);
            if (favorite == 1) {
                mFavorite.setBackgroundResource(R.drawable.ic_favorite_white_18dp);
                fav = true;
            } else {
                mFavorite.setBackgroundResource(R.drawable.ic_favorite_outline_white_18dp);
                fav = false;
            }
            ///////////////////////////////////////////////////////////////////
            //review
            String review = data.getString(COLUMN_REVIEW);
            String author = data.getString(COLUMN_AUTHOR);
            if (author.equals(".")) {
                mReviewTV.setText(review + ".");
            } else {
                mReviewTV.setText(review + "  By " + author);
            }
            ///////////////////////////////////////////////////////////////////
            //video
            key = data.getString(COLUMN_ADDRESS);
            String name = data.getString(COLUMN_NAME);
            mName.setText(name);
        }
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.favorite_image_view) {
            ContentValues cv = new ContentValues();
            cv.put("favorite", "fav");
            String[] fav = {"fav"};
            mFavorite.setBackgroundResource(R.drawable.ic_favorite_white_18dp);
        }
    }
}
