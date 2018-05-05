package com.pranjaldesai.bakingapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.pranjaldesai.bakingapp.ApiData.Steps;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link StepListActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {

    private Steps mStep;
    @BindView(R.id.exo_player)
    SimpleExoPlayerView exoPlayerView;
    @BindView(R.id.step_detail_description)
    TextView description;
    @BindView(R.id.exo_player_image)
    ImageView mThumbnail;
    @BindView(R.id.exo_player_container)
    RelativeLayout container;

    private SimpleExoPlayer player;
    private long playbackPosition = 0;
    private int currentWindow;
    private boolean playWhenReady = true;
    private String videoUrl="";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(getResources().getString(R.string.steps))) {
            mStep = new Gson().fromJson(getArguments().getString(getResources().getString(R.string.steps)),Steps.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);
        if (savedInstanceState != null && savedInstanceState.containsKey(getResources().getString(R.string.current_position))) {
            playbackPosition = savedInstanceState.getLong(getResources().getString(R.string.current_position));
            playWhenReady = savedInstanceState.getBoolean(getResources().getString(R.string.play_when_ready));
        }
        ButterKnife.bind(this, rootView);
        container.setVisibility(View.VISIBLE);

        if (mStep != null) {
            description.setText(mStep.getDescription());
            videoErrorString();
        }

        return rootView;
    }

    private void videoErrorString(){
        if(!mStep.getVideoURL().isEmpty()) {
            videoUrl = mStep.getVideoURL();
            exoPlayerView.setVisibility(View.VISIBLE);
            mThumbnail.setVisibility(View.GONE);
            initializePlayer(videoUrl);
        } else if (!mStep.getThumbnailURL().isEmpty()){
            videoUrl = mStep.getThumbnailURL();
            exoPlayerView.setVisibility(View.GONE);
            mThumbnail.setVisibility(View.VISIBLE);
            try {
                Glide.with(this)
                        .load(videoUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.food_fork_drink)
                                .fitCenter())
                        .into(mThumbnail);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            container.setVisibility(View.GONE);
            exoPlayerView.setVisibility(View.GONE);
            mThumbnail.setVisibility(View.GONE);
        }
    }

    private void initializePlayer(String videoUrl) {
        if (player == null) {

            player= ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this.getActivity()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            exoPlayerView.setPlayer(player);
        }
        MediaSource mediaSource = buildMediaSource(Uri.parse(videoUrl));
        player.prepare(mediaSource, true, false);
        if (playbackPosition != 0) {
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(getResources().getString(R.string.current_position), playbackPosition);
        outState.putBoolean(getResources().getString(R.string.play_when_ready), playWhenReady);
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("baking-exoplayer"))
                .createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            videoErrorString();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            videoErrorString();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
}
