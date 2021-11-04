package com.taxialaan.app.Activities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.taxialaan.app.Helper.AppHelper;
import com.taxialaan.app.Helper.ConnectionHelper;
import com.taxialaan.app.Helper.CustomDialog;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.Helper.URLHelper;
import com.taxialaan.app.Helper.VolleyMultipartRequest;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.LocaleManager;
import com.taxialaan.app.Utils.MyBoldTextView;
import com.taxialaan.app.G;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int SELECT_PHOTO = 100;
    private static String TAG = "EditProfile";
    public Context context = EditProfile.this;
    public Activity activity = EditProfile.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button saveBTN;
    ImageView backArrow;
    MyBoldTextView changePasswordTxt;
    EditText email, first_name, last_name, mobile_no;
    ImageView profile_Image;
    Spinner languageSpinner;

    Boolean isImageChanged = false;
    public static int deviceHeight;
    public static int deviceWidth;
    String languageToLoad = "en";
    Uri uri;

    private CustomDialog customDialogNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        findViewByIdandInitialization();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        if (SharedHelper.getKey(context, "login_by").equalsIgnoreCase("facebook")){
            changePasswordTxt.setVisibility(View.GONE);
        } else {
            changePasswordTxt.setVisibility(View.INVISIBLE);
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());


                /*if (email.getText().toString().equals("") || email.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.email_validation));
                } else*/ if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.mobile_number_empty));
                } else if (first_name.getText().toString().equals("") || first_name.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.first_name_empty));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.last_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(getString(R.string.first_name_no_number));
                } else if (lastName.matches()) {
                    displayMessage(getString(R.string.last_name_no_number));
                } else {
                    if (isInternet) {
                        updateProfile();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }


            }
        });


        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, ChangePassword.class));
            }
        });


        profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkStoragePermission())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    }
                else
                    goToImageIntent();
            }
        });




    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                    goToImageIntent();
    }

    public void goToImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;

            try {
                isImageChanged = true;
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
                    profile_Image.setImageBitmap(reRotateImg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }


    public void updateProfile() {

        if (isImageChanged == true) {
            isImageChanged = false;
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UseProfileUpdate, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    Log.e(TAG, "onResponse: update profile" + response );
                    String res = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                        SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                        SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                        SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                        SharedHelper.putKey(context, "lang", jsonObject.optString("lang"));
                        if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                            SharedHelper.putKey(context, "picture", "");
                        } else {
                            if (jsonObject.optString("picture").startsWith("http"))
                                SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                            else
                                SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + jsonObject.optString("picture"));
                        }

                        SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                        SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                        SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                        SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));

                        updateLocale();

                        Toast.makeText(EditProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        //displayMessage(getString(R.string.update_success));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    Log.e(TAG, "" + error);
                    displayMessage(getString(R.string.something_went_wrong));

                }
            }) {
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("first_name", first_name.getText().toString());
                    params.put("last_name", last_name.getText().toString());
//                    params.put("email", email.getText().toString());
                    params.put("mobile", mobile_no.getText().toString());
                    String selected_lang = languageToLoad;
                    if(languageToLoad.equalsIgnoreCase("ku")){
                        selected_lang = "ar_IQ";
                    }
                    params.put("lang",selected_lang);
                    Log.e(TAG, "getParams: pro update Request body"+params);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }

                @Override
                protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                    Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                    params.put("picture", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                    return params;
                }
            };
            G.getInstance().addToRequestQueue(volleyMultipartRequest);
        } else {
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UseProfileUpdate, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    Log.e(TAG, "onResponse: update profile" + response );
                    String res = new String(response.data);
                    Log.e(TAG, "onResponse: update profile" + res );
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                        SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                        SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                        SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                        SharedHelper.putKey(context, "lang", jsonObject.optString("lang"));
                        if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                            SharedHelper.putKey(context, "picture", "");
                        } else {
                            if (jsonObject.optString("picture").startsWith("http"))
                                SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                            else
                                SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + jsonObject.optString("picture"));
                        }
                        SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                        SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                        SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                        SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));

                        updateLocale();

                        Toast.makeText(EditProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        //displayMessage(getString(R.string.update_success));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    Log.e(TAG, "" + error);
                    displayMessage(getString(R.string.something_went_wrong));

                }
            }) {
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("first_name", first_name.getText().toString());
                    params.put("last_name", last_name.getText().toString());
                   // params.put("email", email.getText().toString());
                    params.put("mobile", mobile_no.getText().toString());
                    String selected_lang = languageToLoad;
                    if(languageToLoad.equalsIgnoreCase("ku")){
                        selected_lang = "ar_IQ";
                    }
                    params.put("lang",selected_lang);
                    params.put("picture", "");
                    Log.e(TAG, "getParams: pro update Request body"+params);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };
            G.getInstance().addToRequestQueue(volleyMultipartRequest);
        }
    }

    private void updateLocale() {
        SharedHelper.putKey(EditProfile.this,"lang",languageToLoad);
        LocaleManager.setLocale(EditProfile.this, languageToLoad);
        restartActivity();
    }



    public void findViewByIdandInitialization() {
        email = (EditText) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        saveBTN = (Button) findViewById(R.id.saveBTN);
        changePasswordTxt = (MyBoldTextView) findViewById(R.id.changePasswordTxt);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        profile_Image = (ImageView) findViewById(R.id.img_profile);
        languageSpinner = (Spinner) findViewById(R.id.language_spinner);
        languageSpinner.setOnItemSelectedListener(this);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        String language = LocaleManager.getLanguage(EditProfile.this);
        if (language.equalsIgnoreCase("en"))
            languageSpinner.setSelection(0);
        else if (language.equalsIgnoreCase("ar"))
            languageSpinner.setSelection(1);
        else if (language.equalsIgnoreCase("ku"))
            languageSpinner.setSelection(2);

        //Assign current profile values to the edittext
        //Glide.with(activity).load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(profile_Image);
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "picture").equalsIgnoreCase(null)
                && SharedHelper.getKey(context, "picture") != null) {
            Picasso.with(context)
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile_Image);
        } else {
            Picasso.with(context)
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile_Image);
        }
        email.setText(SharedHelper.getKey(context, "email"));
        first_name.setText(SharedHelper.getKey(context, "first_name"));
        last_name.setText(SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "mobile")!= null
                && !SharedHelper.getKey(context, "mobile").equals("null")
                && !SharedHelper.getKey(context, "mobile").equals("")) {
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
        }
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    public void restartActivity(){
        customDialogNew = new CustomDialog(EditProfile.this, getResources().getString(R.string.language_update));
        if (customDialogNew != null)
            customDialogNew.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customDialogNew.dismiss();
                Intent mainIntent = new Intent(EditProfile.this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        }, 3000);
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try{
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0){
            languageToLoad  = "en"; // English language
        }
        if (position == 1){
            languageToLoad  = "ar"; // Arabic language
        }
        if (position == 2){
            languageToLoad  = "ku"; // Kurdish language
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
