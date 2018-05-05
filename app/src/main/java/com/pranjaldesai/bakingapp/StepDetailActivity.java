package com.pranjaldesai.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;

import com.google.gson.Gson;
import com.pranjaldesai.bakingapp.ApiData.BakingResult;
import com.pranjaldesai.bakingapp.ApiData.Steps;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Step detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StepListActivity}.
 */
public class StepDetailActivity extends AppCompatActivity {

    BakingResult bakingResult;
    int currentIndex, totalSteps;
    @BindView(R.id.nextButton) Button nextButton;
    @BindView(R.id.previousButton) Button previousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        bakingResult= (BakingResult) getIntent().getExtras().get(getResources().getString(R.string.recipe));
        currentIndex= (int) getIntent().getExtras().get(getResources().getString(R.string.steps_number));
        totalSteps= bakingResult.getSteps().size();

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(getResources().getString(R.string.steps),
                    new Gson().toJson(bakingResult.getSteps().get(currentIndex)));
            arguments.putInt(getResources().getString(R.string.steps_number), currentIndex);
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_detail_container, fragment)
                    .commit();
        }

        displayButtons();
    }

    public void nextClick(View view){
        Bundle arguments = new Bundle();
        currentIndex++;
        displayButtons();
        arguments.putString(getResources().getString(R.string.steps), new Gson().toJson(bakingResult.getSteps().get(currentIndex)));
        arguments.putInt(getResources().getString(R.string.steps_number), currentIndex);
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, fragment)
                .commit();
    }

    public void displayButtons(){
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        if(currentIndex <= 0){
            previousButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        } else if (currentIndex >= (totalSteps-1)){
            previousButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        }

    }

    public void previousClick(View view){
        Bundle arguments = new Bundle();
        currentIndex--;
        displayButtons();
        arguments.putString(getResources().getString(R.string.steps), new Gson().toJson(bakingResult.getSteps().get(currentIndex)));
        arguments.putInt(getResources().getString(R.string.steps_number), currentIndex);
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent= new Intent(this, StepListActivity.class);
            intent.putExtra(getResources().getString(R.string.recipe), bakingResult);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
