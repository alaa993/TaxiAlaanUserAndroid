package com.taxialaan.app.Constants;

import android.view.View;

public interface ItemClickListener<T> {
    void itemClicked(View view,T t);
}
