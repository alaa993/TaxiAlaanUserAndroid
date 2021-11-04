package com.taxialaan.app.Fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.taxialaan.app.R;

class CustomMarker extends RelativeLayout {

    public ImageButton view_main_location_selector_ib;
    public ImageView view_main_center_shadow_iv;
    public View er;
    int dr;

    public CustomMarker(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomMarker);

        try {

            dr=   a.getResourceId(R.styleable.CustomMarker_image,0);

        } finally {
            a.recycle();
        }



        inflate(context,R.layout.marker, this);

        view_main_location_selector_ib = findViewById(R.id.view_main_location_selector_ib);
        er = findViewById(R.id.er);
        view_main_center_shadow_iv = findViewById(R.id.view_main_center_shadow_iv);

        view_main_location_selector_ib.setImageResource(dr);



    }

    public void open(){
        view_main_center_shadow_iv.clearAnimation();
        view_main_center_shadow_iv.setAlpha(0.6f);
        view_main_center_shadow_iv.animate().scaleX(2f)
                .scaleY(2f)
                .setDuration(100)
                .start();

        er.setVisibility(View.VISIBLE);

    }
    public void close(){
        er.setVisibility(View.GONE);
        view_main_center_shadow_iv.clearAnimation();
        view_main_center_shadow_iv.setAlpha(1f);
        view_main_center_shadow_iv.animate().scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .start();

    }


}

