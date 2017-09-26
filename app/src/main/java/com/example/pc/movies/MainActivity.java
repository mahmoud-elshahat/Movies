package com.example.pc.movies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements MovieListener {
    Boolean Tablet;
    MoviesFragment moviesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.fragmenttwo);
        if (frameLayout2 == null) {
            Tablet = false;
        } else {
            Tablet = true;
        }

        try {
            MoviesFragment fragment =
                    (MoviesFragment) getFragmentManager()
                            .getFragment(savedInstanceState, "fragment");
            if (fragment == null) {
                moviesFragment = new MoviesFragment();
            } else {
                moviesFragment = fragment;
            }

        }catch(NullPointerException n)
        {
            moviesFragment=new MoviesFragment();
        }
        finally {
            moviesFragment.setMovieListener(this);
            getFragmentManager().beginTransaction().add(R.id.fragmentone, moviesFragment).commit();
        }

    }

    @Override
    public void setMovie(Movie movie) {
        if (!Tablet) {
            Intent intent = new Intent(this, MoviesDetails.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        } else {
            DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", movie);
            detailsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragmenttwo, detailsFragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        getFragmentManager()
                .putFragment(outState, "fragment", moviesFragment);
    }
}
