package mypopularmovieapp.thiagoxtapps.com.mypopularmovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mypopularmovieapp.thiagoxtapps.com.mypopularmovieapp.api.GlideApp;
import mypopularmovieapp.thiagoxtapps.com.mypopularmovieapp.model.Movie;

public class DetailActivity extends AppCompatActivity {
    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView, favoriteImageView;
    private String uId;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        //----------------------------auth--------------------------------------------------------
        firebaseAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                //firebaseAuth.signOut();
                if(firebaseAuth.getCurrentUser() != null){
                    uId = firebaseAuth.getCurrentUser().getUid().toString();
                }
            }
        };
        //----------------------------auth--------------------------------------------------------

        imageView = (ImageView) findViewById(R.id.thumbnail_image_header);
        favoriteImageView = (ImageView) findViewById(R.id.favorite);
        nameOfMovie = (TextView) findViewById(R.id.movietitle);
        plotSynopsis = (TextView) findViewById(R.id.plotsynopsis);
        userRating = (TextView) findViewById(R.id.userrating);
        releaseDate = (TextView) findViewById(R.id.releasedate);

        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovieToFavorites();
            }
        });

        Intent intentThatStartThisActivity = getIntent();
        if (intentThatStartThisActivity.hasExtra("original_title")){
            String thumbnail = getIntent().getExtras().getString("poster_path");
            thumbnail = getString(R.string.base_img_url) + thumbnail;

            String movieName = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");
            String rating = getIntent().getExtras().getString("vote_average");
            String dateOfRelease = getIntent().getExtras().getString("release_date");

            GlideApp.with(imageView.getContext())
                    .load(thumbnail)
                    .placeholder(R.drawable.load)
                    .into(imageView);

            GlideApp.with(favoriteImageView.getContext())
                    .load(R.drawable.star)
                    .into(favoriteImageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);
        }else{
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    private void initCollapsingToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if(scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow = true;
                }else if(isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void addMovieToFavorites(){

        Intent intentThatStartThisActivity = getIntent();

        if (intentThatStartThisActivity.hasExtra("original_title")){

            String thumbnail = getIntent().getExtras().getString("poster_path");
            thumbnail = getString(R.string.base_img_url) + thumbnail;
            String movieName = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");
            Double rating = getIntent().getExtras().getDouble("vote_average");
            String dateOfRelease = getIntent().getExtras().getString("release_date");
            Integer movieId = getIntent().getExtras().getInt("id");
            Integer voteCount = getIntent().getExtras().getInt("vote_count");

            Movie movie = new Movie(thumbnail, false, synopsis, dateOfRelease, null, movieId,
                                    movieName, null, null, null, null,
                                    voteCount, false, rating, true);

            try{

                final DatabaseReference databaseMovie;
                databaseMovie = FirebaseDatabase.getInstance().getReference("favorites");

                String id = databaseMovie.push().getKey();
                databaseMovie
                        .child(uId)
                        .child(id)
                        .setValue(movie);

                Toast.makeText(this, "Movie added to favorite list.", Toast.LENGTH_SHORT).show();

            }catch (IllegalArgumentException e){
                int a = 1;
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }

    }



}
