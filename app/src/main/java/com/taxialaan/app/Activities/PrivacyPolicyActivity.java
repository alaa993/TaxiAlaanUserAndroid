package com.taxialaan.app.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.taxialaan.app.R;
import com.taxialaan.app.Utils.LocaleManager;

public class PrivacyPolicyActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        webView = (WebView) findViewById(R.id.webView);
        findViewById(R.id.closeButton).setOnClickListener(this);

        // Get Web view
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        if (LocaleManager.getLanguage(this).equals("ar")) {
            webView.loadUrl("file:///android_asset/html/taxialaanArabic.html");

        } else if (LocaleManager.getLanguage(this).equals("en")) {
            webView.loadUrl("file:///android_asset/html/TermsandconditionsEtaxiEnglish.html");

        } else
            webView.loadUrl("file:///android_asset/html/termsKurdish.html");
      //  LocaleManager.updateResources(this, LocaleManager.getLanguage(this));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeButton:
                finish();
                break;
        }
    }
}
