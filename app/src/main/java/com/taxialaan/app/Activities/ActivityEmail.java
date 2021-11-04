package com.taxialaan.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.taxialaan.app.Api.ApiClient;
import com.taxialaan.app.Api.ApiInterface;
import com.taxialaan.app.Api.response.OTPResponse;
import com.taxialaan.app.CountryPicker.Country;
import com.taxialaan.app.CountryPicker.CountryPicker;
import com.taxialaan.app.CountryPicker.CountryPickerListener;
import com.taxialaan.app.Helper.CustomDialog;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.MyTextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jayakumar on 31/01/17.
 */

public class ActivityEmail extends AppCompatActivity {

    ImageView backArrow,countryImage;
    FloatingActionButton nextICON;
    EditText phoneNumber;
    MyTextView register, forgetPassword;
    CountryPicker mCountryPicker;
    TextView countryNumber;

    String country_code = "+964";
    CustomDialog customDialog;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
        nextICON = (FloatingActionButton) findViewById(R.id.right_arrow);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        countryImage = (ImageView) findViewById(R.id.countryImage);
        register = (MyTextView) findViewById(R.id.register);
        forgetPassword = (MyTextView) findViewById(R.id.forgetPassword);
        countryNumber = (TextView) findViewById(R.id.countryNumber);

        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPhoneNumber = phoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(strPhoneNumber)){
                    displayMessage(getString(R.string.mobile_number_validation));
                }else if(strPhoneNumber.length()<10){
                    displayMessage(getString(R.string.valid_mobile_number));
                }else{
                    SharedHelper.putKey(ActivityEmail.this,"phone_number_login",strPhoneNumber);
                    sendOTP(strPhoneNumber);
                }



            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* SharedHelper.putKey(ActivityEmail.this,"email", "");
                Intent mainIntent = new Intent(ActivityEmail.this, BeginScreen.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);

                ActivityEmail.this.finish();*/
                onBackPressed();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityEmail.this,"password", "");
                Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityEmail.this,"password", "");
                Intent mainIntent = new Intent(ActivityEmail.this, ForgetPassword.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
            }
        });

        mCountryPicker = CountryPicker.newInstance("Select Country");
        // You can limit the displayed countries
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country s1, Country s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
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
        mCountryPicker.setCountriesList(countryList);
        setListener();


    }

    private void sendOTP(String phoneNumber) {

        showProgress();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<OTPResponse> call = apiInterface.getOTP(phoneNumber);
        call.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                dismissProgress();
                if (response.isSuccessful()){
                    if (response.body().getStatus() == 0) {
                        moveToRegisterActivity();
                    }else {
                        moveToOTP(response.body().getRequestId());
                    }
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
            customDialog = new CustomDialog(ActivityEmail.this);
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
        Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
        mainIntent.putExtra("request_id",requestID);
        mainIntent.putExtra("action","login");
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private  void moveToRegisterActivity(){

        SharedHelper.putKey(ActivityEmail.this,"password", "");
        Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
        mainIntent.putExtra("isFromMailActivity", true);
        mainIntent.putExtra("mobile",phoneNumber.getText().toString().trim());
      //  mainIntent.putExtra("codeCountry",country_code);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
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

        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Locale current = getResources().getConfiguration().locale;
        Country country = Country.getCountryFromSIM(ActivityEmail.this);
        if (country != null) {
            countryImage.setImageResource(country.getFlag());
            countryNumber.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            Country india = new Country("IQ", "Iraq", "+964", R.drawable.flag_iq);
            countryImage.setImageResource(india.getFlag());
            countryNumber.setText(india.getDialCode());
            country_code = india.getDialCode();
        }
    }

    public void displayMessage(String toastString){
        try{
            Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



   /* @Override
    public void onBackPressed() {
        SharedHelper.putKey(ActivityEmail.this,"email", "");
        Intent mainIntent = new Intent(ActivityEmail.this, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        ActivityEmail.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}