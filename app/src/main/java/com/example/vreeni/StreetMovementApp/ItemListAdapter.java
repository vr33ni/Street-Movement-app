package com.example.vreeni.StreetMovementApp;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by vreee on 13/03/2018.
 */

/**
 * sets the rating based on the recyclerview_item layout, will then be included in the fragment, where the ratings are displayed
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {
    private final ArrayList<HashMap<String, Object>> ratingList;
    private LayoutInflater mInflater;

    public ItemListAdapter(Context context, ArrayList<HashMap<String, Object>> ratingList) {
        mInflater = LayoutInflater.from(context);
        this.ratingList = ratingList;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView usernameItemView;
        public final TextView commentItemView;
        public final RatingBar ratingItemView;
        final ItemListAdapter mAdapter;

        public ItemViewHolder(View itemView, ItemListAdapter adapter) {
            super(itemView);
            usernameItemView = (TextView) itemView.findViewById(R.id.tv_username);
            commentItemView = (TextView) itemView.findViewById(R.id.tv_rating_comment);
            ratingItemView = (RatingBar) itemView.findViewById(R.id.ratingBar_stars);

            this.mAdapter = adapter;
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        HashMap<String, Object> ratingMapOfKeyValuePairs = ratingList.get(position);
        String username = (String) ratingMapOfKeyValuePairs.get("username");
        String comment = (String) ratingMapOfKeyValuePairs.get("comment");
        long rating = (Long) ratingMapOfKeyValuePairs.get("rating");
        // String mCurrent = ratingList.get(position);
//        int mPos = ratingList.indexOf(ratingList.get(position));
        holder.usernameItemView.setText(username);
        holder.commentItemView.setText(comment);
        if (rating > 0 && rating <= 5) {
            float f = (float)rating;
            holder.ratingItemView.setRating(f);
        } else holder.ratingItemView.setRating(0);
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }
}
