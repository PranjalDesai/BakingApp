package com.pranjaldesai.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.pranjaldesai.bakingapp.NetworkUtils.NetworkUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int BAKING_LOADER = 22;
    private final String CUSTOMURL= "url";
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        startLoader();
    }

    private void startLoader(){
        progressBar.setVisibility(View.VISIBLE);
        URL searchURL= NetworkUtils.buildURL();
        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        queryBundle.putString(CUSTOMURL, searchURL.toString());
        Loader<String> loader= loaderManager.getLoader(BAKING_LOADER);
        if(loader== null){
            loaderManager.initLoader(BAKING_LOADER, queryBundle,this);
        }else {
            loaderManager.restartLoader(BAKING_LOADER,queryBundle,this);
        }
    }

    private void setBakingData(String bakingResult) {
        if(bakingResult!= null && !bakingResult.isEmpty()) {
            final SharedPreferences mPreferences = getSharedPreferences(getString(R.string.apiDataPreferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor= mPreferences.edit();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra(getResources().getString(R.string.baking), bakingResult);
            editor.putString(getString(R.string.apiData), bakingResult);
            editor.apply();
            progressBar.setVisibility(View.GONE);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(this, getResources().getString(R.string.sorry_toast), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }


    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mBakingInfo;

            @Override
            protected void onStartLoading(){
                if(args==null){
                    return;
                }

                if(mBakingInfo != null){
                    deliverResult(mBakingInfo);
                }else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String urlString = args.getString(CUSTOMURL);
                String bakingResult;
                try {
                    URL currURL= new URL(urlString);
                    bakingResult = NetworkUtils.getResponseFromHttpUrl(currURL);
                    return bakingResult;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            /*
             *   Is internet available.
             */
            public boolean isOnline() {
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnectedOrConnecting();
            }

            @Override
            public void deliverResult(String json) {
                mBakingInfo=json;
                super.deliverResult(mBakingInfo);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if(loader.getId()== 22){
            setBakingData(data);
        }else {
            setBakingData("");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}