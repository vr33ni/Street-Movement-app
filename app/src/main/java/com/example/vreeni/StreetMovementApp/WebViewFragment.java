//package com.example.vreeni.StreetMovementApp; /**
// * Created by vreeni on 19/12/2017.
// */
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//
//public class WebViewFragment extends android.support.v4.app.Fragment {
//    public WebView webView;
//    String gurl;
//
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        gurl = this.getArguments().getString("url");
//        webView = (WebView) view.findViewById(R.id.webView);
//
//        webView.setInitialScale(1);
//        webView.setWebChromeClient(new WebChromeClient());
//
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
//        webView.setWebViewClient(new WebViewClient());
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
//        int width = displaymetrics.widthPixels;
//
//        Log.e("WebView Log", width + "-" + height);
//
//        String data_html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:white;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe style=\"background:white;\" width=' " + width + "' height='" + height / 2 + "' src=\"" + "https://player.vimeo.com/video/249822484" + "\" frameborder=\"0\"></iframe> </body> </html> ";
//
//        webView.loadDataWithBaseURL("http://vimeo.com", data_html, "text/html", "UTF-8", null);
//    }
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.webview_exercise, container, false);
//
//    }
//}