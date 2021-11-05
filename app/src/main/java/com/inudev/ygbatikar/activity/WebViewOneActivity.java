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

public class WebViewOneActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshWebView;
    private WebView webViewOne;
    WebSettings webSettings;

    String URL = "https://ygbatikar.com/privacypolicy";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_one);

        swipeRefreshWebView = findViewById(R.id.swipeResfresh_WebView);
        webViewOne = findViewById(R.id.webview_one);

        swipeRefreshWebView.setOnRefreshListener(this);

        webSettings = webViewOne.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.getUseWideViewPort();

        webViewOne.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                swipeRefreshWebView.setRefreshing(true);
            }
        });

        webViewOne.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                swipeRefreshWebView.setRefreshing(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshWebView.setRefreshing(false);
            }
        });

        webViewOne.loadUrl(URL);
    }

    @Override
    public void onRefresh() {
        webViewOne.reload();
    }

    @Override
    public void onBackPressed() {
        if (webViewOne.canGoBack()) {
            webViewOne.goBack();
        } else {
            finish();
        }
    }
}
