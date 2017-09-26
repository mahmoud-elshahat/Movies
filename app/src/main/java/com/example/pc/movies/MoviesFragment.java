package com.example.pc.movies;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MoviesFragment extends Fragment {
    private static final String STATE_MOVIES = "movies_state";
    private String SORT_TYPE = "popular";
    ImagesAdapter viewAdapter;
    RecyclerView gridView;
    private ArrayList<Movie> movies;
    RequestQueue requestQueue;
    GridLayoutManager gridLayoutManager;
    int PAGE_NUMBER = 1;
    Boolean CAN_ADD_MORE_PAGES;
    static int lastFirstVisiblePosition;
    MovieListener movieListener;
    private EndlessRecyclerViewScrollListener scrollListener;
    String BASE_URL = "https://api.themoviedb.org/3/movie/";
    String KEY = "2a6f7c194d2bc0982f2112ac8aef7f22";
    final String KEY_PARAM = "api_key";

    TextView notice;

    public void setMovieListener(MovieListener movieListener) {
        this.movieListener = movieListener;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(STATE_MOVIES, MODE_PRIVATE).edit();
        editor.putBoolean("load", CAN_ADD_MORE_PAGES);
        editor.apply();
        try {
            lastFirstVisiblePosition = ((GridLayoutManager) gridView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (movies != null) {
                outState.putParcelableArrayList(STATE_MOVIES, movies);
                outState.putInt("page", PAGE_NUMBER);
                outState.putInt("position", lastFirstVisiblePosition);
            }

        } catch (Exception e) {
            Log.e("error", " " + e.getMessage());
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        CAN_ADD_MORE_PAGES = true;
        gridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());
        requestQueue = Volley.newRequestQueue(getActivity());
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 200;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment, container, false);
        notice = (TextView) root.findViewById(R.id.notify);

        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
            PAGE_NUMBER = savedInstanceState.getInt("page");
            lastFirstVisiblePosition = savedInstanceState.getInt("position");
        }

        if (movies != null && movies.size() != 0) {
            gridView.setAdapter(new ImagesAdapter(getActivity(), movieListener));
            ((ImagesAdapter) gridView.getAdapter()).setMovies(movies);
            gridView.scrollToPosition(lastFirstVisiblePosition);
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(STATE_MOVIES, MODE_PRIVATE).edit();
        editor.putBoolean("load", CAN_ADD_MORE_PAGES);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences(STATE_MOVIES, MODE_PRIVATE);
        CAN_ADD_MORE_PAGES = prefs.getBoolean("load", true);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE & gridView != null) {
            gridLayoutManager.setSpanCount(4);
        } else {
            if (gridView != null) {
                gridLayoutManager.setSpanCount(2);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rated) {
            if (!isOnline()) {
                notice.setText(R.string.internet);
                notice.setVisibility(View.VISIBLE);
                return true;
            } else {
                notice.setVisibility(View.GONE);
                SORT_TYPE = "top_rated";
                PAGE_NUMBER = 1;
                CAN_ADD_MORE_PAGES = true;
                updateScreen();
                return true;
            }

        } else if (id == R.id.popularity) {
            if (!isOnline()) {
                notice.setText(R.string.internet);
                notice.setVisibility(View.VISIBLE);
                return false;
            } else {
                notice.setVisibility(View.GONE);
                SORT_TYPE = "popular";
                PAGE_NUMBER = 1;
                CAN_ADD_MORE_PAGES = true;
                updateScreen();
                return true;
            }

        } else if (id == R.id.favourite) {
            CAN_ADD_MORE_PAGES = false;
            movies.clear();

            String URL = "content://com.example.pc.movies.ContactProvider";
            Uri students = Uri.parse(URL);
            Cursor c = getActivity().getContentResolver().query(students, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    Movie temp = new Movie();

                    temp.original_title = c.getString(c.getColumnIndex(ContactProvider.TITLE));
                    temp.id = c.getInt(c.getColumnIndex(ContactProvider.ID));
                    temp.overview = c.getString(c.getColumnIndex(ContactProvider.OVERVIEW));
                    temp.release_date = c.getString(c.getColumnIndex(ContactProvider.RELEASE_DATE));
                    temp.vote_average = c.getDouble(c.getColumnIndex(ContactProvider.VOTE));
                    temp.poster_path = c.getString(c.getColumnIndex(ContactProvider.POSTER_PATH));

                    movies.add(temp);
                } while (c.moveToNext());


            }

            gridView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
            viewAdapter = new ImagesAdapter(getActivity(), movieListener);
            gridView.setLayoutManager(gridLayoutManager);
            viewAdapter.setMovies(movies);
            gridView.setAdapter(viewAdapter);
            if (movies.size() == 0) {
                notice.setText(R.string.empty);
                notice.setVisibility(View.VISIBLE);
            } else {
                notice.setVisibility(View.GONE);
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();

        if (movies != null && movies.size() != 0) {
            gridLayoutManager.setSpanCount(numberOfColumns());

            return;
        }
        gridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());
        movies = new ArrayList<>();


        if (!CAN_ADD_MORE_PAGES) {
            movies.clear();
            String URL = "content://com.example.pc.movies.ContactProvider";
            Uri students = Uri.parse(URL);
            Cursor c = getActivity().getContentResolver().query(students, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    Movie temp = new Movie();

                    temp.original_title = c.getString(c.getColumnIndex(ContactProvider.TITLE));
                    temp.id = c.getInt(c.getColumnIndex(ContactProvider.ID));
                    temp.overview = c.getString(c.getColumnIndex(ContactProvider.OVERVIEW));
                    temp.release_date = c.getString(c.getColumnIndex(ContactProvider.RELEASE_DATE));
                    temp.vote_average = c.getDouble(c.getColumnIndex(ContactProvider.VOTE));
                    temp.poster_path = c.getString(c.getColumnIndex(ContactProvider.POSTER_PATH));

                    movies.add(temp);
                } while (c.moveToNext());

            }
            if (movies.size() != 0) {
                gridView.setLayoutManager(gridLayoutManager);
                viewAdapter.setMovies(movies);
                gridView.setAdapter(viewAdapter);
                notice.setVisibility(View.GONE);


            } else {
                movies.clear();
                notice.setText(R.string.empty);
                notice.setVisibility(View.VISIBLE);
            }
        }
        int rotation = getActivity().getWindowManager().getDefaultDisplay()
                .getRotation();


        if (!isOnline()) {
            Toast.makeText(getActivity(), "Please check internet Connection", Toast.LENGTH_LONG).show();
        } else {
            if (movies.size() == 0 && CAN_ADD_MORE_PAGES) {
                updateScreen();
            } else {

                gridView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
                gridView.setLayoutManager(gridLayoutManager);

                viewAdapter = new ImagesAdapter(getActivity(), movieListener);
                viewAdapter.setMovies(movies);
                gridView.setAdapter(viewAdapter);
                scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        PAGE_NUMBER++;
                        addPage();
                    }
                };
                // Adds the scroll listener to RecyclerView
                gridView.addOnScrollListener(scrollListener);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateScreen() {
        PAGE_NUMBER = 1;
        movies = new ArrayList<>();

        String url = BASE_URL + SORT_TYPE + "?" + KEY_PARAM + "=" + KEY + "&page=" + PAGE_NUMBER;

        movies.clear();

        if (CAN_ADD_MORE_PAGES) {
            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading...", true);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray moveisArray = response.getJSONArray("results");
                        Gson gson = new GsonBuilder()
                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create();
                        for (int i = 0; i < moveisArray.length(); i++) {
                            Movie movie;
                            movie = gson.fromJson(moveisArray.get(i).toString(), Movie.class);
                            movies.add(movie);
                        }

                        gridView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
                        gridView.setLayoutManager(gridLayoutManager);

                        viewAdapter = new ImagesAdapter(getActivity(), movieListener);
                        viewAdapter.setMovies(movies);
                        gridView.setAdapter(viewAdapter);

                        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                                PAGE_NUMBER++;
                                addPage();
                            }
                        };
                        // Adds the scroll listener to RecyclerView
                        gridView.addOnScrollListener(scrollListener);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();

                    viewAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("error", "Cant get json");
                }
            }
            );
            if (CAN_ADD_MORE_PAGES) {
                requestQueue.add(jsonObjectRequest);
            }
        }
    }

    public void addPage() {
        String url = BASE_URL + SORT_TYPE + "?" + KEY_PARAM + "=" + KEY + "&page=" + PAGE_NUMBER;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray moveisArray = response.getJSONArray("results");
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create();
                    for (int i = 0; i < moveisArray.length(); i++) {
                        Movie movie;
                        movie = gson.fromJson(moveisArray.get(i).toString(), Movie.class);
                        movies.add(movie);
                        Log.v("error", "" + movie.original_title);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                viewAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("error", "Cant get json");
            }
        }
        );
        if (CAN_ADD_MORE_PAGES) {
            requestQueue.add(jsonObjectRequest);
        }
    }


}