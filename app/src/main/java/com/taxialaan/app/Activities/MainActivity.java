package com.taxialaan.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.taxialaan.app.Activities.wallet.ActivityWalletManager;
import com.taxialaan.app.Api.ApiClient;
import com.taxialaan.app.Api.ApiInterface;
import com.taxialaan.app.Api.Repository;
import com.taxialaan.app.Api.interfaces.CallBack;
import com.taxialaan.app.Api.response.ResponseCheckPushy;
import com.taxialaan.app.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.app.Api.response.UpdateResponse;
import com.taxialaan.app.Api.utils.RequestException;
import com.taxialaan.app.Fragments.Coupon;
import com.taxialaan.app.Fragments.Help;
import com.taxialaan.app.Fragments.HomeFragment;
import com.taxialaan.app.Fragments.Payment;
import com.taxialaan.app.Fragments.Wallet;
import com.taxialaan.app.Fragments.YourTrips;
import com.taxialaan.app.G;
import com.taxialaan.app.Helper.CustomDialog;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.Helper.URLHelper;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.CustomTypefaceSpan;
import com.taxialaan.app.Utils.LocaleManager;
import com.taxialaan.app.Utils.MyTextView;
import com.taxialaan.app.Utils.NotificationCenter;
import com.taxialaan.app.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;
import retrofit2.Call;
import retrofit2.Callback;

