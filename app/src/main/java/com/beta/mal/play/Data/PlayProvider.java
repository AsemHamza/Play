package com.beta.mal.play.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.SQLException;
import android.net.Uri;

public class PlayProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PlayDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;
    static final int TRAILER = 200;
    static final int REVIEW =300;


    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(
                PlayContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        PlayContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        PlayContract.ReviewEntry.TABLE_NAME +
                        //
                        " ON " + PlayContract.MovieEntry.TABLE_NAME +
                        "." + PlayContract.MovieEntry.COLUMN_ID +
                        " = " + PlayContract.TrailerEntry.TABLE_NAME +
                        "." + PlayContract.TrailerEntry.COLUMN_MOVIE_ID + " AND "
                        //
                        + PlayContract.MovieEntry.TABLE_NAME +
                        "." + PlayContract.MovieEntry.COLUMN_ID +
                        " = " + PlayContract.ReviewEntry.TABLE_NAME +
                        "." + PlayContract.ReviewEntry.COLUMN_MOVIE_ID + " AND "
                        //
                        + PlayContract.ReviewEntry.TABLE_NAME +
                        "." + PlayContract.ReviewEntry.COLUMN_MOVIE_ID +
                        " = " + PlayContract.TrailerEntry.TABLE_NAME +
                        "." + PlayContract.TrailerEntry.COLUMN_MOVIE_ID);
    }

    private static final String sIDSelection =
            PlayContract.MovieEntry.TABLE_NAME +
                    "." + PlayContract.MovieEntry.COLUMN_ID + " = ? ";


    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {

        int  movieId = PlayContract.MovieEntry.getMovieIdFromUri(uri);
        String selection = sIDSelection;
        String[] selectionArgs = new String[]{Integer.toString(movieId)};


        return sMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PlayContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PlayContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, PlayContract.PATH_MOVIES + "/#", MOVIE_ID);
        matcher.addURI(authority, PlayContract.PATH_TRAILERS , TRAILER);
        matcher.addURI(authority, PlayContract.PATH_REVIEWS , REVIEW);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PlayDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {


            case MOVIE:
                return PlayContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return PlayContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return PlayContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return PlayContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE_ID: {
                returnCursor = getMovieById(uri, projection, sortOrder);
                break;
            }
            case MOVIE: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        PlayContract.MovieEntry.TABLE_NAME, projection,selection,
                        selectionArgs,null,null,sortOrder);
                break;
            }
            case TRAILER: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        PlayContract.TrailerEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            }
            case REVIEW: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        PlayContract.ReviewEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(PlayContract.MovieEntry.TABLE_NAME,null, values);
                if ( _id > 0 )
                    returnUri = PlayContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;

            }

            case REVIEW:{
                long _id = db.insert(PlayContract.ReviewEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = PlayContract.ReviewEntry.buildReviewURL(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }

            case TRAILER: {
                long _id = db.insert(PlayContract.TrailerEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = PlayContract.ReviewEntry.buildReviewURL(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match){
            case MOVIE:
                rowsDeleted = db.delete(PlayContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEW:
                rowsDeleted = db.delete(PlayContract.ReviewEntry.TABLE_NAME,selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(PlayContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case MOVIE: {
                rowsUpdated = db.update(PlayContract.MovieEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(PlayContract.ReviewEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            case TRAILER: {
                rowsUpdated = db.update(PlayContract.TrailerEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(PlayContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                int returnCountR = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(PlayContract.ReviewEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCountR++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountR;
            case TRAILER:
                db.beginTransaction();
                int returnCountV = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(PlayContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountV++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountV;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
