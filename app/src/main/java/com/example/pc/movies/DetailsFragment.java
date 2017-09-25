package com.example.pc.movies;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by PC on 11/1/2016.
 */

public class DetailsFragment extends Fragment {

    String Base_URL = "http://api.themoviedb.org/3/movie/";
    String REST_URL = "/reviews?api_key=";
    String url = "";
    ArrayList<Review> ReviewList;
    ArrayList<String> YoutubeVideosKeys;
    RequestQueue requestQueue;
    TextView review;
    TextView author;
    String id;
    Activity myActivity;
    private final String MY_PREFS_NAME = "favourite";
    ArrayList<Trailer> trailers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity = activity;
    }

    public void onDetach() {
        super.onDetach();
        myActivity = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.details_fragment, container, false);

        final Movie movie = getArguments().getParcelable("movie");
        ReviewList = new ArrayList<>();
        NetworkImageView networkImageView = (NetworkImageView) root.findViewById(R.id.poster);
        TextView release = (TextView) root.findViewById(R.id.release);
        TextView rate = (TextView) root.findViewById(R.id.rate);
        TextView overview = (TextView) root.findViewById(R.id.overview);
        TextView title = (TextView) root.findViewById(R.id.title);
        review = (TextView) root.findViewById(R.id.content);
        author = (TextView) root.findViewById(R.id.author);
     /*   if(movie!=null)
        {
            Toast.makeText(myActivity,"Null",Toast.LENGTH_SHORT).show();
        }
        if(getArguments().getParcelable("movie")==null)
        {
            Toast.makeText(myActivity,"Null Movie",Toast.LENGTH_SHORT).show();
        }
*/

        title.setText(movie.original_title);
        release.setText(getResources().getString(R.string.release)  + movie.release_date);

        rate.setText(getResources().getString(R.string.rating)+ String.valueOf(movie.vote_average));

        overview.setText( getResources().getString(R.string.overview) + movie.overview);
        id = String.valueOf(movie.id);

        String ImageUrl = "http://image.tmdb.org/t/p/w185//" + movie.poster_path;
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(myActivity).getImageLoader();
        networkImageView.setImageUrl(ImageUrl, mImageLoader);
        LikeButton likeButton = null;
        if (myActivity != null) {
            likeButton = (LikeButton) root.findViewById(R.id.like);
        }

        String URL = "content://com.example.pc.movies.ContactProvider";
        Uri students = Uri.parse(URL);
        Cursor c = getActivity().getContentResolver().query(students, null, "id= "+movie.id,null, null);
        boolean flag=false;
        if (c.moveToFirst()) {
            do{
                flag=true;
            } while (c.moveToNext());

        }



       if (flag == true) {
            likeButton.setLiked(true);
        }


        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                ContentValues values = new ContentValues();
                values.put(ContactProvider.TITLE,
                        movie.original_title);
                values.put(ContactProvider.OVERVIEW,
                        movie.overview);
                values.put(ContactProvider.RELEASE_DATE,
                        movie.release_date);
                values.put(ContactProvider.VOTE,
                        movie.vote_average);
                values.put(ContactProvider.POSTER_PATH,
                        movie.poster_path);
                values.put(ContactProvider.ID,
                        movie.id);

                getActivity().getContentResolver().insert(
                        ContactProvider.CONTENT_URL, values);


                //   movie.save();
                Toast.makeText(myActivity, "Added To Favourite List", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {


                getActivity().getContentResolver().delete(ContactProvider.CONTENT_URL,"id= "+movie.id ,null);



                //  new Delete().from(Movie.class).where("movieId = ?", movie.id).execute();
                Toast.makeText(myActivity, "Removed From Favourite List", Toast.LENGTH_SHORT).show();

            }
        });


        url = Base_URL + movie.id + REST_URL + BuildConfig.THE_MOVIE_DB_API_TOKEN;
      //  String Reviewsitems[] = new String[ReviewList.size()];
        requestQueue = Volley.newRequestQueue(myActivity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            Gson gson = new GsonBuilder()
                                    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Review review;
                                review = gson.fromJson(jsonArray.get(i).toString(), Review.class);
                                ReviewList.add(review);
                            }
                            updateData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );


        url = Base_URL + movie.id + "/videos?api_key=" + BuildConfig.THE_MOVIE_DB_API_TOKEN;
        trailers = new ArrayList<>();
        JsonObjectRequest trialersRequset = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Trailer trailer = gson.fromJson(jsonArray.get(i).toString(), Trailer.class);
                        trailers.add(trailer);
                        updateTrailers();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
        requestQueue.add(trialersRequset);
        Log.v("LOL", String.valueOf(ReviewList.size()));

        //Trailers

        return root;
    }


    ListView listView;

    public void updateData() {
        if (myActivity != null) {
            listView = (ListView) myActivity.findViewById(R.id.listView);
        }

        String[] contents = new String[ReviewList.size()];
        String[] authors = new String[ReviewList.size()];
        if (ReviewList.size() > 0) {
            for (int i = 0; i < ReviewList.size(); i++) {
                contents[i] = ReviewList.get(i).content;
                authors[i] = ReviewList.get(i).author;
            }

            ReviewsListAdapter reviewsListAdapter = new ReviewsListAdapter(myActivity, contents, authors);
            if (listView != null) {
                listView.setAdapter(reviewsListAdapter);
                setListViewHeightBasedOnChildren(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ReviewList.get(i).url));
                        startActivity(intent);
                    }
                });
            }
        } else {
            if (myActivity != null) {
                TextView textView = (TextView) myActivity.findViewById(R.id.topText);
                textView.setText("No Reviews");
            }
        }
    }

    public void updateTrailers() {

        YoutubeVideosKeys = new ArrayList<>();

        String TrailerUrl = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + BuildConfig.THE_MOVIE_DB_API_TOKEN;

        JsonObjectRequest TrialersJson = new JsonObjectRequest(Request.Method.GET, TrailerUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        YoutubeVideosKeys.add(jsonObject.getString("key"));
                    }
                    getYoutubeImages(YoutubeVideosKeys);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(TrialersJson);


    }


    public void getYoutubeImages(final ArrayList<String> keys) {

        final ArrayList<String> ImagesPath = new ArrayList<>();
        final ArrayList<String> Titles = new ArrayList<>();
        String YoutubeKey = "AIzaSyCaCaiGDHya5VozIlM47iiSqoX_UmR0tqY";

        int Length = keys.size();

        if (Length > 5) {
            Length = 5;
        }
        for (int i = 0; i < Length; i++) {
            String YoutubeLinkApi = "https://www.googleapis.com/youtube/v3/videos?id=" + keys.get(i) + "&key=" + YoutubeKey + "&part=snippet";
            JsonObjectRequest YoutubeviedoDetail = new JsonObjectRequest(Request.Method.GET, YoutubeLinkApi, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        JSONArray jsonArray = response.getJSONArray("items");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("snippet");
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("thumbnails");
                        JSONObject jsonObject3 = jsonObject2.getJSONObject("medium");

                        ImagesPath.add(jsonObject3.getString("url"));
                        Titles.add(jsonObject1.getString("title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(myActivity, "there is something wrong", Toast.LENGTH_SHORT).show();

                    }
                    if (myActivity != null) {
                        ListView listView = (ListView) myActivity.findViewById(R.id.videosList);
                        listView.setAdapter(new TrailersAdapter(myActivity, ImagesPath, Titles));
                        setListViewHeightBasedOnChildren(listView);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + keys.get(i)));
                                startActivity(intent);
                            }
                        });
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            requestQueue.add(YoutubeviedoDetail);
        }

    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
