package com.taxialaan.app.Activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.taxialaan.app.Api.ApiClient;
import com.taxialaan.app.Api.ApiInterface;
import com.taxialaan.app.Api.request.RegisterRequest;
import com.taxialaan.app.Api.response.LoginResponse;
import com.taxialaan.app.Api.response.OTPResponse;
import com.taxialaan.app.CountryPicker.Country;
import com.taxialaan.app.CountryPicker.CountryPicker;
import com.taxialaan.app.G;
import com.taxialaan.app.Helper.ConnectionHelper;
import com.taxialaan.app.Helper.CustomDialog;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.Helper.URLHelper;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;
import retrofit2.Call;
import retrofit2.Callback;

import static com.taxialaan.app.G.getInstance;
import static com.taxialaan.app.G.trimMessage;

public class RegisterActivity extends AppCompatActivity {

    public Context context = RegisterActivity.this;
    public Activity activity = RegisterActivity.this;
    String TAG = "RegisterActivity";
    String device_token, device_UDID;
    ImageView backArrow,countryImage;
    FloatingActionButton nextICON;
    EditText email, first_name, last_name, phoneNumber, password,edCode;
    Spinner spinnercity;

    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utils utils = new Utils();
    Boolean fromActivity = false;
    TextView countryNumber;
    String country_code = "+964";
    String phoneNumberIntent = "";
    List<Country> countryList;
    public static final int VERIFY_MOBILE = 123;
    public static int APP_REQUEST_CODE = 99;


   // AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
    //UIManager uiManager;
   CountryPicker mCountryPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            device_token = Pushy.register(getApplicationContext());
        } catch (PushyException e) {
            e.printStackTrace();
        }
        try {
            Intent intent = getIntent();
            if (intent != null) {
                if (getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = true;
                    //country_code = getIntent().getExtras().getString("codeCountry");
                    phoneNumberIntent = getIntent().getExtras().getString("mobile");
                } else if (!getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = false;
                } else {
                    fromActivity = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fromActivity = false;
        }

        findViewById();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());
              //  String strPhoneNumber = phoneNumber.getText().toString().trim();

         //       if (TextUtils.isEmpty(strPhoneNumber)){
             //       displayMessage(getResources().getString(R.string.enter_your_mobile_number));
             //   }
               /* else if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                    displayMessage(getString(R.string.email_validation));
                }*/ /*else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                    displayMessage(getString(R.string.password_validation));
                }*//* else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().equalsIgnoreCase(getString(R.string.mobile_no))) {
                    displayMessage(getString(R.string.mobile_number_empty));
                }*/  if (first_name.getText().toString().equals("") || first_name.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                    displayMessage(getString(R.string.first_name_empty));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().equalsIgnoreCase(getString(R.string.last_name))) {
                    displayMessage(getString(R.string.last_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(getString(R.string.first_name_no_number));
                } else if (lastName.matches()) {
                    displayMessage(getString(R.string.last_name_no_number));
                } /*else if (password.length() < 6) {
                    displayMessage(getString(R.string.password_size));
                } */else {
                    if (isInternet) {
                        //phoneLogin();
                        saveRegisterFields();
                       // sendOTP(country_code+strPhoneNumber);
                        //checkMobi();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                Intent mainIntent = new Intent(RegisterActivity.this, ActivityPassword.class);
//                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mainIntent);
//                RegisterActivity.this.finish();
            }
        });

      /*  mCountryPicker = CountryPicker.newInstance("Select Country");
        // You can limit the displayed countries
         countryList = Country.getAllCountries();
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country s1, Country s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        mCountryPicker.setCountriesList(countryList);*/
       // setListener();
        GetToken();
    }

    private void sendOTP(String phoneNumber) {

        showProgress();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<OTPResponse> call = apiInterface.getOTP(phoneNumber);
        call.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, retrofit2.Response<OTPResponse> response) {
                dismissProgress();
                if (response.isSuccessful()){
                    moveToOTP(response.body().getRequestId());
                }else{
                    displayMessage(getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {
                dismissProgress();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });
    }


    private void showProgress() {
        if (customDialog == null) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
        }
    }

    private void dismissProgress() {
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
    }

    private void moveToOTP(String requestID) {
        Intent mainIntent = new Intent(RegisterActivity.this, ActivityPassword.class);
        mainIntent.putExtra("request_id",requestID);
        mainIntent.putExtra("action","register");
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    private void setListener() {
      /*  mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID) {
                countryNumber.setText(dialCode);
                country_code = dialCode;
                countryImage.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
            }
        });

        countryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        countryNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

                if (phoneNumber.getText().length() == 1){

                    if (phoneNumber.getText().toString().equals("0")) {
                        phoneNumber.setText("");
                    }
                }
            }

            @Override public void afterTextChanged(Editable editable) {

            }
        });

        getUserCountryInfo();*/
    }

    private void getUserCountryInfo() {
        Locale current = getResources().getConfiguration().locale;
        Country country = Country.getCountryFromSIM(RegisterActivity.this);
        if (country != null) {
            countryImage.setImageResource(country.getFlag());
            countryNumber.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            for (Country item:countryList){

                if (item.getDialCode().equals(country_code)) {

                    countryImage.setImageResource(item.getFlag());
                    countryNumber.setText(item.getDialCode());
                    country_code = item.getDialCode();
                }

            }
        }
    }

    private void saveRegisterFields() {

        SharedHelper.putKey(context,"first_name",first_name.getText().toString());
        SharedHelper.putKey(context,"last_name",last_name.getText().toString());
        SharedHelper.putKey(context,"email",email.getText().toString());
       //SharedHelper.putKey(context,"password",password.getText().toString());
        SharedHelper.putKey(RegisterActivity.this,"phone_number_register",phoneNumberIntent);
        SharedHelper.putKey(context,"city",spinnercity.getSelectedItem().toString());
        Log.d("ttttalaa", spinnercity.getSelectedItem().toString());
        //SharedHelper.putKey(RegisterActivity.this,"countrycode_register","+"+country_code);

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setDevice_id(device_UDID);
        registerRequest.setDevice_token(device_token);
        registerRequest.setMobile(phoneNumber.getText().toString());
       // registerRequest.setCountry_code(SharedHelper.getKey(getInstance(),"countrycode_register"));
        registerRequest.setFirst_name(SharedHelper.getKey(getInstance(),"first_name"));
        registerRequest.setLast_name(SharedHelper.getKey(getInstance(),"last_name"));
        registerRequest.setEmail(SharedHelper.getKey(getInstance(),"email"));
        registerRequest.setShare_key(edCode.getText().toString());
        registerRequest.setApp_version("" + versionNumber);
        registerRequest.setCity(SharedHelper.getKey(getInstance(),"city"));

        goToRegister(registerRequest);


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
    }

    public void findViewById() {
        email = findViewById(R.id.email);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        edCode = findViewById(R.id.edtCode);
        phoneNumber = findViewById(R.id.phoneNumber);
        nextICON = findViewById(R.id.nextIcon);
        backArrow = findViewById(R.id.backArrow);
        countryImage = findViewById(R.id.countryImage);
        spinnercity = findViewById(R.id.spinnerCity);
        helper = new ConnectionHelper(context);

        String[] items = new String[]{"Erbil", "Sulaymaniyah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        spinnercity.setAdapter(adapter);
      //  countryNumber = findViewById(R.id.countryNumber);
        isInternet = helper.isConnectingToInternet();
        if (!fromActivity) {
            email.setText(SharedHelper.getKey(context, "email"));
        }

        phoneNumber.setText(phoneNumberIntent);
      //  countryNumber.setText(country_code);
        phoneNumber.setEnabled(false);

    }

    private void checkMobi() {
      //  Intent intent = new Intent(this,CheckValidationActivity.class);
        //startActivityForResult(intent,VERIFY_MOBILE);
       // startActivity(intent);
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_MOBILE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isVerified", false);
                String phoneNumber = data.getStringExtra("phone");
                SharedHelper.putKey(RegisterActivity.this, "mobile", phoneNumber);
                if (result) {
                    //registerAPI();
                }
            }
        }
    }*/

    private void goToRegister(RegisterRequest request) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.Register(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                dismissProgress();

                if (response.isSuccessful()){
                    SharedHelper.putKey(context, "access_token", response.body().getAccessToken());
                    SharedHelper.putKey(context, "refresh_token", response.body().getRefreshToken());
                    SharedHelper.putKey(context, "token_type", response.body().getTokenType());
                    getProfile();
                }else {
                    dismissProgress();
                    displayMessage(getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dismissProgress();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });
    }


    public void getProfile() {
        if (isInternet) {
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            if(customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URLHelper.UserProfile +"?lang=" + "en", object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    utils.print("GetProfile Login", response.toString());
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    if (response.optString("picture").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("picture"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base+"storage/"+response.optString("picture"));
                    SharedHelper.putKey(context, "gender", response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                    SharedHelper.putKey(context, "balance", response.optString("balance"));
                    SharedHelper.putKey(context, "wallet_id", response.optString("wallet_id"));
                    SharedHelper.putKey(context,"share_key",response.optString("share_key"));
                    SharedHelper.putKey(context,"city",response.optString("city"));
                    if(!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency",response.optString("currency"));
                    else
                        SharedHelper.putKey(context, "currency","$");
                    SharedHelper.putKey(context,"sos",response.optString("sos"));
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));

                    GoToMainActivity();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken();
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            }else if(response.statusCode == 503){
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    utils.print("authoization",""+SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            G.getInstance().addToRequestQueue(jsonObjectRequest);
        }else{
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }


    private void refreshAccessToken() {
        if (isInternet) {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            if(customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
                object.put("scope", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);

                    if (response != null && response.data != null) {
                        //    SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
                        GoToBeginActivity();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            G.getInstance().addToRequestQueue(jsonObjectRequest);

        }else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        activity.finish();
    }

    public void GoToBeginActivity(){
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
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
        if (fromActivity) {
            Intent mainIntent = new Intent(RegisterActivity.this, BeginScreen.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        } else {
            Intent mainIntent = new Intent(RegisterActivity.this, BeginScreen.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }
    }

    public void GetToken() {

        try {
            if (!SharedHelper.getKeyDeviceToken(getInstance(), "device_token").equals("") && SharedHelper.getKeyDeviceToken(getInstance(), "device_token") != null) {
                device_token = SharedHelper.getKeyDeviceToken(getInstance(), "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

//            try {
//                device_token = Pushy.register(getApplicationContext());
//            } catch (PushyException e) {
//                e.printStackTrace();
//            }

            try {
                device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                utils.print(TAG, "Device UDID:" + device_UDID);
            } catch (Exception e) {
                device_UDID = "COULD NOT GET UDID";
                e.printStackTrace();
                utils.print(TAG, "Failed to complete device UDID");
            }

    }

}
