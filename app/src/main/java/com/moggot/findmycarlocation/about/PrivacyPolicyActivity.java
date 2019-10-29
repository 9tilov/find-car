package com.moggot.findmycarlocation.about;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.moggot.findmycarlocation.R;
import com.moggot.findmycarlocation.base.BaseActivity;

public class PrivacyPolicyActivity extends BaseActivity {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        webView = findViewById(R.id.privacy_policy_webview);

        String url = getString(R.string.privacy_policy_link);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public int getFragmentContainerId() {
        return 0;
    }
}
