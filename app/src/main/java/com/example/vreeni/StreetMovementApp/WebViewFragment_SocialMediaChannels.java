package com.example.vreeni.StreetMovementApp;

/**
 * Created by vreeni on 19/12/2017.
 */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//this class has the purpose to enable an embedded view of one of Street Movement's Social Media Channels
public class WebViewFragment_SocialMediaChannels extends android.support.v4.app.Fragment {
    public WebView webView;
    String socMedChannelUrl;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        socMedChannelUrl = this.getArguments().getString("url");
        webView = (WebView) view.findViewById(R.id.webView);
        callWebClient(socMedChannelUrl);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.webviewfragment_social, container, false);
    }

    private void callWebClient(String url) {
        webView.setWebViewClient(new myWebViewClient());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
    }

    public class myWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stubsuper.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            Log.e("urhhllll", url);
            view.loadUrl(url);
            return true;

        }
    }
}