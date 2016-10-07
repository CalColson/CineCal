package com.example.cal.cinecal;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.PhantomReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    MovieAdapter mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<MovieInfo>());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_movies);
        listView.setAdapter(mMovieAdapter);



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieData();
    }

    public class FetchMovieData extends AsyncTask<String, Void, MovieInfo[]> {
        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        @Override
        protected MovieInfo[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr;
            MovieInfo[] resultInfo = null;

            String key;
            //api-key variable key is initialized here (omitted in github for security)

            try {
                final String QUERY_BASE_URL;
                if (params[0].equals("popular")) {
                    QUERY_BASE_URL = "https://api.themoviedb.org/3/movie/popular?";
                }
                else QUERY_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated?";
                final String KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(QUERY_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, key)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    line = line + "\n";
                    buffer.append(line);
                }

                if (buffer.length() == 0) return null;

                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "movieJSON String: " + movieJsonStr);

                resultInfo = getMovieInfoFromJson(movieJsonStr);
            }

            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error", e);
            }
            finally {
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

            return resultInfo;
        }

        @Override
        protected void onPostExecute(MovieInfo[] movieInfos) {
            if (movieInfos != null) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movieInfos);
            }
            super.onPostExecute(movieInfos);
        }
    }

    private MovieInfo[] getMovieInfoFromJson(String movieJsonStr)
    throws JSONException{

        final String TMDB_RESULTS = "results";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_TITLE = "title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_DATE = "release_date";

        JSONObject infoJson = new JSONObject(movieJsonStr);
        JSONArray resultsArray = infoJson.getJSONArray(TMDB_RESULTS);

        MovieInfo[] resultInfo = new MovieInfo[resultsArray.length()];
        for (int i = 0; i < resultsArray.length(); i++) {
            String posterPath;
            String title;
            String overview;
            String releaseDate;

            JSONObject movieInfo = resultsArray.getJSONObject(i);
            posterPath = movieInfo.getString(TMDB_POSTER);
            title = movieInfo.getString(TMDB_TITLE);
            overview = movieInfo.getString(TMDB_OVERVIEW);
            releaseDate = movieInfo.getString(TMDB_DATE);

            resultInfo[i] = new MovieInfo(posterPath, title, overview, releaseDate);
        }

        return resultInfo;
    }

    private void updateMovieData() {
        FetchMovieData task = new FetchMovieData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String searchType = prefs.getString(getString(R.string.pref_search_key),
                getString(R.string.pref_search_default));
        task.execute(searchType);
    }
}
