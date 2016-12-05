package com.paranoidandroid.journey.recommendations.activities;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.models.ui.Tip;
import com.paranoidandroid.journey.recommendations.adapters.TipsListAdapter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.paranoidandroid.journey.support.ui.ColorUtils.getColorWithAplha;

public class RecommendationDetailActivity extends AppCompatActivity {

    public static final String EXTRA_REC = "rec";
    private int screenHeight;
    private boolean isBookmarked;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ivBackdrop) ImageView ivBackdrop;
    @BindView(R.id.rbRating) SimpleRatingBar rbRating;
    @BindView(R.id.rvTips) RecyclerView rvTips;
    @BindView(R.id.scrimView) View scrimView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.fabBookmark) FloatingActionButton fabBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_recommendation_detail);
        ButterKnife.bind(this);

        Recommendation recommendation = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_REC));
        isBookmarked = recommendation.isBookmarked();

        setupRating(recommendation.getRating());
        setupFab();
        setupTipsRecyclerView(recommendation.getTips());
        setupBackdrop(recommendation.getImageUrl(), recommendation.getId());
        setupToolbar(recommendation.getName());
    }

    // Run the animation when the activity is shown on screen

    @Override
    protected void onStart() {
        super.onStart();
        slideInRecyclerView();
    }

    private void slideInRecyclerView() {
        ObjectAnimator animY = ObjectAnimator.ofFloat(rvTips,
                View.TRANSLATION_Y, screenHeight, 24);
        animY.setDuration(550);
        animY.setInterpolator(new DecelerateInterpolator());
        animY.setStartDelay(250);
        animY.start();
    }

    // Component setup

    private void setupToolbar(String name) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        Typeface font = Typer.set(this).getFont(Font.ROBOTO_MEDIUM);
        collapsingToolbar.setExpandedTitleTypeface(font);
    }

    private void setupTipsRecyclerView(List<Tip> tips) {
        rvTips.setHasFixedSize(true);
        rvTips.setLayoutManager(new LinearLayoutManager(this));
        rvTips.setAdapter(new TipsListAdapter(this, tips));
        findScreenHeight();
        placeRecyclerViewOffScreen();
    }

    private void setupFab() {
        fabBookmark.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, isBookmarked ? R.drawable.ic_bookmark_activity_selected : R.drawable.ic_bookmark_activity_normal));
    }

    private void setupRating(double rating) {
        if (rating == -1) {
            rbRating.setVisibility(View.GONE);
        } else {
            rbRating.setRating((float) rating/2);
        }
    }

    // Get Palette color for title view scrim

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            ivBackdrop.setImageBitmap( bitmap );
            Palette.from(bitmap).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch vibrant = palette.getDarkMutedSwatch();
                    if (vibrant != null) {
                        int transparentRGBInt = getColorWithAplha(vibrant.getRgb(), 0.5f);
                        scrimView.setBackgroundColor(transparentRGBInt);
                    }
                }
            });
        }
    };

    private void setupBackdrop(String imageUrl, String id) {
        ivBackdrop.setTag(target);
        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_placeholder))
                .error(ContextCompat.getDrawable(this, R.drawable.ic_placeholder))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .into(target);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(ivBackdrop, "P"+id);
            supportPostponeEnterTransition();
            ivBackdrop.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ivBackdrop.getViewTreeObserver().removeOnPreDrawListener(this);
                            supportStartPostponedEnterTransition();
                            return true;
                        }
                    });
        }
    }

    // Find the height of the screen

    private void findScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;
    }

    // Place recyclerView off-screen

    private void placeRecyclerViewOffScreen() {
        rvTips.setTranslationY(screenHeight);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // FAB actions

    @OnClick(R.id.fabBookmark)
    public void onBookmarkClicked(View v) {
        // TODO this is a mock behavior, actually add/remove the bookmark in the server
        isBookmarked = !isBookmarked;
        setupFab();
    }

}
