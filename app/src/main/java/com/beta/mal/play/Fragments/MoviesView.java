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


public class MoviesView extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Adapter gridViewAdapter;
    private GridView gridView;
    private int mPosition = GridView.INVALID_POSITION;
    public static boolean showFav = false;
    private static final String SELECTED_KEY = "selected_position";
    private static final int PLAY_LOADER = 0;
    public static final int COLUMN_ID = 1;
    public static final int COLUMN_POSTER_PATH = 2;
    public static final int COLUMN_FAV = 3;
    private static final String[] DETAIL_COLUMNS = {
            PlayContract.MovieEntry.TABLE_NAME + "." + PlayContract.MovieEntry._ID,
            PlayContract.MovieEntry.COLUMN_ID,
            PlayContract.MovieEntry.COLUMN_POSTER_PATH,
            PlayContract.MovieEntry.COLUMN_FAV,
    };

    ///////////////////////////////////////////////////////////////////
    public MoviesView() {
        // Required empty public constructor
    }

    ///////////////////////////////////////////////////////////////////
    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_movies, container, false);
        gridViewAdapter = new Adapter(getActivity(), null, 0);
        gridView = (GridView) layout.findViewById(R.id.Rec);
        gridView.setAdapter(gridViewAdapter);
        Log.d("Photon", "Setting Adapter Done");
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            ///////////////////////////////////////////////////////////////////
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Log.d("Photon", "Item Clicked");
                if (cursor != null) {
                    int movieId = cursor.getInt(COLUMN_ID);
                    ((Callback) getActivity())
                            .onItemSelected(PlayContract.MovieEntry.buildMovieURL(movieId));
                }
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return layout;
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != gridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLAY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        Log.i("Photon", "onActivityCreated");
    }

    ///////////////////////////////////////////////////////////////////
    void onSortByChange() {
        getLoaderManager().restartLoader(PLAY_LOADER, null, this);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri MoviesUri = PlayContract.MovieEntry.buildMoviesURL();
        String[] sortOrder = {Adapter.getSortBy(getActivity())};
        String sort_type = "sort=?";
        if (Adapter.getSortBy(getActivity()).equals("fav")) {
            sort_type = "favorite=?";
            sortOrder[0] = "1";
        }
        return new CursorLoader(getActivity(),
                MoviesUri,
                DETAIL_COLUMNS,
                sort_type,
                sortOrder,
                null);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst() && showFav) {
            int x = data.getCount();
            int favorite = data.getInt(COLUMN_FAV);
            if (favorite == 1) {
                gridViewAdapter.swapCursor(data);
                if (mPosition != GridView.INVALID_POSITION) {
                    gridView.smoothScrollToPosition(mPosition);
                }
            }
        } else if (data != null && data.moveToFirst() && !showFav) {
            gridViewAdapter.swapCursor(data);
            if (mPosition != GridView.INVALID_POSITION) {
                gridView.smoothScrollToPosition(mPosition);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        gridViewAdapter.swapCursor(null);
    }

}
