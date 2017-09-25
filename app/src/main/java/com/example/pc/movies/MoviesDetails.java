package com.example.pc.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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