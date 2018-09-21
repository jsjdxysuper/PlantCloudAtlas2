package com.pm.newenergyapp;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Doraemon on 2014/7/15.
 */
public class Fragment_Two extends Fragment {
    private ProgressWebView wv;
    private String url;
    private int index;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_two, null);
        initWebView(contentView);

        return contentView;
    }
    @Override
    public void onStart() {
        super.onStart();
        wv.loadUrl(url);
    }
    public void initWebView(View contentView){
        wv = (ProgressWebView) contentView.findViewById(R.id.webview);

        //自动适应屏幕
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);

        //支持 Javascript
        wv.getSettings().setJavaScriptEnabled(true);

        //不使用缓存
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //得到焦点
        wv.requestFocus();



        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
//                JzActivity.loadoptionurl("file:///android_asset/nonet.html");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                String url1 =getApplication().getString(R.string.page1_url);
//                String url2 = getApplication().getString(R.string.page2_url);
//                String url3 = getApplication().getString(R.string.page3_url);
//                if (url.contains(url1)||url.contains(url2)||url.contains(url3)) {
//                    return super.shouldInterceptRequest(view, url);//正常加载
//                }else{
//
//                    flag = 1;
//                    return super.shouldInterceptRequest(view, url);//正常加载
//                }
//            }
        });
    }
    public ProgressWebView getWv() {
        return wv;
    }
    public void setWv(ProgressWebView wv) {
        this.wv = wv;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
