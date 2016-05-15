package com.beta.mal.play.Sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.beta.mal.play.Data.PlayContract;
import com.beta.mal.play.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class PlaySyncAdapter extends AbstractThreadedSyncAdapter{


    public static  String sorting = "popular";
    ArrayList<Integer> moviesIds = new ArrayList<Integer>();
    public final String LOG_TAG = PlaySyncAdapter.class.getSimpleName();
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    public PlaySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        Log.d("PHOTON",sorting);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesListJsonStr = null;
        String trailersListJsonStr = null;
        String reviewsListJsonStr = null;
        String apiKey = "a6a5b3f1deac6542b9916ba6d06ad267";
        try {
            final String PLAY_BASE_URL =
                    "http://api.themoviedb.org/3/movie/" +sorting+"?";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(PLAY_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            moviesListJsonStr = buffer.toString();
            ArrayList<Integer> moviesID = getMovieDataFromJson(moviesListJsonStr);
            Log.d("PHOTON",moviesListJsonStr);
            for(int i = 0; i < moviesID.size(); i++){
                ///////////////////////////////////////////////////////////////////
                //https://api.themoviedb.org/3/movie/211672/videos?api_key=[YOUR API KEY]
                final String VIDEO_BASE_URL =
                        "https://api.themoviedb.org/3/movie/"+ moviesID.get(i)+ "/videos?";
                Uri builtVideoUrl = Uri.parse(VIDEO_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM,apiKey)
                        .build();
                URL videoUrl = new URL(builtVideoUrl.toString());
                urlConnection = (HttpURLConnection) videoUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream videoInputStream = urlConnection.getInputStream();
                StringBuffer videoBuffer = new StringBuffer();
                if (videoInputStream == null) {
                    // Nothing to do.
                    // return;
                }
                reader = new BufferedReader(new InputStreamReader(videoInputStream));
                String videoLine;
                while ((videoLine = reader.readLine()) != null) {
                    videoBuffer.append(videoLine + "\n");
                }
                if (videoBuffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                }
                trailersListJsonStr = videoBuffer.toString();
                getTrailerFormJson(trailersListJsonStr, moviesID.get(i));
                ///////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////
                //https://api.themoviedb.org/3/movie/211672/reviews?api_key=[YOUR API KEY]
                final String REVIEW_BASE_URL =
                        "https://api.themoviedb.org/3/movie/" + moviesID.get(i)+ "/reviews?";
                Uri builtReviewUrl = Uri.parse(REVIEW_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                URL reviewUrl = new URL(builtReviewUrl.toString());
                urlConnection = (HttpURLConnection) reviewUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream reviewInputStream = urlConnection.getInputStream();
                StringBuffer reviewBuffer = new StringBuffer();
                if (reviewInputStream == null) {
                    // Nothing to do.
                    // return;
                }
                reader = new BufferedReader(new InputStreamReader(reviewInputStream));
                String reviewLine;
                while ((reviewLine = reader.readLine()) != null) {
                    reviewBuffer.append(reviewLine + "\n");
                }
                if (reviewBuffer.length() == 0) {
                    // Nothing to do.
                    // return;
                }
                reviewsListJsonStr = reviewBuffer.toString();
                getReviewFromJson(reviewsListJsonStr, moviesID.get(i));
            }
            Log.d(LOG_TAG, "Fetch is Complete. " + moviesID.size() + " Inserted" );
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<Integer> getMovieDataFromJson(String MoviesJsonStr)
            throws JSONException {
        final String MP_BACKDROP_PATH = "backdrop_path";
        final String MP_ID = "id";
        final String MP_OVERVIEW = "overview";
        final String MP_RELEASE_DATE = "release_date";
        final String MP_POSTER_PATH = "poster_path";
        final String MP_TITLE = "title";
        final String MP_VIDEO = "video";
        final String MP_VOTE_AVERAGE = "vote_average";
        final String MP_RESULTS = "results";
        try {
            JSONObject MoviesListJson = new JSONObject(MoviesJsonStr);
            JSONArray moviesArray = MoviesListJson.getJSONArray(MP_RESULTS);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());
            for (int i = 0; i < moviesArray.length(); i++) {
                String backdropPath;
                String overview;
                String releaseDate;
                String posterPath;
                String title;
                String video;
                String voteAverage;
                String sort_type;
                int movieId;
                JSONObject fullMovie = moviesArray.getJSONObject(i);
                backdropPath = fullMovie.getString(MP_BACKDROP_PATH);
                overview = fullMovie.getString(MP_OVERVIEW);
                releaseDate = fullMovie.getString(MP_RELEASE_DATE);
                posterPath = fullMovie.getString(MP_POSTER_PATH);
                title = fullMovie.getString(MP_TITLE);
                video = fullMovie.getString(MP_VIDEO);
                voteAverage = fullMovie.getString(MP_VOTE_AVERAGE);
                movieId = fullMovie.getInt(MP_ID);
                sort_type = sorting;
                ContentValues movieValues = new ContentValues();
                movieValues.put(PlayContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
                movieValues.put(PlayContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(PlayContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(PlayContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(PlayContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(PlayContract.MovieEntry.COLUMN_VIDEO, video);
                movieValues.put(PlayContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                movieValues.put(PlayContract.MovieEntry.COLUMN_ID, movieId);
                movieValues.put(PlayContract.MovieEntry.COLUMN_FAV, 0);
                movieValues.put(PlayContract.MovieEntry.SORT_TYPE,sort_type);
                Log.d("Photon","Movie : "+title+"  sorting = " + sort_type);
                if (!movieExists(moviesIds, movieId)) {
                    moviesIds.add(movieId);
                    cVVector.add(movieValues);
                }
                if (cVVector.size() > 0) {
                    ContentValues cv = new ContentValues(cVVector.size());
                    cv.equals(cVVector);
                    getContext().getContentResolver().insert(PlayContract.MovieEntry.CONTENT_URI, movieValues);
                }
            }
            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return moviesIds;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void getReviewFromJson(String reviewJsonStr, int movieId)
            throws JSONException {
        final String MP_RESULTS = "results";
        final String MP_REVIEW_ID = "id";
        final String MP_AUTHOR = "author";
        final String MP_REVIEW = "content";
        try {
            JSONObject reviewJSON = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJSON.getJSONArray(MP_RESULTS);
            if( reviewArray.length() > 0){
                for(int i = 0; i < 1; i++){
                    String review;
                    String author;
                    String reviewId;
                    JSONObject fullReview = reviewArray.getJSONObject(i);
                    review = fullReview.getString(MP_REVIEW);
                    author = fullReview.getString(MP_AUTHOR);
                    reviewId = fullReview.getString(MP_REVIEW_ID);
                    ContentValues reviewValue = new ContentValues();
                    reviewValue.put(PlayContract.ReviewEntry.COLUMN_AUTHOR, author);
                    reviewValue.put(PlayContract.ReviewEntry.COLUMN_REVIEW, review);
                    reviewValue.put(PlayContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
                    reviewValue.put(PlayContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                    getContext().getContentResolver().delete(PlayContract.ReviewEntry.CONTENT_URI,
                            PlayContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movieId)});
                    getContext().getContentResolver().insert(PlayContract.ReviewEntry.CONTENT_URI, reviewValue);
                }
            }else{
                ContentValues reviewValue = new ContentValues();
                reviewValue.put(PlayContract.ReviewEntry.COLUMN_AUTHOR, "_");
                reviewValue.put(PlayContract.ReviewEntry.COLUMN_REVIEW, "_");
                reviewValue.put(PlayContract.ReviewEntry.COLUMN_REVIEW_ID, "_");
                reviewValue.put(PlayContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                getContext().getContentResolver().delete(PlayContract.ReviewEntry.CONTENT_URI,
                        PlayContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Integer.toString(movieId)});
                getContext().getContentResolver().insert(PlayContract.ReviewEntry.CONTENT_URI, reviewValue);
            }
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void getTrailerFormJson(String videoJsonStr, int movieId)
            throws JSONException {
        final String MP_RESULTS = "results";
        final String MP_VIDEO_ID = "id";
        final String MP_ADDRESS = "key";
        final String MP_NAME = "name";
        try {
            JSONObject videoJSON = new JSONObject(videoJsonStr);
            JSONArray videoArray = videoJSON.getJSONArray(MP_RESULTS);
            if(videoArray.length() > 0){
                for(int i = 0; i < 1; i++){
                    String address;
                    String name;
                    String videoId;
                    JSONObject fullVideo= videoArray.getJSONObject(i);
                    address = fullVideo.getString(MP_ADDRESS);
                    name = fullVideo.getString(MP_NAME);
                    videoId = fullVideo.getString(MP_VIDEO_ID);
                    ContentValues videoValue = new ContentValues();
                    videoValue.put(PlayContract.TrailerEntry.COLUMN_ADDRESS, address);
                    videoValue.put(PlayContract.TrailerEntry.COLUMN_MOVIE_NAME, name);
                    videoValue.put(PlayContract.TrailerEntry.COLUMN_VIDEO_ID, videoId);
                    videoValue.put(PlayContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                    getContext().getContentResolver().delete(PlayContract.TrailerEntry.CONTENT_URI,
                            PlayContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movieId)});
                    getContext().getContentResolver().insert(PlayContract.TrailerEntry.CONTENT_URI, videoValue);
                }
            }else{
                ContentValues videoValue = new ContentValues();
                videoValue.put(PlayContract.TrailerEntry.COLUMN_ADDRESS, "_");
                videoValue.put(PlayContract.TrailerEntry.COLUMN_MOVIE_NAME, "_");
                videoValue.put(PlayContract.TrailerEntry.COLUMN_VIDEO_ID, "_");
                videoValue.put(PlayContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                getContext().getContentResolver().delete(PlayContract.TrailerEntry.CONTENT_URI,
                        PlayContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Integer.toString(movieId)});
                getContext().getContentResolver().insert(PlayContract.TrailerEntry.CONTENT_URI, videoValue);
            }
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private boolean movieExists(ArrayList<Integer> moviesID, int movieID) {
        boolean found = false;
        for (int i = 0; i < moviesID.size(); i++) {
            if (moviesID.get(i) == movieID) {
                found = true;
                Log.d("Photon","movie found = "+ found);
            }

        }
        return found;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private static void onAccountCreated(Account newAccount, Context context) {
        PlaySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
