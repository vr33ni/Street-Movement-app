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


/**
 * Class defining the properties of a custom info window that is displayed after clicking on a marker on google maps
 * => implementation of the InfoWindowAdapter
 */
public class GoogleMapsCustomInfoWindow implements GoogleMap.InfoWindowAdapter{

    private static final String LOG_TAG = "CustomInfoWindow";
    private Activity context;

    public GoogleMapsCustomInfoWindow(Activity context){
        this.context = context;
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
        tvTitle.setWidth(ivPark.getDrawable().getIntrinsicWidth());

        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        tvSubTitle.setWidth(ivPark.getDrawable().getIntrinsicWidth());


        String str=marker.getTitle();
        final String[] str2=str.split("_");

        ivPark.setImageResource(R.drawable.img_railheaven);
        tvTitle.setText(str2[0]);
        tvSubTitle.setText(str2[1]);
        tvSubTitle.setMaxLines(3);

        loadImageWithGlide(marker, ivPark);

        return view;
    }


    public void loadImageWithGlide(final Marker marker, ImageView iv) {

        Glide.with(context)
                .load(marker.getSnippet())
                .override(700, 400)
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
    }

}