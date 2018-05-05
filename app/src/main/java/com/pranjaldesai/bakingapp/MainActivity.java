package com.pranjaldesai.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pranjaldesai.bakingapp.ApiData.Baking;
import com.pranjaldesai.bakingapp.ApiData.BakingResult;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.GridItemClickListener {

    private ArrayList<BakingResult> bakingResults;
    private RecipeListAdapter movieAdaptor;
    @BindView(R.id.recipe_list) RecyclerView mRecylerView;
    private GridLayoutManager gridLayoutManager;
    SharedPreferences mPreferences;
    ArrayList<String> imageURL, title, id, serving;

    @Nullable
    private SimpleIdlingResource mIdlingResource;


    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Gson gson= new Gson();
        Intent intent= getIntent();
        mPreferences = getSharedPreferences(getString(R.string.apiDataPreferences), Context.MODE_PRIVATE);

        String receiver;

        if(intent.hasExtra(getResources().getString(R.string.baking))){
            receiver= intent.getStringExtra(getResources().getString(R.string.baking));
        } else {
            receiver= mPreferences.getString(getString(R.string.apiData), "");
        }

        if(!receiver.isEmpty()) {
            bakingResults = new ArrayList<>(Arrays.asList(gson.fromJson(receiver, BakingResult[].class)));
        }

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int column;
        if(isTablet){
            column=3;
        }else{
            column=1;
        }

        if(bakingResults!=null){
            gridLayoutManager= new GridLayoutManager(this,column);
            mRecylerView.setLayoutManager(gridLayoutManager);
            mRecylerView.setHasFixedSize(true);
            imageURL= new ArrayList<>();
            title= new ArrayList<>();
            serving =new ArrayList<>();
            id= new ArrayList<>();
            for (BakingResult movieResult : bakingResults) {
                imageURL.add(movieResult.getImage());
                title.add(movieResult.getName());
                id.add(Integer.toString(movieResult.getId()));
                serving.add("Serving Size: "+Integer.toString(movieResult.getServings()));
            }
            movieAdaptor = new RecipeListAdapter(this, title, id,imageURL, serving);
            mRecylerView.setAdapter(movieAdaptor);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        IngredientWidgetService.startActionUpdateIngredientWidgets(this, bakingResults.get(clickedItemIndex));
        Intent intent = new Intent(this, StepListActivity.class);
        intent.putExtra(getResources().getString(R.string.recipe), bakingResults.get(clickedItemIndex));
        startActivity(intent);

    }
}
