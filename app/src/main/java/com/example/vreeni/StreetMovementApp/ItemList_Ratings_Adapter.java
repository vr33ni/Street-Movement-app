package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * sets the rating based on the recyclerview_item_rating layout, will then be included in the fragment, where the ratings are displayed
 */
public class ItemList_Ratings_Adapter extends RecyclerView.Adapter<com.example.vreeni.StreetMovementApp.ItemList_Ratings_Adapter.ViewHolder> {

    private final ArrayList<HashMap<String, Object>> ratingList;
    private LayoutInflater mInflater;
    private Context context;

    public ItemList_Ratings_Adapter(Context context, ArrayList<HashMap<String, Object>> ratingList) {
        mInflater = LayoutInflater.from(context);
        this.ratingList = ratingList;
        this.context = context;
    }


    @Override
    public com.example.vreeni.StreetMovementApp.ItemList_Ratings_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_rating, parent, false);
        com.example.vreeni.StreetMovementApp.ItemList_Ratings_Adapter.ViewHolder holder = new com.example.vreeni.StreetMovementApp.ItemList_Ratings_Adapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemList_Ratings_Adapter.ViewHolder holder, int position) {
        HashMap<String, Object> ratingMapOfKeyValuePairs = ratingList.get(position);
        String username = (String) ratingMapOfKeyValuePairs.get("username");
        String comment = (String) ratingMapOfKeyValuePairs.get("comment");
        long rating = (Long) ratingMapOfKeyValuePairs.get("rating");
        // String mCurrent = ratingList.get(position);
//        int mPos = ratingList.indexOf(ratingList.get(position));
        holder.usernameItemView.setText(username);
        holder.commentItemView.setText(comment);
        if (rating > 0 && rating <= 5) {
            float f = (float) rating;
            holder.ratingItemView.setRating(f);
        } else holder.ratingItemView.setRating(0);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameItemView;
        private TextView commentItemView;
        private RatingBar ratingItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameItemView = itemView.findViewById(R.id.tv_username);
            commentItemView = itemView.findViewById(R.id.tv_rating_comment);
            ratingItemView = itemView.findViewById(R.id.ratingBar_stars);
        }
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }
}
