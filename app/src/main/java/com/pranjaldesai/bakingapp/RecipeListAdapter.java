package com.pranjaldesai.bakingapp;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    final private GridItemClickListener mOnClickListener;
    private ArrayList<String> recipeTitle, recipeImage, id, servingSize;
    private String tag;

    public RecipeListAdapter(GridItemClickListener mOnClickListener, ArrayList<String> recipeTitle, ArrayList<String> id, ArrayList<String> recipeImage, ArrayList<String> servingSize) {
        this.mOnClickListener = mOnClickListener;
        this.recipeTitle = recipeTitle;
        this.recipeImage= recipeImage;
        this.servingSize= servingSize;
        this.id= id;
    }


    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutID = R.layout.recipe_list_item;
        LayoutInflater inflater= LayoutInflater.from(context);

        View view= inflater.inflate(layoutID, parent, false);

        return new RecipeViewHolder(view, context);
    }

    public void setCurrentTag(String tag){
        this.tag= tag;
    }

    public String getTag(){
        return tag;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.itemView.setTag(id.get(position));
        holder.bind(recipeTitle.get(position), recipeImage.get(position), servingSize.get(position));
    }
    /*
     *   removes all the current data
     */
    public void removeData(){
        recipeTitle=null;
        recipeImage=null;
        id=null;
        servingSize=null;
    }

    /*
     *   updates the recyclerView
     */
    public void updateList(ArrayList<String> title, ArrayList<String> recipeId,  ArrayList<String> image, ArrayList<String> serving){
        recipeTitle= new ArrayList<>();
        id= new ArrayList<>();
        servingSize= new ArrayList<>();
        recipeImage= new ArrayList<>();
        recipeTitle.addAll(title);
        recipeImage.addAll(image);
        servingSize.addAll(serving);
        id.addAll(recipeId);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return recipeTitle.size();
    }

    public interface GridItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    class RecipeViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_image) ImageView recipeImageView;
        @BindView(R.id.recipe_name) TextView titleView;
        @BindView(R.id.recipe_serving) TextView servingView;
        Context mContext;

        public RecipeViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext=context;
            itemView.setOnClickListener(this);
        }

        /*
         *   binds the title and image to the recyclerview
         */
        void bind(String title, String posterUrl, String serving){
            titleView.setText(title);
            servingView.setText(serving);
            try {
                Picasso.with(mContext)
                        .load(posterUrl)
                        .placeholder(R.drawable.food_fork_drink)
                        .into(recipeImageView);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            setCurrentTag((String)v.getTag());
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
