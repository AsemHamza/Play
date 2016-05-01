package com.beta.mal.play.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class PlayContract {
    public static final String CONTENT_AUTHORITY = "com.beta.mal.play";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;



        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_FAV = "favorite";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieURL(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        }
        public static Uri buildMoviesURL() {
            return CONTENT_URI.buildUpon().build();
        }
        public static int getMovieIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1)) ;
        }

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;



        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_MOVIE_NAME = "name";



        public static final Uri buildTrailerURL (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerURL(int movieId){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        }
        public static int getTrailerIdFromURL(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final class ReviewEntry implements  BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"+ PATH_REVIEWS;


        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_REVIEW = "content";

        public static final Uri buildReviewURL (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewURL(int movieId){
            return CONTENT_URI.buildUpon().appendPath((Integer.toString(movieId))).build();
        }

        public static int getReviewIdFromURL(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));

        }


    }

}
