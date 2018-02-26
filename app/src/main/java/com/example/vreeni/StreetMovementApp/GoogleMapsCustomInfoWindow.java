package com.example.vreeni.StreetMovementApp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsCustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private static final String LOG_TAG = "CustomInfoWindow";

    private Activity context;
    private String photoUrl;

    public GoogleMapsCustomInfoWindow(Activity context, String photoUrl){
        this.context = context;
        this.photoUrl = photoUrl;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.google_maps_custom_info_window, null);

        ImageView ivPark = (ImageView) view.findViewById(R.id.iv_park);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);

        String str=marker.getTitle();
        final String[] str2=str.split("_");

        ivPark.setImageResource(R.drawable.img_railheaven);
        tvTitle.setText(str2[0]);
        tvSubTitle.setText(str2[1]);

        Log.d(LOG_TAG, "photo url: " + photoUrl);

        loadImageWithGlide(marker, ivPark);

        return view;
    }


    public void loadImageWithGlide(final Marker marker, ImageView iv) {

        Glide.with(context)
                .load(marker.getSnippet())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (marker.isInfoWindowShown()) {
                            marker.hideInfoWindow();
                            marker.showInfoWindow();
                        }
                        return false;
                    }
                })
                .into(iv);

//        Glide.with(this.context)
//                .load(photoUrl)
//                .asBitmap()
//                .fitCenter()
//                .override(700,450)
//                .dontAnimate()
//                .listener(new RequestListener<String, Bitmap>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                        e.printStackTrace();
//                        return false;
//                    }
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        if(!isFromMemoryCache) marker.showInfoWindow();
//                        return false;
//                    }
//                }).into(iv);
    }
}