package com.taxialaan.app.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chaos.view.PinView;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.shuhart.stepview.StepView;
import com.splunk.mint.Mint;
import com.taxialaan.app.Api.ApiClient;
import com.taxialaan.app.Api.ApiInterface;
import com.taxialaan.app.Api.request.LoginRequest;
import com.taxialaan.app.Api.response.LoginResponse;
import com.taxialaan.app.Api.response.OTPResponse;
import com.taxialaan.app.G;
import com.taxialaan.app.Helper.ConnectionHelper;
import com.taxialaan.app.Helper.CustomDialog;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.Helper.URLHelper;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.Utils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

import static com.taxialaan.app.G.getInstance;
import static com.taxialaan.app.G.trimMessage;
import com.firebase.ui.auth.AuthUI;


public class BeginScreen extends AppCompatActivity {

    ConnectionHelper helper;
    Boolean isInternet;

    public Context context = BeginScreen.this;
    String TAG = "BEGINSCREEN";
    String device_token, device_UDID,country_code, phoneNumberString;
    Utils utils = new Utils();



    private int currentStep = 0;
    LinearLayout layout1,layout2,layout3;
    StepView stepView;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth;

    private String phoneNumber;
    private Button sendCodeButton;
    private Button verifyCodeButton;
    private Button signOutButton;
    private Button button3;

    private EditText phoneNum;
    private PinView verifyCodeET;
    private TextView phonenumberText,get_code;

    private Button login;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private CountryCodePicker countryCodePicker;


    private FirebaseAuth mAuth;
    private final int REQUESR_LOG = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.login);
        helper = new ConnectionHelper(BeginScreen.this);
        isInternet = helper.isConnectingToInternet();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder().setIsSmartLockEnabled(false).setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build())).setTheme(R.style.AppTheme).build(), REQUESR_LOG);
            }
        });








        GetToken();

    }





    private void verify(final String phoneNumber , LoginRequest request) {

        showLoading();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<OTPResponse> call = apiInterface.getOTP(phoneNumber);
        call.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, retrofit2.Response<OTPResponse> response) {
                hideLoading();

                if (response.isSuccessful()){

                    if (response.body() != null) {
                        if (response.body().getStatus() == 0) {
                            moveToRegisterActivity(phoneNumber);
                        } else {

                            LoginRequest request = new LoginRequest();
                            request.setMobile(phoneNumber);
                            //   request.setDevice_id(device_UDID);
                            //   request.setDevice_token(device_token);
                            //  request.setMobile(SharedHelper.getKey(getInstance(), "phone_number_login"));
                            request.setDevice_type("android");
                           // request.setUser_agent("" + Build.VERSION.SDK_INT + " " + Build.MODEL);
                            goToLogin(request);

                        }
                    }

                }else{
                    displayMessage(getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {
                hideLoading();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });
    }



    private void goToLogin(LoginRequest request) {

        showLoading();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.Login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful()){
                    SharedHelper.putKey(getApplicationContext(), "access_token", response.body().getAccessToken());
                    SharedHelper.putKey(getApplicationContext(), "refresh_token", response.body().getRefreshToken());
                    SharedHelper.putKey(getApplicationContext(), "token_type", response.body().getTokenType());
                    getProfile();
                }else {
                   hideLoading();
                   if (response.body() != null){
                       displayMessage(response.body().getMessage());
                   }else {
                       displayMessage(getResources().getString(R.string.something_went_wrong));
                   }

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
               hideLoading();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });

    }

    public void getProfile() {
        if (isInternet) {

            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URLHelper.UserProfile +"?lang=" + "en", object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                     hideLoading();
                    utils.print("GetProfile Login", response.toString());
                    SharedHelper.putKey(getInstance(), "id", response.optString("id"));
                    SharedHelper.putKey(getInstance(), "first_name", response.optString("first_name"));
                    SharedHelper.putKey(getInstance(), "last_name", response.optString("last_name"));
                    SharedHelper.putKey(getInstance(), "email", response.optString("email"));
                    if (response.optString("picture").startsWith("http"))
                        SharedHelper.putKey(getInstance(), "picture", response.optString("picture"));
                    else
                        SharedHelper.putKey(getInstance(), "picture", URLHelper.base+"storage/"+response.optString("picture"));
                    SharedHelper.putKey(getInstance(), "gender", response.optString("gender"));
                    SharedHelper.putKey(getInstance(), "mobile", response.optString("mobile"));
                    SharedHelper.putKey(getInstance(), "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(getInstance(), "payment_mode", response.optString("payment_mode"));
                    SharedHelper.putKey(getInstance(), "balance", response.optString("balance"));
                    SharedHelper.putKey(getInstance(), "wallet_id", response.optString("wallet_id"));
                    SharedHelper.putKey(context,"city",response.optString("city"));
                    if(!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(getInstance(), "currency",response.optString("currency"));
                    else
                        SharedHelper.putKey(getInstance(), "currency","$");
                    SharedHelper.putKey(getInstance(),"sos",response.optString("sos"));
                    SharedHelper.putKey(getInstance(), "loggedIn", getString(R.string.True));

                    GoToMainActivity();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  hideLoading();
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
                               // refreshAccessToken();
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
                    headers.put("Authorization", "" + SharedHelper.getKey(getInstance(), "token_type") + " "
                            + SharedHelper.getKey(getInstance(), "access_token"));
                    utils.print("authoization",""+SharedHelper.getKey(getInstance(), "token_type") + " "
                            + SharedHelper.getKey(getInstance(), "access_token"));
                    return headers;
                }
            };

            G.getInstance().addToRequestQueue(jsonObjectRequest);
        }else{
            displayMessage(getString(R.string.something_went_wrong_net));
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

//        try {
//            device_token = Pushy.register(getApplicationContext());
//        } catch (PushyException e) {
//            e.printStackTrace();
//        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    private  void moveToRegisterActivity(String phone){

        //String phone = phoneNum.getText().toString();
        SharedHelper.putKey(BeginScreen.this,"password", "");
        Intent mainIntent = new Intent(BeginScreen.this, RegisterActivity.class);
        mainIntent.putExtra("isFromMailActivity", true);
        mainIntent.putExtra("mobile",phone);
//        mainIntent.putExtra("codeCountry",countryCodePicker.getSelectedCountryCode());
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void GoToMainActivity(){

        Intent mainIntent = new Intent(BeginScreen.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void displayMessage(String toastString){
        utils.print("displayMessage",""+toastString);
        Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
//        Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
  //              .setAction("Action", null).show();
    }

    private ProgressDialog progressDialog;

    public void showLoading() {
        hideLoading();
        progressDialog = new ProgressDialog(BeginScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(Utils.getString(R.string.pls_wait));
        progressDialog.show();
    }


    public void hideLoading() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESR_LOG) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                    LoginRequest request = new LoginRequest();
                    //request.setDevice_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    //request.setDevice_token(FirebaseAuth.getInstance().getAccessToken(true).toString());
                    request.setMobile(SharedHelper.getKey(getInstance(), "phone_number_login"));
                   // request.setDevice_type("android");
                 //   request.setUser_agent("" + Build.VERSION.SDK_INT + " " + Build.MODEL);

                    verify(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),request);
                    //goToLogin(request);
                    return;
                } else {
                    if (response == null) {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, "NO internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, "Unkonw erorrs", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    moveToRegisterActivity();
                }
            }
        }
    }

}