import static com.taxialaan.app.G.trimMessage;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener, NotificationCenter.NotificationCenterDelegate {


    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PAYMENT = "payments";
    private static final String TAG_YOURTRIPS = "yourtrips";
    private static final String TAG_COUPON = "coupon";
    private static final String TAG_WALLET = "wallet";
    private static final String TAG_HELP = "help";
    private static final String TAG_SHARE = "share";
    private static final String TAG_LOGOUT = "logout";
    public Context context = MainActivity.this;
    public Activity activity = MainActivity.this;
    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtWebsite;
    private MyTextView txtName, amountTxt;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    CustomDialog customDialog;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private String notificationMsg;
    String  token = "";

    private static final int REQUEST_LOCATION = 1450;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            notificationMsg = intent.getExtras().getString("Notification");

        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (MyTextView) navHeader.findViewById(R.id.usernameTxt);
        amountTxt = (MyTextView) navHeader.findViewById(R.id.amountTxt);
        txtWebsite = (TextView) navHeader.findViewById(R.id.status_txt);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, EditProfile.class));
            }
        });


        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        checkVersion();
        //checkToken();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateWalletAmount);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateWalletAmount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWalletView();
    }

    private void updateWalletView() {
        if (txtName != null) {
            txtName.setText(SharedHelper.getKey(context, "first_name"));
            txtName.append(" " + SharedHelper.getKey(context, "last_name"));
            txtWebsite.setText("");
            amountTxt.setText(Utils.getString(R.string.wallet_id));
            amountTxt.append(" : ");
            amountTxt.append(SharedHelper.getKey(context, "wallet_id"));
            amountTxt.append("\n");
            amountTxt.append(Utils.getString(R.string.balance) + " : ");
            amountTxt.append(SharedHelper.getKey(context, "currency"));
            amountTxt.append(" " + SharedHelper.getKey(context, "balance"));
            amountTxt.append("\n");
            amountTxt.append(getString(R.string.Introducer_code));
            amountTxt.append(" : ");
            amountTxt.append(SharedHelper.getKey(context,"share_key"));
            navHeader.invalidate();
        }


    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website

        // Loading profile image
        //Glide.with(activity).load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProfile);
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
            && !SharedHelper.getKey(context, "picture").equalsIgnoreCase(null) && SharedHelper.getKey(context, "picture") != null) {
            Picasso.with(context).load(SharedHelper.getKey(context, "picture"))
                   .placeholder(R.drawable.ic_dummy_user)
                   .error(R.drawable.ic_dummy_user)
                   .into(imgProfile);
        } else {
            Picasso.with(context).load(R.drawable.ic_dummy_user)
                   .placeholder(R.drawable.ic_dummy_user)
                   .error(R.drawable.ic_dummy_user)
                   .into(imgProfile);
        }

        // showing dot next to notifications label
        //  navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {

        SharedHelper.putKey(context, "current_status", "");
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    private Fragment getHomeFragment() {

        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = HomeFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("Notification", notificationMsg);
                homeFragment.setArguments(bundle);
                return homeFragment;
            case 1:
                // Payment fragment
                Payment paymentFragment = new Payment();
                return paymentFragment;
            case 2:
                // Your Trips
                YourTrips yourTripsFragment = new YourTrips();
                return yourTripsFragment;
            case 3:
                // Coupon
                Coupon couponFragment = new Coupon();
                return couponFragment;
            case 4:
                // wallet fragment
                Wallet walletFragment = new Wallet();
                return walletFragment;

            case 5:
                // Help fragment
                Help helpFragment = new Help();
                return helpFragment;

            default:
                return new HomeFragment();
        }

    }


    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;


                    case R.id.nav_privacy:
                        Intent i = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;

                    case R.id.nav_wallet_manager:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, ActivityWalletManager.class));
                        break;
                    case R.id.nav_payment:
                        drawer.closeDrawers();
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PAYMENT;
                        break;
                    case R.id.nav_yourtrips:
                        drawer.closeDrawers();
                      /*  navItemIndex = 2;
                        CURRENT_TAG = TAG_YOURTRIPS;*/
                        SharedHelper.putKey(context, "current_status", "");
                        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                        intent.putExtra("tag", "past");
                        startActivity(intent);
                        return true;
                    // break;
                    case R.id.nav_coupon:
                        drawer.closeDrawers();
                       /* navItemIndex = 3;
                        CURRENT_TAG = TAG_COUPON;
                        break;*/
                        SharedHelper.putKey(context, "current_status", "");
                        startActivity(new Intent(MainActivity.this, CouponActivity.class));
                        return true;
                    case R.id.nav_wallet:
                        drawer.closeDrawers();
                        /*navItemIndex = 4;
                        CURRENT_TAG = TAG_WALLET;*/
                        SharedHelper.putKey(context, "current_status", "");
                        startActivity(new Intent(MainActivity.this, ActivityWallet.class));
                        return true;
                    case R.id.nav_settings:
                        drawer.closeDrawers();
                        /*navItemIndex = 4;
                        CURRENT_TAG = TAG_WALLET;*/
                        SharedHelper.putKey(context, "current_status", "");
                        startActivity(new Intent(MainActivity.this, ActivitySettings.class));
                        return true;
                    case R.id.nav_help:
                        drawer.closeDrawers();
                       /* navItemIndex = 5;
                        CURRENT_TAG = TAG_HELP;*/
                        SharedHelper.putKey(context, "current_status", "");
                        startActivity(new Intent(MainActivity.this, ActivityHelp.class));
                        break;
                    case R.id.nav_share:
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        navigateToShareScreen(URLHelper.Code);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_logout:
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        showLogoutDialog();
                        return true;

                    default:
                        navItemIndex = 0;
                }
                loadHomeFragment();

                return true;
            }
        });

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem);

        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Repository.getInstance().getProfile(null);
                updateWalletView();
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("MainAct", "Google User Logged out");
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    public void logout() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(this, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "logout: " + object);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                drawer.closeDrawers();
                if (SharedHelper.getKey(context, "login_by").equals("facebook"))
                    LoginManager.getInstance().logOut();
                if (SharedHelper.getKey(context, "login_by").equals("google"))
                    signOut();

                SharedHelper.clearSharedPreferences(activity);

                Intent goToLogin = new Intent(activity, BeginScreen.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goToLogin);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            /*refreshAccessToken();*/
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
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
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                Log.e("getHeaders: Token", SharedHelper.getKey(context, "access_token") + SharedHelper.getKey(context, "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        G.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setTitle(context.getString(R.string.app_name))
                   .setIcon(R.mipmap.ic_launcher)
                   .setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                SharedHelper.putKey(context, "current_status", "");
                loadHomeFragment();
                return;
            } else {
                SharedHelper.putKey(context, "current_status", "");
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notification, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " \n " + getString(R.string.Introducer_code)+" : "+SharedHelper.getKey(context,"share_key"));
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }


    public void checkVersion() {

        PackageInfo pinfo = null;
        int versionNumber = 0;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumber = pinfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<UpdateResponse> call = apiInterface.updateVersionApp("user");
        final int finalVersionNumber = versionNumber;
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {

                if (response.isSuccessful()) {

                    if (Integer.valueOf(response.body().getVersion()) > finalVersionNumber) {

                        showDialogCheckVersion(response.body().getDec(), response.body().getUrl());
                    }

                }

            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                // dismissProgress();
                //  displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });

    }

    private void showDialogCheckVersion(String message, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
               .setIcon(R.mipmap.ic_launcher)
               .setMessage(message)
               .setCancelable(false)
               .setPositiveButton("OK",
                                  new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog,
                                                          int id) {

                                          Uri uri = Uri.parse(url);
                                          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                          startActivity(intent);

                                      }
                                  });

        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateWalletAmount) {
            updateWalletView();
        }
    }

    private void checkToken(){


        if (!SharedHelper.getKey(this,"device_token").equals("true")){

            Repository.getInstance().checkPushy(new CallBack<ResponseCheckPushy>() {
                @Override
                public void onSuccess(ResponseCheckPushy responseCheckPushy) {
                    super.onSuccess(responseCheckPushy);

                    if (responseCheckPushy.getToken().isEmpty() && responseCheckPushy.getToken() == null){

                        try {
                            token = Pushy.register(getApplicationContext());
                        } catch (PushyException e) {
                            e.printStackTrace();
                            checkToken();
                        }


                        if (Pushy.isRegistered(getApplicationContext()) && !token.isEmpty()) {
                            updateToken(token);
                        }

                    }else {
                        SharedHelper.putKey(getApplicationContext(),"device_token","true");
                    }
                }

                @Override
                public void onFail(RequestException e) {
                    super.onFail(e);
                }
            });

        }

    }

    private void updateToken(String token){

        Repository.getInstance().updateTokenPushy(token, new CallBack<ResponseUpdateTokenPushy>() {
            @Override
            public void onSuccess(ResponseUpdateTokenPushy responseUpdateTokenPushy) {
                super.onSuccess(responseUpdateTokenPushy);

                SharedHelper.putKey(getApplicationContext(),"device_token","true");
            }

            @Override
            public void onFail(RequestException e) {
                super.onFail(e);


            }
        });
    }

}