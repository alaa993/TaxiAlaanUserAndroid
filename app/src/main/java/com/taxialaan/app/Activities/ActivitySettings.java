package com.taxialaan.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.taxialaan.app.Helper.CustomDialog;
import com.taxialaan.app.Helper.LocaleUtils;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.LocaleManager;

/**
 * Created by Esack N on 9/27/2017.
 */

public class ActivitySettings extends AppCompatActivity {

    private RadioButton radioEnglish, radioArabic, radioKurdish;

    private LinearLayout lnrEnglish, lnrArabic, lnrKurdish;

    private CustomDialog customDialogNew;

    private ImageView backArrow;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {

        radioEnglish = (RadioButton) findViewById(R.id.radioEnglish);
        radioArabic = (RadioButton) findViewById(R.id.radioArabic);
        radioKurdish = (RadioButton) findViewById(R.id.radioKurdish);

        lnrEnglish = (LinearLayout) findViewById(R.id.lnrEnglish);
        lnrArabic = (LinearLayout) findViewById(R.id.lnrArabic);
        lnrKurdish = (LinearLayout) findViewById(R.id.lnrKurdish);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (SharedHelper.getKey(ActivitySettings.this, "lang").equalsIgnoreCase("en")){
            radioEnglish.setChecked(true);
        }else if (SharedHelper.getKey(ActivitySettings.this, "lang").equalsIgnoreCase("ar")){
            radioArabic.setChecked(true);
        }else if (SharedHelper.getKey(ActivitySettings.this, "lang").equalsIgnoreCase("ku")){
            radioKurdish.setChecked(true);
        }else{
            radioEnglish.setChecked(true);
        }

        lnrEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioArabic.setChecked(false);
                radioKurdish.setChecked(false);
                radioEnglish.setChecked(true);
            }
        });

        lnrKurdish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioArabic.setChecked(false);
                radioKurdish.setChecked(true);
            }
        });

        lnrArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioKurdish.setChecked(false);
                radioArabic.setChecked(true);
            }
        });

        radioKurdish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioEnglish.setChecked(false);
                    radioArabic.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "lang", "ku");
                    setLanguage();
//                    recreate();
                    GoToMainActivity();
                }
            }
        });

        radioArabic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioEnglish.setChecked(false);
                    radioKurdish.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "lang", "ar");
                    setLanguage();
//                    recreate();
                    GoToMainActivity();
                }
            }
        });

        radioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    radioArabic.setChecked(false);
                    radioKurdish.setChecked(false);
                    SharedHelper.putKey(ActivitySettings.this, "lang", "en");
                    setLanguage();
//                    recreate();
                    GoToMainActivity();
                }
            }
        });

        MobileAds.initialize(this,
                "ca-app-pub-6606021354718512~3331887888");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void GoToMainActivity(){
        customDialogNew = new CustomDialog(ActivitySettings.this, getResources().getString(R.string.language_update));
        if (customDialogNew != null)
            customDialogNew.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customDialogNew.dismiss();
                Intent mainIntent = new Intent(ActivitySettings.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        }, 3000);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(ActivitySettings.this, "lang");
        LocaleManager.setLocale(ActivitySettings.this, languageCode);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
