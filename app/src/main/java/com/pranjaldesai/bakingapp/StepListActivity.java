package com.pranjaldesai.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pranjaldesai.bakingapp.ApiData.BakingResult;
import com.pranjaldesai.bakingapp.ApiData.Ingredients;
import com.pranjaldesai.bakingapp.ApiData.Steps;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    BakingResult bakingResult;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ingredientsList) TextView ingredientsView;
    @BindView(R.id.step_list) View recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (findViewById(R.id.step_detail_container) != null) {
            mTwoPane = true;
        }
        bakingResult= (BakingResult) getIntent().getExtras().get(getResources().getString(R.string.recipe));

        assert recyclerView != null;
        if(bakingResult!=null) {
            toolbar.setTitle(bakingResult.getName());
            actionBar.setTitle(bakingResult.getName());
            setupIngredientsView();
            setupRecyclerView((RecyclerView) recyclerView);
        }

    }

    public void setupIngredientsView(){
        ArrayList<Ingredients> ingredients;
        ingredients= bakingResult.getIngredients();
        String ingredientList="";
        for(Ingredients ingredient: ingredients){
            String ingredientBuilder= "- "+ ingredient.getQuantity() + " "+ ingredient.getMeasure() + " "+ ingredient.getIngredient()+"\n";
            ingredientList= ingredientList.concat(ingredientBuilder);
        }
        ingredientsView.setText(ingredientList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        ArrayList<Steps> steps;
        List<Integer> stepNumber= new ArrayList<>();
        steps=bakingResult.getSteps();
        ArrayList<String> stepName= new ArrayList<>();
        for(Steps step: steps){
            stepName.add(step.getShortDescription());
            stepNumber.add(step.getId());
        }

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, stepName, stepNumber, mTwoPane, bakingResult));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final StepListActivity mParentActivity;
        private final ArrayList<String> mValues;
        private final List<Integer> mIds;
        private final boolean mTwoPane;
        private final BakingResult mBakingResult;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Steps step= mBakingResult.getSteps().get(Integer.parseInt(view.getTag().toString()));
                if (mTwoPane) {
                    Context context = view.getContext();
                    Bundle arguments = new Bundle();
                    arguments.putString(context.getResources().getString(R.string.steps), new Gson().toJson(step));
                    arguments.putInt(context.getResources().getString(R.string.steps_number), Integer.parseInt(view.getTag().toString()));
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, StepDetailActivity.class);
                    String stepString= new Gson().toJson(step);
                    intent.putExtra(context.getResources().getString(R.string.steps), stepString);
                    intent.putExtra(context.getResources().getString(R.string.steps_number), Integer.parseInt(view.getTag().toString()));
                    intent.putExtra(context.getResources().getString(R.string.recipe), mBakingResult);
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(StepListActivity parent,
                                      ArrayList<String> items,
                                      List<Integer> ids,
                                      boolean twoPane, BakingResult bakingResult) {
            mValues = items;
            mIds = ids;
            mParentActivity = parent;
            mTwoPane = twoPane;
            mBakingResult= bakingResult;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position));

            holder.itemView.setTag(mIds.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
