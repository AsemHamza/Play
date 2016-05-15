package com.beta.mal.play;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.beta.mal.play.Fragments.DetailsView;
import com.beta.mal.play.Fragments.MoviesView;
import com.beta.mal.play.Sync.PlaySyncAdapter;


public class Movies extends AppCompatActivity implements MoviesView.Callback {


    private boolean mTwoPane;
    FragmentManager FM = getFragmentManager();
    FragmentTransaction T = FM.beginTransaction();
    MoviesView moviesView = new MoviesView();
    DetailsView detailsView = new DetailsView();
    static String sort_type = "popular";

    public static String getSortBy() {
        return sort_type;
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        LinearLayout dView = (LinearLayout) findViewById(R.id.main_layout2);
        if (tabletSize) {
            mTwoPane = true;
        }
        if (dView == null) {
            mTwoPane = false;
            Log.d("Photon", "One Pane");
        } else {
            mTwoPane = true;
            Log.d("Photon", "Two Pane");
        }
        T.add(R.id.main_layout, moviesView, "moviesView");
        T.commit();
        PlaySyncAdapter.initializeSyncAdapter(this);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_pop) {
            sort_type = "popular";
            moviesView.showFav = false;
            PlaySyncAdapter.sorting = sort_type;
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(PlaySyncAdapter.getSyncAccount(this), getString(R.string.content_authority), bundle);
            finish();
            startActivity(getIntent());
            return true;
        }
        if (id == R.id.action_sort_top) {
            sort_type = "top_rated";
            moviesView.showFav = false;
            PlaySyncAdapter.sorting = sort_type;
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(PlaySyncAdapter.getSyncAccount(this), getString(R.string.content_authority), bundle);
            finish();
            startActivity(getIntent());
            return true;
        }
        if (id == R.id.action_favorite) {
            sort_type = "fav";
            moviesView.showFav = true;
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(PlaySyncAdapter.getSyncAccount(this), getString(R.string.content_authority), bundle);
            finish();
            startActivity(getIntent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onItemSelected(Uri movieUri) {
        if (!mTwoPane) {
            Intent intent = new Intent(this, Details.class)
                    .setData(movieUri);
            startActivity(intent);
        } else {
            Bundle extras = new Bundle();
            extras.putParcelable("URI", movieUri);
            detailsView.setArguments(extras);
            invalidateOptionsMenu();
            T.replace(R.id.main_layout2, detailsView);
            T.commit();
        }
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }
}
