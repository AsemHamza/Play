package com.beta.mal.play;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.beta.mal.play.R;
import com.beta.mal.play.Sync.PlaySyncAdapter;
import com.beta.mal.play.Fragments.MoviesView;


public class Movies extends AppCompatActivity implements MoviesView.Callback {



    private boolean mTwoPane;
    FragmentManager FM = getFragmentManager();
    FragmentTransaction T = FM.beginTransaction();
    MoviesView moviesView = new MoviesView();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        T.add(R.id.main_layout, moviesView, "moviesView");
        T.commit();
        PlaySyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_pop) {

            PlaySyncAdapter.sorting = "popular";
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(PlaySyncAdapter.getSyncAccount(this), getString(R.string.content_authority), bundle);
            finish();
            startActivity(getIntent());
            return true;
        }
        if (id == R.id.action_sort_top) {

            PlaySyncAdapter.sorting = "top_rated";
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(PlaySyncAdapter.getSyncAccount(this), getString(R.string.content_authority), bundle);
            finish();
            startActivity(getIntent());
            return true;
        }
        if (id == R.id.action_refresh) {

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

    @Override
    public void onItemSelected(Uri dateUri) {
        Intent intent = new Intent(this, Details.class)
                .setData(dateUri);
        startActivity(intent);
    }

    @Override
    public void onRestart(){
        finish();
        startActivity(getIntent());
        super.onRestart();
    }
}
