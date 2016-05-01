package com.beta.mal.play.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlayDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";

    public PlayDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + PlayContract.MovieEntry.TABLE_NAME + " (" +

                PlayContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the movie entry associated with this movie data
                PlayContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL," +
                PlayContract.MovieEntry.COLUMN_ID + " INTEGER NOT NULL," +
                PlayContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                PlayContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                PlayContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                PlayContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                PlayContract.MovieEntry.COLUMN_VIDEO + " TEXT NOT NULL," +
                PlayContract.MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL," +
                PlayContract.MovieEntry.COLUMN_FAV + " INTEGER NULL," +
                " UNIQUE (" + PlayContract.MovieEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + PlayContract.TrailerEntry.TABLE_NAME + " (" +
                PlayContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlayContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                PlayContract.TrailerEntry.COLUMN_VIDEO_ID + " TEXT  NOT NULL," +
                PlayContract.TrailerEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                PlayContract.TrailerEntry.COLUMN_ADDRESS + " TEXT NOT NULL," +
                " UNIQUE (" + PlayContract.TrailerEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        ;

        Log.d("Photon","Two table have been created");

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + PlayContract.ReviewEntry.TABLE_NAME + " (" +
                PlayContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlayContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                PlayContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL," +
                PlayContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                PlayContract.ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL," +
                " UNIQUE (" + PlayContract.ReviewEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlayContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlayContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlayContract.ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
