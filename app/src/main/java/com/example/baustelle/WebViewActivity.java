package com.example.baustelle;

import androidx.appcompat.app.AppCompatActivity;

import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView=findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://192.168.1.191:8081/AutoVL");
//        webView.loadUrl("www.google.com");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
                handler.proceed();
            }
        });

    }
}