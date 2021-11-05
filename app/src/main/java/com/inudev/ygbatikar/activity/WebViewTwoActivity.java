package com.inudev.ygbatikar.activity;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inudev.ygbatikar.R;

public class WebViewTwoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshWebView;
    private WebView webViewTwo;
    WebSettings webSettings;

    String URL = "http://amikom.ac.id";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_two);

        swipeRefreshWebView = findViewById(R.id.swipeResfresh_WebViewTwo);
        webViewTwo = findViewById(R.id.webview_two);

        swipeRefreshWebView.setOnRefreshListener(this);

        webSettings = webViewTwo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.getUseWideViewPort();

        webViewTwo.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                swipeRefreshWebView.setRefreshing(true);
            }
        });

        webViewTwo.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                swipeRefreshWebView.setRefreshing(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshWebView.setRefreshing(false);
            }
        });

        webViewTwo.loadUrl(URL);
    }

    @Override
    public void onRefresh() {
        webViewTwo.reload();
    }

    @Override
    public void onBackPressed() {
        if (webViewTwo.canGoBack()) {
            webViewTwo.goBack();
        } else {
            finish();
        }
    }
}
