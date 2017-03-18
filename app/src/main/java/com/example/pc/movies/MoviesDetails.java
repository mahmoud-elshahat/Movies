package com.example.pc.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.key;
import static android.R.attr.labelTextSize;
import static android.R.attr.start;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.view.View.Y;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.pc.movies.R.id.author;
import static com.example.pc.movies.R.id.like;
import static com.example.pc.movies.R.id.videosList;

public class MoviesDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_details);
        Bundle bundle=getIntent().getExtras();
        Movie m=bundle.getParcelable("movie");
        getSupportActionBar().setTitle(m.original_title);
        if (savedInstanceState == null) {

            DetailsFragment detailsFragment=new DetailsFragment();

            detailsFragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .add(R.id.detailsFragment, detailsFragment)
                    .commit();
        }
}}
//snackBar