package com.beta.mal.play.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.beta.mal.play.Data.PlayContract;
import com.beta.mal.play.R;
import com.beta.mal.play.Fragments.Adapter;


public class MoviesView extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private Adapter gridViewAdapter;
    private GridView gridView;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    private static final int PLAY_LOADER = 0;

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_POSTER_PATH = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_RELEASE_DATE = 3;
    public static final int COLUMN_MOVIE_ID = 4;

    private static final String[] DETAIL_COLUMNS = {
            PlayContract.MovieEntry.TABLE_NAME + "." + PlayContract.MovieEntry._ID,
            PlayContract.MovieEntry.COLUMN_POSTER_PATH,
            PlayContract.MovieEntry.COLUMN_TITLE,
            PlayContract.MovieEntry.COLUMN_RELEASE_DATE,
            PlayContract.MovieEntry.COLUMN_ID
    };


    public MoviesView() {
        // Required empty public constructor
    }

    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_movies, container, false);

        gridViewAdapter = new Adapter(getActivity(), null, 0);
        gridView = (GridView) layout.findViewById(R.id.Rec);
        gridView.setAdapter(gridViewAdapter);
        Log.d("Photon","Setting Adapter Done");
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Log.d("Photon","Item Clicked");
                if (cursor != null) {
                    int movieId = cursor.getInt(COLUMN_MOVIE_ID);
                    ((Callback) getActivity())
                            .onItemSelected(PlayContract.MovieEntry.buildMovieURL(movieId));


                }
                mPosition = position;
            }

        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The GridView probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        ;

        return layout;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to gridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != gridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLAY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        Log.i("Photon", "onActivityCreated");
    }


    void onSortByChange() {
        getLoaderManager().restartLoader(PLAY_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri MoviesUri = PlayContract.MovieEntry.buildMoviesURL();
        String sortOrder;
        if (Adapter.getSortBy(getActivity()).equals("vote_average.desc")) {
            sortOrder = PlayContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        } else if (Adapter.getSortBy(getActivity()).equals("favorite_movie.desc")) {
            sortOrder = PlayContract.MovieEntry.COLUMN_FAV + " DESC";
        } else {
            sortOrder = null;
        }


        return new CursorLoader(getActivity(),
                MoviesUri,
                DETAIL_COLUMNS,
                null,
                null,
                "ROWID LIMIT 18");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        gridViewAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        gridViewAdapter.swapCursor(null);
    }

}
