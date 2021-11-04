package com.taxialaan.app.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;
import com.taxialaan.app.Activities.AddCard;
import com.taxialaan.app.Activities.BeginScreen;
import com.taxialaan.app.Activities.CustomGooglePlacesSearch;
import com.taxialaan.app.Activities.HistoryActivity;
import com.taxialaan.app.Activities.MainActivity;
import com.taxialaan.app.Activities.ShowProfile;
import com.taxialaan.app.Api.Repository;
import com.taxialaan.app.G;
import com.taxialaan.app.Helper.ConnectionHelper;
import com.taxialaan.app.Helper.CustomDialog;
import com.taxialaan.app.Helper.DirectionsJSONParser;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.Helper.URLHelper;
import com.taxialaan.app.Models.CardInfo;
import com.taxialaan.app.Models.Driver;
import com.taxialaan.app.Models.PlacePredictions;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.Constants;
import com.taxialaan.app.Utils.Function;
import com.taxialaan.app.Utils.LocaleManager;
import com.taxialaan.app.Utils.MapAnimator;
import com.taxialaan.app.Utils.MapRipple;
import com.taxialaan.app.Utils.MyBoldTextView;
import com.taxialaan.app.Utils.MyButton;
import com.taxialaan.app.Utils.MyEditText;
import com.taxialaan.app.Utils.MyTextView;
import com.taxialaan.app.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.taxialaan.app.G.trimMessage;


public class HomeFragment extends Fragment implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnCameraChangeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener {

    private static final String TAG = "HomeFragment";

    Activity activity;
    Context context;
    View rootView;
    HomeFragmentListener listener;

    TextView tv_marker_text;
    String minsReturn = "0";


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    public interface HomeFragmentListener {
    }

    int totalRideAmount = 0, walletAmountDetected = 0, couponAmountDetected = 0;
    String isPaid = "", paymentMode = "";
    Utils utils = new Utils();
    int flowValue = 0;
    String strCurrentStatus = "";
    DrawerLayout drawer;
    int NAV_DRAWER = 0;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    private final int ADD_CARD_CODE = 435;
    private static final int REQUEST_LOCATION = 1450;
    String feedBackRating;
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    double height;
    double width;
    public String PreviousStatus = "";
    public String CurrentStatus = "";
    Handler handleCheckStatus;
    String strPickLocation = "", strTag = "", strPickType = "source";
    boolean once = true;
    int click = 1;
    boolean afterToday = false;
    boolean pick_first = true;
    Driver driver;
    //        <!-- Map frame -->
    LinearLayout mapLayout;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    Marker marker;
    Double latitude, longitude;
    String currentAddress;
    GoogleApiClient mGoogleApiClient;
    PlacePredictions placePredictions = new PlacePredictions();
    CustomMarker customMarker;
    //        <!-- Source and Destination Layout-->
    LinearLayout sourceAndDestinationLayout;
    FrameLayout frmSource, frmDestination;
    MyBoldTextView source, destination;
    ImageView imgMenu, mapfocus, imgBack, shadowBack;
    View tripLine;
    LinearLayout errorLayout;
//       <!--1. Request to providers -->

    LinearLayout lnrRequestProviders;
    RecyclerView rcvServiceTypes;
    ImageView imgPaymentType;
    ImageView imgSos;
    ImageView imgShareRide;
    MyBoldTextView lblPaymentType, lblPaymentChange, booking_id;
    MyButton btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";
    String flowStatus = "";

//        <!--1. Driver Details-->

    LinearLayout lnrHidePopup, lnrSearchAnimation, lnrProviderPopup, lnrPriceBase, lnrPricemin, lnrPricekm;
    ImageView imgProviderPopup;
    MyBoldTextView lblPriceMin, lblBasePricePopup, lblCapacity, lblServiceName, lblPriceKm, lblCalculationType, lblProviderDesc;
    MyButton btnDonePopup;

//         <!--2. Approximate Rate ...-->

    LinearLayout lnrApproximate;
    MyButton btnRequestRideConfirm;
    MyButton imgSchedule;
    //CheckBox chkWallet;
    // CheckBox chkServiceWallet;
    MyBoldTextView lblEta;
    MyBoldTextView lblType;
    MyBoldTextView lblApproxAmount, surgeDiscount, surgeTxt;
    View lineView;
    View line1View;

    LinearLayout ScheduleLayout;
    MyBoldTextView scheduleDate;
    MyBoldTextView scheduleTime;
    MyButton scheduleBtn;
    DatePickerDialog datePickerDialog;

    LocationRequest mLocationRequest;

//         <!--3. Waiting For Providers ...-->

    RelativeLayout lnrWaitingForProviders;
    MyBoldTextView lblNoMatch;
    ImageView imgCenter;
    MyButton btnCancelRide;
    private boolean mIsShowing;
    private boolean mIsHiding;
    RippleBackground rippleBackground;
    LinearLayout ln_internet;

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

//         <!--4. Driver Accepted ...-->

    LinearLayout lnrProviderAccepted, lnrAfterAcceptedStatus, AfterAcceptButtonLayout;
    ImageView imgProvider, imgServiceRequested;
    MyBoldTextView lblProvider, lblStatus, lblServiceRequested, lblModelNumber, lblSurgePrice, lblestimated;
    RatingBar ratingProvider;
    MyButton btnCall, btnCancelTrip;

//          <!--5. Invoice Layout ...-->

    LinearLayout lnrInvoice;
    MyBoldTextView lblBasePrice, lblExtraPrice, lblDistancePrice, lblCommision, lblTaxPrice, lblDiscountPrice, lblPaymentTypeInvoice,
            lblPaymentChangeInvoice;
    TextView lblTotalPrice, txt04Trip, txt04Copun;
    ImageView imgPaymentTypeInvoice;
    MyButton btnPayNow;
    MyButton btnPaymentDoneBtn;
    LinearLayout discountDetectionLayout, walletDetectionLayout;

//          <!--6. Rate provider Layout ...-->

    LinearLayout lnrRateProvider;
    MyBoldTextView lblProviderNameRate;
    ImageView imgProviderRate;
    RatingBar ratingProviderRate;
    EditText txtCommentsRate;
    Button btnSubmitReview;
    Float pro_rating;

//            <!-- Static marker-->

    LinearLayout rtlStaticMarker;
    ImageView imgDestination;
    ImageButton view_main_location_selector_ib;
    CustomMarker btnDone;
    CameraPosition cmPosition;


    String current_lat = "", current_lng = "", current_address = "", source_lng = "", source_address = "", source_lat = "",
            dest_lat = "", dest_lng = "", dest_address = "";

    private double s_lat_user, s_lot_user;
    LatLng myLocation;

    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    //RecylerView
    int currentPostion = 0;
    CustomDialog customDialog;

    //MArkers
    Marker availableProviders;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();
    AlertDialog alert;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    ParserTask parserTask;
    String notificationTxt;
    boolean scheduleTrip = false;

    MapRipple mapRipple;
    double wallet_balance;
    Button arabicButton;
    Button kurdishButton;
    Button englishButton;
    Button currentTemperatureField;

    private Button btnCancel;
    CircleImageView img_whatsapp;
    CircleImageView img_viber;
    CircleImageView img_call_phone;
    LinearLayout lnCall;

    MyEditText ttime;

    int count = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    long timerMoveMap = 0;

    private JSONArray waypoints = new JSONArray();
    ArrayList<LatLng> pointss;
    PolylineOptions lineOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            notificationTxt = bundle.getString("Notification");
            Log.e("HomeFragment", "onCreate: Notification" + notificationTxt);
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }

        customDialog = new CustomDialog(getActivity());
        if (customDialog != null)
            customDialog.show();
        init(rootView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    initMap();
                    MapsInitializer.initialize(getActivity());
                }
            }
        }, 500);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            listener = (HomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void init(View rootView) {


        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        statusCheck();
        getCards();

        img_viber = rootView.findViewById(R.id.img_viber);
        img_whatsapp = rootView.findViewById(R.id.img_whatsapp);
        img_call_phone = rootView.findViewById(R.id.img_call_phone);
        lnCall = rootView.findViewById(R.id.layer_call_phone);
        btnCancel = rootView.findViewById(R.id.btnCancelCall);

//        <!-- Map frame -->
        mapLayout = rootView.findViewById(R.id.mapLayout);
        drawer = rootView.findViewById(R.id.drawer_layout);
        drawer = activity.findViewById(R.id.drawer_layout);
        customMarker = rootView.findViewById(R.id.customMarker);

//        <!-- Source and Destination Layout-->
        sourceAndDestinationLayout = rootView.findViewById(R.id.sourceAndDestinationLayout);
        frmSource = rootView.findViewById(R.id.frmSource);
        frmDestination = rootView.findViewById(R.id.frmDestination);
        source = rootView.findViewById(R.id.source);
        destination = rootView.findViewById(R.id.destination);
        imgMenu = rootView.findViewById(R.id.imgMenu);
        imgSos = rootView.findViewById(R.id.imgSos);
        imgShareRide = rootView.findViewById(R.id.imgShareRide);
        mapfocus = rootView.findViewById(R.id.mapfocus);
        imgBack = rootView.findViewById(R.id.imgBack);
        shadowBack = rootView.findViewById(R.id.shadowBack);
        tripLine = rootView.findViewById(R.id.trip_line);

//        <!-- Request to providers-->

        lnrRequestProviders = rootView.findViewById(R.id.lnrRequestProviders);
        rcvServiceTypes = rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = rootView.findViewById(R.id.imgPaymentType);
        lblPaymentType = rootView.findViewById(R.id.lblPaymentType);
        lblPaymentChange = rootView.findViewById(R.id.lblPaymentChange);
        booking_id = rootView.findViewById(R.id.booking_id);
        btnRequestRides = rootView.findViewById(R.id.btnRequestRides);

//        <!--  Driver and service type Details-->

        lnrSearchAnimation = rootView.findViewById(R.id.lnrSearch);
        lnrProviderPopup = rootView.findViewById(R.id.lnrProviderPopup);
        lnrPriceBase = rootView.findViewById(R.id.lnrPriceBase);
        lnrPricekm = rootView.findViewById(R.id.lnrPricekm);
        lnrPricemin = rootView.findViewById(R.id.lnrPricemin);
        lnrHidePopup = rootView.findViewById(R.id.lnrHidePopup);

        imgProviderPopup = rootView.findViewById(R.id.imgProviderPopup);

        lblServiceName = rootView.findViewById(R.id.lblServiceName);
        lblCapacity = rootView.findViewById(R.id.lblCapacity);
        lblPriceKm = rootView.findViewById(R.id.lblPriceKm);
        lblPriceMin = rootView.findViewById(R.id.lblPriceMin);
        lblCalculationType = rootView.findViewById(R.id.lblCalculationType);
        lblBasePricePopup = rootView.findViewById(R.id.lblBasePricePopup);
        lblProviderDesc = rootView.findViewById(R.id.lblProviderDesc);

        btnDonePopup = rootView.findViewById(R.id.btnDonePopup);


//         <!--2. Approximate Rate ...-->

        lnrApproximate = rootView.findViewById(R.id.lnrApproximate);
        imgSchedule = rootView.findViewById(R.id.imgSchedule);
//        chkWallet = (CheckBox) rootView.findViewById(R.id.chkWallet);
        //  chkServiceWallet = (CheckBox) rootView.findViewById(R.id.chkServiceWallet);
        lblEta = rootView.findViewById(R.id.lblEta);
        lblType = rootView.findViewById(R.id.lblType);
        lblApproxAmount = rootView.findViewById(R.id.lblApproxAmount);
        surgeDiscount = rootView.findViewById(R.id.surgeDiscount);
        surgeTxt = rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = rootView.findViewById(R.id.btnRequestRideConfirm);
        lineView = rootView.findViewById(R.id.lineView);
        line1View = rootView.findViewById(R.id.line1View);

        //Schedule Layout
        ScheduleLayout = rootView.findViewById(R.id.ScheduleLayout);
        scheduleDate = rootView.findViewById(R.id.scheduleDate);
        scheduleTime = rootView.findViewById(R.id.scheduleTime);
        scheduleBtn = rootView.findViewById(R.id.scheduleBtn);

//         <!--3. Waiting For Providers ...-->

        lnrWaitingForProviders = rootView.findViewById(R.id.lnrWaitingForProviders);
        lblNoMatch = rootView.findViewById(R.id.lblNoMatch);
        //imgCenter = (ImageView) rootView.findViewById(R.id.imgCenter);
        btnCancelRide = rootView.findViewById(R.id.btnCancelRide);
        //  rippleBackground = (RippleBackground) rootView.findViewById(R.id.content);

//          <!--4. Driver Accepted ...-->

        lnrProviderAccepted = rootView.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = rootView.findViewById(R.id.lnrAfterAcceptedStatus);
        AfterAcceptButtonLayout = rootView.findViewById(R.id.AfterAcceptButtonLayout);
        imgProvider = rootView.findViewById(R.id.imgProvider);
        imgServiceRequested = rootView.findViewById(R.id.imgServiceRequested);
        lblProvider = rootView.findViewById(R.id.lblProvider);
        lblStatus = rootView.findViewById(R.id.lblStatus);
        lblSurgePrice = rootView.findViewById(R.id.lblSurgePrice);
        lblServiceRequested = rootView.findViewById(R.id.lblServiceRequested);
        lblModelNumber = rootView.findViewById(R.id.lblModelNumber);
        ratingProvider = rootView.findViewById(R.id.ratingProvider);
        btnCall = rootView.findViewById(R.id.btnCall);
        btnCancelTrip = rootView.findViewById(R.id.btnCancelTrip);
        lblestimated = rootView.findViewById(R.id.estimated);

//           <!--5. Invoice Layout ...-->

        lnrInvoice = rootView.findViewById(R.id.lnrInvoice);
        lblBasePrice = rootView.findViewById(R.id.lblBasePrice);
        lblExtraPrice = rootView.findViewById(R.id.lblExtraPrice);
        lblDistancePrice = rootView.findViewById(R.id.lblDistancePrice);
        //lblCommision = (MyBoldTextView) rootView.findViewById(R.id.lblCommision);
        lblTaxPrice = rootView.findViewById(R.id.lblTaxPrice);
        lblDiscountPrice = rootView.findViewById(R.id.lblDiscountPrice);
        lblTotalPrice = rootView.findViewById(R.id.lblTotalPrice);
        txt04Trip = rootView.findViewById(R.id.txt04Trip);
        txt04Copun = rootView.findViewById(R.id.txt04Copun);
        lblPaymentTypeInvoice = rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = rootView.findViewById(R.id.imgPaymentTypeInvoice);
        btnPayNow = rootView.findViewById(R.id.btnPayNow);
        btnPaymentDoneBtn = rootView.findViewById(R.id.btnPaymentDoneBtn);
        walletDetectionLayout = rootView.findViewById(R.id.walletDetectionLayout);
        discountDetectionLayout = rootView.findViewById(R.id.discountDetectionLayout);

//          <!--6. Rate provider Layout ...-->

        lnrRateProvider = rootView.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = rootView.findViewById(R.id.lblProviderName);
        imgProviderRate = rootView.findViewById(R.id.imgProviderRate);
        txtCommentsRate = rootView.findViewById(R.id.txtComments);
        ratingProviderRate = rootView.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = (MyButton) rootView.findViewById(R.id.btnSubmitReview);


        ln_internet = rootView.findViewById(R.id.fragmet_ln_internet);
//            <!--Static marker-->

        rtlStaticMarker = rootView.findViewById(R.id.rtlStaticMarker);

        ttime = rootView.findViewById(R.id.ttime);
        // imgDestination = rootView.findViewById(R.id.imgDestination);
        // view_main_location_selector_ib = rootView.findViewById(R.id.view_main_location_selector_ib);
        // btnDone = rootView.findViewById(R.id.customMarker);


        btnRequestRides.setOnClickListener(new OnClick());
        btnDonePopup.setOnClickListener(new OnClick());
        lnrHidePopup.setOnClickListener(new OnClick());
        btnRequestRideConfirm.setOnClickListener(new OnClick());
        btnCancelRide.setOnClickListener(new OnClick());
        btnCancelTrip.setOnClickListener(new OnClick());
        btnCall.setOnClickListener(new OnClick());
        btnPayNow.setOnClickListener(new OnClick());
        btnPaymentDoneBtn.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());

        frmDestination.setOnClickListener(new OnClick());
        lblPaymentChange.setOnClickListener(new OnClick());
        frmSource.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        mapfocus.setOnClickListener(new OnClick());
        imgSchedule.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        scheduleBtn.setOnClickListener(new OnClick());
        scheduleDate.setOnClickListener(new OnClick());
        scheduleTime.setOnClickListener(new OnClick());
        imgProvider.setOnClickListener(new OnClick());
        imgProviderRate.setOnClickListener(new OnClick());
        img_call_phone.setOnClickListener(new OnClick());
        img_viber.setOnClickListener(new OnClick());
        img_whatsapp.setOnClickListener(new OnClick());

        imgSos.setOnClickListener(new OnClick());
        imgShareRide.setOnClickListener(new OnClick());
        arabicButton = rootView.findViewById(R.id.arabicButton);
        arabicButton.setOnClickListener(new OnClick());
        englishButton = rootView.findViewById(R.id.englishButton);

        englishButton.setOnClickListener(new OnClick());
        kurdishButton = rootView.findViewById(R.id.kurdishButton);
        kurdishButton.setOnClickListener(new OnClick());
        currentTemperatureField = rootView.findViewById(R.id.currentTemperatureField);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnCall.setVisibility(View.GONE);
            }
        });
        String language = LocaleManager.getLanguage(getActivity());
        if (language.equalsIgnoreCase("ar"))
            arabicButton.setTextColor(Color.YELLOW);
        else if (language.equalsIgnoreCase("en"))
            englishButton.setTextColor(Color.YELLOW);
        else if (language.equalsIgnoreCase("ku"))
            kurdishButton.setTextColor(Color.YELLOW);

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);

        customMarker.view_main_location_selector_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setLocation();
                utils.print("customMarker", "" + cmPosition.target.latitude);


            }
        });
        flowValue = 1;
        layoutChanges();

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    utils.print("", "Back key pressed!");

                    if (strPickType.equals("source") && !placePredictions.strSourceLongitude.equals("") ||
                            (strPickType.equals("destination") && dest_lat.equals("") && dest_lng.equals(""))) {

                        mapClear();
                        source_address = "";
                        source_lat = "";
                        source_lng = "";
                        placePredictions.strSourceAddress = "";
                        placePredictions.strSourceLatitude = "";
                        placePredictions.strSourceLongitude = "";
                        placePredictions.strSourceLatLng = "";
                        customMarker.view_main_location_selector_ib.setImageResource(R.drawable.ic_origin_unselected_marker);
                        strPickType = "source";

                    } else if (strPickType.equals("destination") && !dest_lat.equals("") && !dest_lng.equals("")) {

                        mapClear();
                        rtlStaticMarker.setVisibility(View.VISIBLE);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude)))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin_selected_marker));

                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.scrollBy(0,-300));
                        strPickType = "destination";
                        customMarker.view_main_location_selector_ib.setImageResource(R.drawable.ic_dest_unselected_marker);

                        return true;
                    } else if (lnrRequestProviders.getVisibility() == View.VISIBLE) {

                        getActivity().finish();
                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        flowValue = 1;
                    } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                        //flowValue = 2;
                        btnCancelRide.performClick();
                        return true;
                    } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                        flowValue = 2;
                    } else {
                        getActivity().finish();
                    }
                    layoutChanges();
                    return true;
                }
                return false;
            }
        });


    }

    @SuppressWarnings("MissingPermission")
    void initMap() {

        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }

        if (mMap != null) {
            setupMap();
        }

    }

    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setBuildingsEnabled(true);
            //  mMap.getUiSettings().setCompassEnabled(false);
            //  mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setOnCameraChangeListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveListener(this);

        }

    }


    @Override
    public void onLocationChanged(Location location) {

        if (marker != null) {
            marker.remove();
        }

        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);



            location.bearingTo(location);

            Log.e("MAP", "onLocationChanged: 1 " + location.getLatitude());
            Log.e("MAP", "onLocationChanged: 2 " + location.getLongitude());
            current_lat = "" + location.getLatitude();
            current_lng = "" + location.getLongitude();
            if (value == 0) {
                 myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setPadding(0, 0, 0, 0);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
                source_lat = "" + latitude;
                source_lng = "" + longitude;
                source_address = currentAddress;
                current_address = currentAddress;
                source.setText(currentAddress);
                value++;
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                flowValue = 1;
                if (lnrRequestProviders.getVisibility() != View.VISIBLE) {
                    layoutChanges();
                }
                getServiceList();

                taskLoadUp("");
            }
            //Testing
            //currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
            if (flowValue == 1)
                getProvidersList("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handleCheckStatus == null) {
            handleCheckStatus = new Handler();

            //check status every 3 sec
            handleCheckStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (helper.isConnectingToInternet()) {
                        if (!isAdded()) {
                            return;
                        }
                        utils.print("Handler", "Called");
                        checkStatus();
                        if (alert != null && alert.isShowing()) {
                            alert.dismiss();
                            alert = null;
                        }
                    } else {
                        showDialog();
                    }
                    handleCheckStatus.postDelayed(this, 3000);
                }
            }, 3000);
        }

        if (mapFragment != null && mMap != null)
            mapFragment.onResume();

        PreviousStatus = "";
        count = 0;

    }


    @Override
    public void onPause() {
        super.onPause();
        if (handleCheckStatus != null) {
            handleCheckStatus.removeCallbacksAndMessages(null);
            handleCheckStatus = null;
        }

        if ((customDialog != null) && customDialog.isShowing())
            customDialog.dismiss();
        customDialog = null;

        if (mapFragment != null && mMap != null)
            mapFragment.onPause();
    }


    private void setLanguage() {
        if (getActivity() != null) {
            String languageCode = SharedHelper.getKey(getActivity(), "lang");
            Log.e(TAG, "setLanguage: " + languageCode);
            if (!languageCode.equalsIgnoreCase(LocaleManager.getLanguage(getActivity()))) {
                LocaleManager.setLocale(getActivity(), languageCode);
                getActivity().recreate();
            }
        }

    }

    private void showreasonDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        final AlertDialog dialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancalReason = reasonEtxt.getText().toString();
                cancelRequest();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void openWhatsappContact(String number) {

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        String waNumber = number.replace("+", "");
        sendIntent.putExtra("jid", waNumber + "@s.whatsapp.net");
        startActivity(sendIntent);


    }

    public void callViber(String phone) {

        Uri uri = Uri.parse("tel:" + Uri.encode(phone));
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
        intent.setData(uri);
        getContext().startActivity(intent);

    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void restartActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getActivity().finish();
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "-" + "Mr/Mrs." + name + " would like to share a ride with you at " +
                    shareUrl + current_lat + "," + current_lng);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intentCall);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sosRequest() {

    }

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showreasonDialog();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void layoutChanges() {
        try {
            utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());
            if (lnrApproximate.getVisibility() == View.VISIBLE) {
                lnrApproximate.startAnimation(slide_down);
            } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                ScheduleLayout.startAnimation(slide_down);
            } else if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                lnrRequestProviders.startAnimation(slide_down);
            } else if (lnrProviderPopup.getVisibility() == View.VISIBLE) {
                lnrProviderPopup.startAnimation(slide_down);
                lnrSearchAnimation.startAnimation(slide_up_down);
                lnrSearchAnimation.setVisibility(View.VISIBLE);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            }
            lnrRequestProviders.setVisibility(View.GONE);
            lnrProviderPopup.setVisibility(View.GONE);
            lnrApproximate.setVisibility(View.GONE);
            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            ScheduleLayout.setVisibility(View.GONE);
            frmSource.setVisibility(View.GONE);
            frmDestination.setVisibility(View.GONE);
            imgMenu.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            shadowBack.setVisibility(View.GONE);
            txtCommentsRate.setText("");
            scheduleDate.setText("" + context.getString(R.string.sample_date));
            scheduleTime.setText("" + context.getString(R.string.sample_time));

            if (flowValue == 0) {
                if (imgMenu.getVisibility() == View.GONE) {
                    if (mMap != null) {
                        mMap.clear();
                        setupMap();
                    }
                }
                frmDestination.setVisibility(View.VISIBLE);
                frmSource.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                destination.setText("");
                source.setText("" + current_address);
                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
                sourceAndDestinationLayout.setVisibility(View.VISIBLE);

            } else if (flowValue == 1) {
                if (mMap != null) {
                    mMap.clear();
                    setupMap();
                }
                //frmSource.setVisibility(View.VISIBLE);
                if (placePredictions != null) {
                    if (placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                        source_lat = "" + current_lat;
                        source_lng = "" + current_lng;
                        source.setText("" + current_address);
                    } else {
                        source_lat = "" + placePredictions.strSourceLatitude;
                        source_lng = "" + placePredictions.strSourceLongitude;
                        source_address = "" + placePredictions.strSourceAddress;
                        source.setText("" + placePredictions.strSourceAddress);
                        if (strPickLocation.equalsIgnoreCase("yes")) {
                          //  strPickLocation = "";
                          //  strPickType = "";
                            destination.setText("" + dest_address);
                        }
                    }
                } else {
                    if (strPickLocation.equalsIgnoreCase("yes")) {
                        strPickLocation = "";
                      //  strPickType = "";
                        destination.setText("" + dest_address);
                    } else {
                        source_lat = "" + current_lat;
                        source_lng = "" + current_lng;
                        source.setText("" + current_address);
                    }
                }
                if (parserTask != null) {
                    parserTask = null;
                }

                frmDestination.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.VISIBLE);
                lnrRequestProviders.startAnimation(slide_up);
                lnrRequestProviders.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }
            } else if (flowValue == 2) {
                imgBack.setVisibility(View.VISIBLE);
                lnrApproximate.startAnimation(slide_up);
                lnrApproximate.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 3) {
                imgBack.setVisibility(View.VISIBLE);
                lnrWaitingForProviders.setVisibility(View.VISIBLE);
                //sourceAndDestinationLayout.setVisibility(View.GONE);
            } else if (flowValue == 4) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrProviderAccepted.startAnimation(slide_up_down);
                lnrProviderAccepted.setVisibility(View.VISIBLE);
            } else if (flowValue == 5) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
            } else if (flowValue == 6) {
                //stopGetLiveDriverLocation();
                imgMenu.setVisibility(View.VISIBLE);
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(1.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
            } else if (flowValue == 7) {
                imgBack.setVisibility(View.VISIBLE);
                ScheduleLayout.startAnimation(slide_up);
                ScheduleLayout.setVisibility(View.VISIBLE);
            } else if (flowValue == 8) {
                // clear all views
                shadowBack.setVisibility(View.GONE);
            } else if (flowValue == 9) {

                lnrRequestProviders.setVisibility(View.VISIBLE);
                frmDestination.setVisibility(View.VISIBLE);

                if (!placePredictions.strSourceLatLng.equals("")) {

                    LatLng latLng = new LatLng(new Double(placePredictions.strSourceLatitude), new Double(placePredictions.strSourceLongitude));

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin_selected_marker));

                    mMap.addMarker(markerOptions);

                    if (rtlStaticMarker.getVisibility() == View.GONE){
                        rtlStaticMarker.setVisibility(View.VISIBLE);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    }

                    mMap.moveCamera(CameraUpdateFactory.scrollBy(0,-300));
                    customMarker.view_main_location_selector_ib.setImageResource(R.drawable.ic_dest_unselected_marker);
                    shadowBack.setVisibility(View.GONE);

                } else {

                    if (rtlStaticMarker.getVisibility() == View.GONE){
                        rtlStaticMarker.setVisibility(View.VISIBLE);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    }

                    customMarker.view_main_location_selector_ib.setImageResource(R.drawable.ic_origin_unselected_marker);
                    mMap.moveCamera(CameraUpdateFactory.scrollBy(0,0));
                    shadowBack.setVisibility(View.GONE);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_map));

            if (!success) {
                utils.print("Map:Style", "Style parsing failed.");
            } else {
                utils.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            utils.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;
        mMap.setTrafficEnabled(true);

        setupMap();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        cmPosition = cameraPosition;
        if (lstProviderMarkers.size() > 0) {
            lstProviderMarkers.get(0).setVisible(cameraPosition.zoom > 7);
        }
        if (strPickLocation.equalsIgnoreCase("yes")) {
            cmPosition = cameraPosition;
            if (pick_first) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cmPosition.target.latitude,
                        cmPosition.target.longitude), 16.0f));
                pick_first = false;
            }
        }
    }


    public void sendRequest(String flow) {

        btnRequestRides.setEnabled(false);
        flowStatus = flow;
        customDialog = new CustomDialog(getContext());
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        final JSONObject object = new JSONObject();
        try {
            object.put("s_latitude", source_lat);
            object.put("s_longitude", source_lng);
            object.put("d_latitude", dest_lat);
            object.put("d_longitude", dest_lng);
            object.put("s_address", source_address);
            object.put("d_address", dest_address);
            object.put("service_type", SharedHelper.getKey(context, "service_type"));
            object.put("distance", SharedHelper.getKey(context, "distance"));
            object.put("flow", flow);
            object.put("ttime", ttime.getText().toString());
            object.put("city", SharedHelper.getKey(context, "city"));
            object.put("schedule_date", scheduledDate);
            object.put("schedule_time", scheduledTime);

            Log.e("Schedule Request", "sendRequest: " + object);

            if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
            } else {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                object.put("card_id", SharedHelper.getKey(context, "card_id"));
            }
            utils.print("SendRequestInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle params = new Bundle();
        params.putString("sendRequest", object.toString());
        mFirebaseAnalytics.logEvent("sendRequest", params);

        G.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SEND_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                btnRequestRides.setEnabled(true);
                if (response != null) {

                    utils.print("SendRequestResponse", response.toString());
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    if (response.optString("request_id", "").equals("")) {
                        if (isAdded())
                            utils.displayMessage(getView(), response.optString("message"));
                        Bundle params = new Bundle();
                        params.putString("SendRequestResponse", response.toString());
                        mFirebaseAnalytics.logEvent("SendRequestResponse", params);
                    } else {
                        SharedHelper.putKey(context, "current_status", "");
                        SharedHelper.putKey(context, "request_id", "" + response.optString("request_id"));
                        scheduleTrip = !scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase("");
                        flowValue = 3;
                        layoutChanges();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                btnRequestRides.setEnabled(true);

                errorHandler(error, "sendRequest", object.toString(),URLHelper.SEND_REQUEST_API);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    initMap();
                    MapsInitializer.initialize(getActivity());
                } /*else {
                    showPermissionReqDialog();
                }*/
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionReqDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setMessage("Taxialaan needs the location permission, Please accept to use location functionality")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        // requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), HomeFragment.TAG);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(callGPSSettingIntent);
                            }
                        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST) {
            if (parserTask != null) {
                parserTask = null;
            }
            if (resultCode == Activity.RESULT_OK) {
                if (marker != null) {
                    marker.remove();
                }
                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                strPickLocation = data.getExtras().getString("pick_location");
                strPickType = data.getExtras().getString("type");
                if (strPickLocation.equalsIgnoreCase("yes")) {
                    pick_first = true;
                    mMap.clear();
                    flowValue = 9;
                    layoutChanges();

                    float zoomLevel = 16.0f; //This goes up to 21
                    //stopAnim();
                } else {
                    if (placePredictions != null) {
                        if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                            source_lat = "" + placePredictions.strSourceLatitude;
                            source_lng = "" + placePredictions.strSourceLongitude;
                            source_address = placePredictions.strSourceAddress;
                            if (!placePredictions.strSourceLatitude.equalsIgnoreCase("") && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) {
                                double latitude = Double.parseDouble(placePredictions.strSourceLatitude);
                                double longitude = Double.parseDouble(placePredictions.strSourceLongitude);

                                LatLng location = new LatLng(latitude, longitude);

                                mMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin_selected_marker));

                                marker = mMap.addMarker(markerOptions);
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }

                        }
                        if (!placePredictions.strDestAddress.equalsIgnoreCase("")) {
                            if (placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                                source_lat = "" + current_lat;
                                source_lng = "" + current_lng;
                                source_address = "" + current_address;
                            }
                            dest_lat = "" + placePredictions.strDestLatitude;
                            dest_lng = "" + placePredictions.strDestLongitude;
                            dest_address = placePredictions.strDestAddress;
                            destination.setText(dest_address);
                            SharedHelper.putKey(context, "current_status", "2");
                            rtlStaticMarker.setVisibility(View.GONE);

                        }

                        if (dest_address.equalsIgnoreCase("")) {
                            flowValue = 1;
                            source.setText(source_address);
                            getServiceList();
                        } else {
                            flowValue = 1;
                            if (cardInfoArrayList.size() > 0) {
                                getCardDetailsForPayment(cardInfoArrayList.get(0));
                            }
                            getServiceList();
                        }
                        layoutChanges();
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }
        if (requestCode == REQUEST_LOCATION) {
            Log.e("GPS Result Status", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status", "onActivityResult: " + data);
        } else {
            Log.e("GPS Result Status else", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status else", "onActivityResult: " + data);
        }
    }

    void showProviderPopup(JSONObject jsonObject) {
        lnrSearchAnimation.startAnimation(slide_up_top);
        lnrSearchAnimation.setVisibility(View.GONE);
        lnrProviderPopup.setVisibility(View.VISIBLE);
        lnrRequestProviders.setVisibility(View.GONE);

        Glide.with(activity).load(jsonObject.optString("image")).placeholder(R.drawable.pickup_drop_icon).dontAnimate()
                .error(R.drawable.pickup_drop_icon).into(imgProviderPopup);

        lnrPriceBase.setVisibility(View.GONE);
        lnrPricemin.setVisibility(View.GONE);
        lnrPricekm.setVisibility(View.GONE);

        if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("HOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")) {
                lblCalculationType.setText("Minutes");
            } else {
                lblCalculationType.setText("Hours");
            }
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCE")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            lblCalculationType.setText(activity.getResources().getString(R.string.distance));
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEHOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")) {
                lblCalculationType.setText("Distance and Minutes");
            } else {
                lblCalculationType.setText("Distance and Hours");
            }
        }

        if (!jsonObject.optString("capacity").equalsIgnoreCase("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lblCapacity.setText(jsonObject.optString("capacity"));
            } else {
                lblCapacity.setText(jsonObject.optString("capacity") + " peoples");
            }
        } else {
            lblCapacity.setVisibility(View.GONE);
        }

        lblServiceName.setText("" + jsonObject.optString("name"));
        lblBasePricePopup.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("fixed"));
        lblPriceKm.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("price"));
        lblPriceMin.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("minute"));
        if (jsonObject.optString("description").equalsIgnoreCase("null")) {
            lblProviderDesc.setVisibility(View.GONE);
        } else {
            lblProviderDesc.setVisibility(View.VISIBLE);
            lblProviderDesc.setText("" + jsonObject.optString("description"));
        }
    }

    public void setValuesForApproximateLayout() {
        if (isInternet) {
            String surge = SharedHelper.getKey(context, "surge");
            if (surge.equalsIgnoreCase("1")) {
                surgeDiscount.setVisibility(View.VISIBLE);
                surgeTxt.setVisibility(View.VISIBLE);
                surgeDiscount.setText(SharedHelper.getKey(context, "surge_value"));
            } else {
                surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.GONE);
            }
            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));
            lblEta.setText(SharedHelper.getKey(context, "eta_time"));
            if (!SharedHelper.getKey(context, "name").equalsIgnoreCase("")
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase(null)
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase("null")) {
                lblType.setText(SharedHelper.getKey(context, "name"));
            } else {
                lblType.setText("" + "Sedan");
            }

            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
        }
    }

    private void getCards() {
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> response) {
                        // response contains both the headers and the string result
                        try {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray.length() > 0) {
                                        CardInfo cardInfo = new CardInfo();
                                        cardInfo.setCardId("CASH");
                                        cardInfo.setCardType("CASH");
                                        cardInfo.setLastFour("CASH");
                                        cardInfoArrayList.add(cardInfo);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject cardObj = jsonArray.getJSONObject(i);
                                            cardInfo = new CardInfo();
                                            cardInfo.setCardId(cardObj.optString("card_id"));
                                            cardInfo.setCardType(cardObj.optString("brand"));
                                            cardInfo.setLastFour(cardObj.optString("last_four"));
                                            cardInfoArrayList.add(cardInfo);
                                        }
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            CardInfo cardInfo = new CardInfo();
                            cardInfo.setCardId("CASH");
                            cardInfo.setCardType("CASH");
                            cardInfo.setLastFour("CASH");
                            cardInfoArrayList.add(cardInfo);
                        }
                    }
                });

    }

    public void getServiceList() {
        if (getContext() != null) {
            customDialog = new CustomDialog(getContext());
            customDialog.setCancelable(false);
            if (customDialog != null && !customDialog.isShowing())
                customDialog.show();
        }


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetServices", response.toString());

                SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
                SharedHelper.putKey(context, "name", response.optJSONObject(0).optString("name"));

                if (customDialog != null && customDialog.isShowing())
                    customDialog.dismiss();

                if (response.length() > 0) {
                    currentPostion = 0;
                    ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                    rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    rcvServiceTypes.setAdapter(serviceListAdapter);
                    getProvidersList(SharedHelper.getKey(context, "service_type"));
                } else {
                    if (isAdded())
                        utils.displayMessage(getView(), getString(R.string.no_service));
                }
                mMap.clear();
                if (!destination.getText().toString().equalsIgnoreCase("")) {
                    setValuesForSourceAndDestination();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if ((customDialog != null) && customDialog.isShowing()) {
                    customDialog.dismiss();
                }

                errorHandler(error, "getServiceList", SharedHelper.getKey(context, "token_type") + " "
                        + SharedHelper.getKey(context, "access_token"),URLHelper.GET_SERVICE_LIST_API);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                        + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    public void getApproximateFare() {
        customDialog = new CustomDialog(getContext());
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        final String constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                "?s_latitude=" + source_lat
                + "&s_longitude=" + source_lng
                + "&d_latitude=" + dest_lat
                + "&d_longitude=" + dest_lng
                + "&service_type=" + SharedHelper.getKey(context, "service_type");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                        utils.print("ApproximateResponse", response.toString());
                        SharedHelper.putKey(context, "estimated_fare", response.optString("estimated_fare"));
                        SharedHelper.putKey(context, "distance", response.optString("distance"));
                        SharedHelper.putKey(context, "eta_time", response.optString("time"));
                        SharedHelper.putKey(context, "surge", response.optString("surge"));
                        SharedHelper.putKey(context, "surge_value", response.optString("surge_value"));
                        setValuesForApproximateLayout();
                        double wallet_balance = response.optDouble("wallet_balance");
                        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                            lineView.setVisibility(View.VISIBLE);
                            //  chkWallet.setVisibility(View.VISIBLE);
                            //  chkServiceWallet.setVisibility(View.VISIBLE);
                        } else {
                            lineView.setVisibility(View.GONE);
                            //  chkWallet.setVisibility(View.GONE);
                            //   chkServiceWallet.setVisibility(View.GONE);
                        }
                        flowValue = 2;
                        layoutChanges();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                errorHandler(error, "getApproximateFare",
                        SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"),constructedURL);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    void getProvidersList(String strTag) {

        final String providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                "latitude=" + current_lat +
                "&longitude=" + current_lng +
                "&service=" + strTag;

        utils.print("Get all providers", "" + providers_request);
        utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));

        for (int i = 0; i < lstProviderMarkers.size(); i++) {
            lstProviderMarkers.get(i).remove();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(providers_request, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetProvidersList", response.toString());
//                customDialog.dismiss();
//                mMap.clear();
//
//                setValuesForSourceAndDestination();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (response != null && response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject jsonObj = response.getJSONObject(i);
                            if (!jsonObj.getString("latitude").equalsIgnoreCase("")
                                    && !jsonObj.getString("longitude").equalsIgnoreCase("")) {

                                Double proLat = Double.parseDouble(jsonObj.getString("latitude"));
                                Double proLng = Double.parseDouble(jsonObj.getString("longitude"));


                                Float rotation = 0.0f;

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(proLat, proLng))
                                        .rotation(rotation)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

//                        availableProviders = mMap.addMarker(markerOptions);
                                lstProviderMarkers.add(mMap.addMarker(markerOptions));

                                builder.include(new LatLng(proLat, proLng));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    CameraUpdate cu = null;
                    LatLngBounds bounds = builder.build();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                errorHandler(error, "PAST_TRIPS", SharedHelper.getKey(context, "access_token"),providers_request);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    private String getDirectionsUrl(LatLng sourceLatLng, LatLng destLatLng) {

        // Origin of routelng;
        String str_origin = "origin=" + sourceLatLng.latitude + "," + sourceLatLng.longitude;
        String str_dest = "destination=" + destLatLng.latitude + "," + destLatLng.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String key = getString(R.string.google_map_api);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        utils.print("url", url);
        return url;

    }


    public void cancelRequest() {
        customDialog = new CustomDialog(getContext());
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Bundle params = new Bundle();
        params.putString("mobile", SharedHelper.getKey(context, "mobile"));
        params.putString("ss_latitude", source_lat + "--" + source_lng);
        params.putString("dd_latitude", "" + dest_lat + "--" + dest_lng);
        params.putString("cancelRequest", object.toString());
        mFirebaseAnalytics.logEvent("cancelRequest", params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                Toast.makeText(context, getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                if (!flowStatus.equalsIgnoreCase("") && !flowStatus.equalsIgnoreCase("OPTIONAL"))
                    mapClear();
                else {
                    mapClear();
                    value = 0;
                }


                SharedHelper.putKey(context, "request_id", "");
                flowValue = 1;
                PreviousStatus = "";
                layoutChanges();
                setupMap();
                getProvidersList("");
                getServiceList();
                showMarkerSelectRoat();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                errorHandler(error, "cancelRequest", params.toString(), URLHelper.CANCEL_REQUEST_API);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void setValuesForSourceAndDestination() {
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
                    source.setText(source_address);
                } else {
                    source.setText(current_address);
                }
            } else {
                source.setText(current_address);
            }

            if (!dest_lat.equalsIgnoreCase("")) {
                destination.setText(dest_address);
            }


            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }

            if (sourceLatLng != null && destLatLng != null) {
                utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
                String url = getDirectionsUrl(sourceLatLng, destLatLng);
                if (destLatLng.latitude != 0.0 && destLatLng.longitude != 0.0) {
                    DownloadTask downloadTask = new DownloadTask();
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }

        }
    }


    private void checkStatus() {
        try {

            if (isInternet) {

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.REQUEST_STATUS_CHECK_API, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (ln_internet.isShown()){
                            ln_internet.setVisibility(View.GONE);
                        }
                        utils.print("Response", "" + response.toString());

                        if (response.optJSONArray("data") == null || response.optJSONArray("data").length() == 0) {
                            stopCountDownDriver();
                        }
                        if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                            utils.print("response", "not null");
                            try {
                                JSONArray requestStatusCheck = response.optJSONArray("data");
                                JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                flowStatus = requestStatusCheckObject.optString("flow");
                                wallet_balance = requestStatusCheckObject.optJSONObject("user").optDouble("wallet_balance");
                                waypoints = requestStatusCheckObject.optJSONArray("waypoints");

                                //Driver Detail
                                if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                    driver = new Driver();
                                    driver.setFname(requestStatusCheckObject.optJSONObject("provider").optString("first_name"));
                                    driver.setLname(requestStatusCheckObject.optJSONObject("provider").optString("last_name"));
                                    driver.setEmail(requestStatusCheckObject.optJSONObject("provider").optString("email"));
                                    driver.setMobile(requestStatusCheckObject.optJSONObject("provider").optString("mobile"));
                                    driver.setImg(requestStatusCheckObject.optJSONObject("provider").optString("avatar"));
                                    driver.setRating(requestStatusCheckObject.optJSONObject("provider").optString("rating"));
                                }
                                String status = requestStatusCheckObject.optString("status");
                                String wallet = requestStatusCheckObject.optString("use_wallet");
                                source_lat = requestStatusCheckObject.optString("s_latitude");
                                source_lng = requestStatusCheckObject.optString("s_longitude");
                                dest_lat = requestStatusCheckObject.optString("d_latitude");
                                dest_lng = requestStatusCheckObject.optString("d_longitude");

                                s_lat_user = requestStatusCheckObject.optDouble("s_latitude");
                                s_lot_user = requestStatusCheckObject.optDouble("s_longitude");

                                if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                    LatLng myLocation = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                }


                                // surge price
                                if (requestStatusCheckObject.optString("surge").equalsIgnoreCase("1")) {
                                    lblSurgePrice.setVisibility(View.VISIBLE);
                                } else {
                                    lblSurgePrice.setVisibility(View.GONE);
                                }

                                utils.print("PreviousStatus", "" + PreviousStatus);
                                //by ibrahim
                                if (status.equals("STARTED"))
                                {
                                    Log.d("ibrahim","STARTEDDDD");


                                    JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                    Log.d("ibrahim",provider.getString("estimate_time"));

                                    lblestimated.setText(provider.getString("estimate_time") + " ");
//                                                long timeToHere = provider.getLong("estimate_time");
                                    lblestimated.setVisibility(View.VISIBLE);
                                    lblestimated.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                    lblestimated.setTypeface(lblestimated.getTypeface(), Typeface.BOLD);


                                }

                                if (!PreviousStatus.equals(status)) {
                                    mMap.clear();
                                    PreviousStatus = status;
                                    rtlStaticMarker.setVisibility(View.GONE);
                                    if (!status.equals("STARTED")) {
                                        stopCountDownDriver();
                                    }
                                    flowValue = 8;
                                    layoutChanges();
                                    SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                                   if (!status.equals("STARTED"))
                                        reCreateMap();
                                    utils.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                    switch (status) {

                                        case "SEARCHING":
                                            show(lnrWaitingForProviders);
                                            strTag = "search_completed";
                                            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                                LatLng myLocation1 = new LatLng(Double.parseDouble(source_lat),
                                                        Double.parseDouble(source_lng));
                                                CameraPosition cameraPosition1 = new CameraPosition.Builder().target(myLocation1).zoom(16).build();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                                            }
                                            break;
                                        case "CANCELLED":
                                            strTag = "";
                                            imgSos.setVisibility(View.GONE);
                                            break;
                                        case "ACCEPTED":
                                            strTag = "ride_accepted";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                show(lnrProviderAccepted);
                                                flowValue = 9;
                                                layoutChanges();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "STARTED":
                                            strTag = "ride_started";
                                            try {

                                                count++;
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " "
                                                        + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))

                                                    Picasso.with(context).load(provider.optString("avatar"))
                                                            .placeholder(R.drawable.ic_dummy_user)
                                                            .error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") +
                                                        "\n" + provider_service.optString("service_number"));

                                                Picasso.with(context).load(service_type.optString("image"))
                                                        .placeholder(R.drawable.car_select)
                                                        .error(R.drawable.car_select).into(imgServiceRequested);

                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                              ////  String first = getString(R.string.estimated_first);
                                              //  String second = getString(R.string.estimated_secode);

                                                 lblestimated.setText(provider.getString("estimate_time") + " ");
                                                long timeToHere = provider.getLong("estimate_time");
                                                lblestimated.setVisibility(View.VISIBLE);
                                                lblestimated.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                                lblestimated.setTypeface(lblestimated.getTypeface(), Typeface.BOLD);
                                                //startCountDownDriver(timeToHere * 60000);
                                                flowValue = 4;
                                                Log.d("ibrahim",provider.getString("estimate_time"));
                                                mapClear();


                                                layoutChanges();
                                                if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                                                    SharedHelper.putKey(context, "current_status", "");
                                                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                                                    intent.putExtra("tag", "upcoming");
                                                    startActivity(intent);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "ARRIVED":
                                            once = true;
                                            strTag = "ride_arrived";
                                            utils.print("MyTest", "ARRIVED");
                                            try {
                                                utils.print("MyTest", "ARRIVED TRY");
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                lblStatus.setText(getString(R.string.arrived));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                lblestimated.setVisibility(View.VISIBLE);
                                                lblestimated.setText(R.string.the_caption_arrived);
                                                lblestimated.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                lblestimated.setTypeface(lblestimated.getTypeface(), Typeface.BOLD);
                                                flowValue = 4;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                utils.print("MyTest", "ARRIVED CATCH");
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "PICKEDUP":
                                            once = true;
                                            strTag = "ride_picked";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.with(context).load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                imgSos.setVisibility(View.VISIBLE);
                                                //imgShareRide.setVisibility(View.VISIBLE);
                                                lblStatus.setText(getString(R.string.picked_up));
                                                btnCancelTrip.setText(getString(R.string.share));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                lblestimated.setVisibility(View.VISIBLE);
                                                lblestimated.setText(R.string.enjoy_your_trip);
                                                lblestimated.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                lblestimated.setTypeface(lblestimated.getTypeface(), Typeface.BOLD);
                                                flowValue = 4;
                                                layoutChanges();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "DROPPED":
                                            once = true;
                                            strTag = "";
                                            imgSos.setVisibility(View.VISIBLE);
                                            //imgShareRide.setVisibility(View.VISIBLE);
                                            try {
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("tax"));
                                                    lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("discount"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("distance"));
                                                    //lblCommision.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("commision"));
                                                    lblTotalPrice.setText("" + payment.optString("total"));
                                                    txt04Copun.setText("" + payment.optString("discount"));
                                                    txt04Trip.setText("" + payment.optString("trip_price"));
                                                    totalRideAmount = payment.optInt("total");
                                                    walletAmountDetected = payment.optInt("wallet");
                                                    couponAmountDetected = payment.optInt("discount");
                                                }
                                                if (requestStatusCheckObject.optString("booking_id") != null &&
                                                        !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                    booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                } else {
                                                    booking_id.setVisibility(View.GONE);
                                                }
                                                if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 &&
                                                        totalRideAmount == 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                        totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                        totalRideAmount == 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected == 0 &&
                                                        totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.VISIBLE);
                                                    discountDetectionLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.card));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    btnPaymentDoneBtn.setVisibility(View.GONE);
                                                    walletDetectionLayout.setVisibility(View.GONE);
                                                    discountDetectionLayout.setVisibility(View.GONE);
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(getActivity().getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {

                                                    btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                        && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                }
                                                lblestimated.setVisibility(View.GONE);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "COMPLETED":
                                            strTag = "";
                                            try {
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("tax"));
                                                    lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("discount"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("distance"));
                                                    lblTotalPrice.setText("" + payment.optString("total"));
                                                    txt04Copun.setText("" + payment.optString("discount"));
                                                    txt04Trip.setText("" + payment.optString("trip_price"));
                                                    totalRideAmount = payment.optInt("total");
                                                    walletAmountDetected = payment.optInt("wallet");
                                                    couponAmountDetected = payment.optInt("discount");
                                                }
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                isPaid = requestStatusCheckObject.optString("paid");
                                                paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                imgSos.setVisibility(View.GONE);
                                                //imgShareRide.setVisibility(View.GONE);
                                                // lblCommision.setText(payment.optString("commision"));
                                                if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    btnPayNow.setVisibility(View.GONE);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money1);
                                                    lblPaymentTypeInvoice.setText(activity.getResources().getString(R.string.cash));
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                    flowValue = 5;
                                                    layoutChanges();
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText(activity.getResources().getString(R.string.card));
                                                    btnPayNow.setVisibility(View.GONE);
                                                } else if (isPaid.equalsIgnoreCase("1")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    lblProviderNameRate.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.with(context).load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    else
                                                        Picasso.with(context).load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    flowValue = 6;
                                                    layoutChanges();
                                                    //imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    // lblPaymentTypeInvoice.setText("CARD");

                                                    //need to delete xml view
                                                    imgProviderRate.setVisibility(View.GONE);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                }

                               liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                            requestStatusCheckObject.getJSONObject("provider").optString("longitude"));

                            } catch (Exception e) {
                                e.printStackTrace();
                                if (isAdded())
                                    utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                        } else if (PreviousStatus.equalsIgnoreCase("SEARCHING")) {
                            SharedHelper.putKey(context, "current_status", "");
                            if (scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                    && !scheduledTime.equalsIgnoreCase("")) {
                               /* Toast.makeText(context, getString(R.string.schdule_accept), Toast.LENGTH_SHORT).show();
                                if (scheduleTrip){
                                    Intent intent = new Intent(activity,HistoryActivity.class);
                                    intent.putExtra("tag","upcoming");
                                    startActivity(intent);
                                }*/
                            } else
                                Toast.makeText(context, getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            value = 0;
                            flowValue = 1;
                            layoutChanges();
                            mMap.clear();
                            mapClear();
                            showMarkerSelectRoat();


                        } else if (PreviousStatus.equalsIgnoreCase("STARTED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            value = 0;
                            flowValue = 1;
                            layoutChanges();
                            mMap.clear();
                            mapClear();
                            showMarkerSelectRoat();
                        } else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            value = 0;
                            flowValue = 1;
                            layoutChanges();
                            mMap.clear();
                            mapClear();
                            showMarkerSelectRoat();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());
                        errorHandler(error, "checkStatus",
                                SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"),
                                URLHelper.REQUEST_STATUS_CHECK_API);

                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        utils.print("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));

                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

                G.getInstance().addToRequestQueue(jsonObjectRequest);

            } else {

                errorHandler(new TimeoutError(), "checkStatus",
                        SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"),
                        URLHelper.REQUEST_STATUS_CHECK_API);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMarkerSelectRoat(){

        rtlStaticMarker.setVisibility(View.VISIBLE);
        customMarker.view_main_location_selector_ib.setImageResource(R.drawable.ic_origin_unselected_marker);

        strPickType = "source";
        strPickLocation = "";
        placePredictions.strSourceAddress = "";
        placePredictions.strSourceLatitude = "";
        placePredictions.strSourceLongitude = "";
        placePredictions.strSourceLatLng = "";
        count = 0;

    }

    private void gotoAddCard() {
        Intent mainIntent = new Intent(activity, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    private void showChooser() {

        String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            if (cardInfoArrayList.get(i).getLastFour().equals("CASH")) {
                cardsList[i] = "CASH";
            } else {
                cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
            }
        }

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builderSingle.setTitle(getString(R.string.choose_payment));
        builderSingle.setSingleChoiceItems(cardsList, 0, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.custom_tv);
        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card;
            if (cardInfoArrayList.get(j).getLastFour().equals("CASH")) {
                card = "CASH";
            } else {
                card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            }
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                utils.print("Items clicked===>", "" + selectedPosition);
                getCardDetailsForPayment(cardInfoArrayList.get(selectedPosition));
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//        builderSingle.setAdapter(
//                arrayAdapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        getCardDetailsForPayment(cardInfoArrayList.get(which));
//                        dialog.dismiss();
//                    }
//                });
        builderSingle.show();
    }

    private void getCardDetailsForPayment(CardInfo cardInfo) {

        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText(getActivity().getResources().getString(R.string.cash));
        } else {
            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.drawable.visa);
            lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
        }
    }

    public void payNow() {

        customDialog = new CustomDialog(getContext());
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        final JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("PayNowRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                flowValue = 6;
                // update wallet balance

                layoutChanges();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                errorHandler(error, "payNow", object.toString(),URLHelper.PAY_NOW_API);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public synchronized void liveNavigation(String status, String lat, String lng) {

        synchronized (object) {

           // Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);

         //   utils.print("timeMOVE",timerMoveMap+""+"****"+System.currentTimeMillis()/1000);


            if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {
                Double proLat = Double.parseDouble(lat);
                Double proLng = Double.parseDouble(lng);
                if (proLat == 0 || proLng == 0) {
                    return;
                }

              //  Toast.makeText(activity, proLat +"--"+proLng, Toast.LENGTH_SHORT).show();
              //  float rotation = 0.0f;

              //  View marker = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
                //  tv_marker_text = marker.findViewById(R.id.time_txt);
                // String mins = setETA(Double.parseDouble(lat), Double.parseDouble(lng));
                //  tv_marker_text.setText(mins + " Mins");

                float rotate = 0;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(proLat, proLng))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon))
                        .rotation(rotate)
                        .flat(true);


              /*  if ("ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status) || "COMPLETED".equals(status))
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), marker)));*/


                if (!PreviousStatus.equals("STARTED") && flowStatus.equalsIgnoreCase("OPTIONAL") &&  waypoints.length() > 0){

                    mMap.clear();
                    pointss = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    for (int j = 0; j < waypoints.length(); j++) {

                        try {
                            JSONObject jsonObject = waypoints.getJSONObject(j);
                            double latitude1 = Double.parseDouble(jsonObject.getString("latitude"));
                            double longitude1 = Double.parseDouble(jsonObject.getString("longitude"));
                            LatLng position = new LatLng(latitude1, longitude1);
                            pointss.add(position);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    lineOptions.addAll(pointss);
                    lineOptions.width(8);
                    lineOptions.color(Color.BLUE);

                    mMap.getUiSettings().setMapToolbarEnabled(false);

                    // Drawing polyline in the Google Map for the i-th route
                    if (lineOptions == null) {
                        // Toast.makeText(activity, getResources().getString(R.string.no_routes_available), Toast.LENGTH_SHORT).show();
                    } else {
                        mMap.addPolyline(lineOptions);
                    }

                }

                if (count == 1 && PreviousStatus.equals("STARTED")) {

                    sourceLatLng = new LatLng(proLat, proLng);
                    destLatLng = new LatLng(s_lat_user, s_lot_user);

                    String url = getDirectionsUrl(sourceLatLng, destLatLng);
                    if (destLatLng.latitude != 0.0 && destLatLng.longitude != 0.0) {
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);
                    }
                    count++;
                }

                if (providerMarker != null) {

                    double oldLat = providerMarker.getPosition().latitude;
                    double oldLng = providerMarker.getPosition().longitude;
                    double newLat = markerOptions.getPosition().latitude;
                    double newLng = markerOptions.getPosition().longitude;
                    //Find the Bearing from current location to next location
                    float v = calculateBearingAngle(oldLat, oldLng, newLat, newLng);
                    //markerOptions.rotation(r);
                    markerOptions.rotation(v);
                    providerMarker.remove();
                }

                timerMoveMap +=30;
                long timeCurent = System.currentTimeMillis()/1000;

                if (timerMoveMap < timeCurent){
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(proLat, proLng)).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                timerMoveMap -=30;

                providerMarker = mMap.addMarker(markerOptions);
            }
        }
    }

    CountDownTimer countDownTimer;

    private void startCountDownDriver(long time) {
        utils.print("startCountDownDriver", "time : " + time);
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(time, 1000) { // adjust the milli seconds here
                public void onTick(long millisUntilFinished) {

                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    if (lblestimated != null) {
                        lblestimated.setText(String.format("%02d", minutes)
                                + ":" + String.format("%02d", seconds));
                    }

                }

                public void onFinish() {
                    if (lblestimated != null) {
                        lblestimated.setText("00:00");
                    }
                }
            };
            countDownTimer.start();
        }

    }

    private void stopCountDownDriver() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


    private void mapClear() {
        if (parserTask != null)
            parserTask.cancel(true);
        mMap.clear();

        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";

        //Once user cancelled ride
        destination.setText("");
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }
            utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
            String url = getDirectionsUrl(sourceLatLng, destLatLng);
            if (destLatLng.latitude != 0.0 && destLatLng.longitude != 0.0) {
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        }
    }

    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }


    private final Object object = new Object();

    class OnClick implements View.OnClickListener {

        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.arabicButton:
                    SharedHelper.putKey(context, "lang", "ar");
                    setLanguage();
                    restartActivity();
                    break;

                case R.id.englishButton:
                    SharedHelper.putKey(context, "lang", "en");
                    setLanguage();
                    restartActivity();

                    break;
                case R.id.kurdishButton:
                    SharedHelper.putKey(context, "lang", "ku");
                    setLanguage();

                    restartActivity();

                    break;


                case R.id.frmSource:
                    Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent.putExtra("cursor", "source");
                    intent.putExtra("s_address", source.getText().toString());
                    intent.putExtra("d_address", destination.getText().toString());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.frmDestination:

                    dest_address = "";
                    dest_lat = "";
                    dest_lng = "";

                    destination.setText("");
                    Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intentDest.putExtra("cursor", "destination");
                    intentDest.putExtra("s_address", source.getText().toString());
                    intentDest.putExtra("d_address", destination.getText().toString());
                    if (!source_lat.equals("") && !source_lng.equals("")) {

                        intentDest.putExtra("latitude", Double.parseDouble(source_lat));
                        intentDest.putExtra("longitude", Double.parseDouble(source_lng));
                    }
                    startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);


                    break;
                case R.id.lblPaymentChange:
                    if (cardInfoArrayList.size() > 0)
                        showChooser();
                    else
                        gotoAddCard();
                    break;
                case R.id.btnRequestRides:
                    scheduledDate = "";
                    scheduledTime = "";
                    if (!source.getText().toString().equalsIgnoreCase("") &&
                            !destination.getText().toString().equalsIgnoreCase("")) {
                        getApproximateFare();
                    } else if (destination.getText().toString().equalsIgnoreCase("")) {
                        //Toast.makeText(context, "Please enter both pickup and drop locations", Toast.LENGTH_SHORT).show();
                        sendRequest("OPTIONAL");
                    }
                    break;
                case R.id.btnRequestRideConfirm:
                    SharedHelper.putKey(context, "name", "");
                    sendRequest("NORMAL");
                    break;
                case R.id.btnPayNow:
                    payNow();
                    break;
                case R.id.btnPaymentDoneBtn:
                    btnPayNow.setVisibility(View.GONE);
                    btnPaymentDoneBtn.setVisibility(View.GONE);
                    flowValue = 6;
                    layoutChanges();
                    break;
                case R.id.btnSubmitReview:
                    submitReviewCall();
                    break;
                case R.id.lnrHidePopup:
                case R.id.btnDonePopup:

                    lnrHidePopup.setVisibility(View.GONE);
                    lnrProviderPopup.setVisibility(View.GONE);

                    lnrSearchAnimation.startAnimation(slide_up_top);
                    lnrSearchAnimation.setVisibility(View.VISIBLE);

                    lnrRequestProviders.startAnimation(slide_up);
                    lnrRequestProviders.setVisibility(View.VISIBLE);

                    break;
                case R.id.btnCancelRide:
                    showCancelRideDialog();
                    break;
                case R.id.btnCancelTrip:
                    if (btnCancelTrip.getText().toString().equals(getString(R.string.cancel_trip)))
                        showCancelRideDialog();
                    else {
                        String shareUrl = URLHelper.REDIRECT_SHARE_URL;
                        navigateToShareScreen(shareUrl);
                    }
                    break;
                case R.id.imgSos:
                    showSosPopUp();
                    break;
                case R.id.imgShareRide:
                    String url = "http://maps.google.com/maps?q=loc:";
                    navigateToShareScreen(url);
                    break;
                case R.id.imgProvider:
                    Intent intent1 = new Intent(activity, ShowProfile.class);
                    intent1.putExtra("driver", driver);
                    startActivity(intent1);
                    break;
                case R.id.imgProviderRate:
                    Intent intent3 = new Intent(activity, ShowProfile.class);
                    intent3.putExtra("driver", driver);
                    startActivity(intent3);
                    break;
                case R.id.btnCall:

                    String mobile = SharedHelper.getKey(context, "provider_mobile_no");
                    if (mobile != null && !mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
                        if (!isAppInstalled(context, "com.whatsapp")) {

                            img_whatsapp.setVisibility(View.INVISIBLE);
                        }

                        if (!isAppInstalled(context, "com.viber.voip")) {

                            img_viber.setVisibility(View.INVISIBLE);
                        }
                        lnCall.setVisibility(View.VISIBLE);
                        // layer_call_phone.setVisibility(View.VISIBLE);

                    } else {
                        // displayMessage(getString(R.string.user_no_mobile));
                    }

                    break;
                case R.id.customMarker:


                    break;
                case R.id.imgBack:
                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 1;
                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        flowValue = 1;
                    } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 2;
                    } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                        flowValue = 2;
                    }
                    layoutChanges();
                    break;
                case R.id.imgMenu:
                    if (NAV_DRAWER == 0) {
                        if (drawer != null)
                            drawer.openDrawer(Gravity.START);
                    } else {
                        NAV_DRAWER = 0;
                        if (drawer != null)
                            drawer.closeDrawers();
                    }
                    break;
                case R.id.mapfocus:
                    Double crtLat, crtLng;
                    if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                        crtLat = Double.parseDouble(current_lat);
                        crtLng = Double.parseDouble(current_lng);

                        if (crtLat != null && crtLng != null) {
                            LatLng loc = new LatLng(crtLat, crtLng);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                    break;
                case R.id.imgSchedule:
                    flowValue = 7;
                    layoutChanges();
                    break;
                case R.id.scheduleBtn:
                    SharedHelper.putKey(context, "name", "");
                    if (scheduledDate != "" && scheduledTime != "") {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        if (!DateUtils.isToday(milliseconds)) {
                            sendRequest("NORMAL");
                        } else {
                            if (utils.checktimings(scheduledTime)) {
                                sendRequest("NORMAL");
                            } else {
                                Toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.scheduleDate:
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    // set day of month , month and year value in the edit text
                                    String choosedMonth = "";
                                    String choosedDate = "";
                                    String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                    scheduledDate = choosedDateFormat;
                                    try {
                                        choosedMonth = utils.getMonth(choosedDateFormat);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (dayOfMonth < 10) {
                                        choosedDate = "0" + dayOfMonth;
                                    } else {
                                        choosedDate = "" + dayOfMonth;
                                    }
                                    afterToday = Utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                    scheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.show();
                    break;
                case R.id.scheduleTime:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        int callCount = 0;   //To track number of calls to onTimeSet()

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            if (callCount == 0) {
                                String choosedHour = "";
                                String choosedMinute = "";
                                String choosedTimeZone = "";
                                String choosedTime = "";

                                scheduledTime = selectedHour + ":" + selectedMinute;

                                if (selectedHour > 12) {
                                    choosedTimeZone = "PM";
                                    selectedHour = selectedHour - 12;
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                } else {
                                    choosedTimeZone = "AM";
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                }

                                if (selectedMinute < 10) {
                                    choosedMinute = "0" + selectedMinute;
                                } else {
                                    choosedMinute = "" + selectedMinute;
                                }
                                choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                                if (scheduledDate != "" && scheduledTime != "") {
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long milliseconds = date.getTime();
                                    if (!DateUtils.isToday(milliseconds)) {
                                        scheduleTime.setText(choosedTime);
                                    } else {
                                        if (utils.checktimings(scheduledTime)) {
                                            scheduleTime.setText(choosedTime);
                                        } else {
                                            Toast toast = new Toast(activity);
                                            Toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                                }
                            }
                            callCount++;
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    break;

                case R.id.img_viber:

                    callViber(SharedHelper.getKey(context, "provider_mobile_no"));
                    lnCall.setVisibility(View.INVISIBLE);

                    break;
                case R.id.img_call_phone:
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intentPhone);
                    lnCall.setVisibility(View.INVISIBLE);
                    break;

                case R.id.img_whatsapp:

                    openWhatsappContact(SharedHelper.getKey(context, "provider_mobile_no"));
                    lnCall.setVisibility(View.INVISIBLE);
                    break;

            }
        }
    }


    public float calculateBearingAngle(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        double Phi1 = Math.toRadians(startLatitude);
        double Phi2 = Math.toRadians(endLatitude);
        double DeltaLambda = Math.toRadians(endLongitude - startLongitude);

        double Theta = Math.atan2((Math.sin(DeltaLambda) * Math.cos(Phi2)),
                (Math.cos(Phi1) * Math.sin(Phi2) - Math.sin(Phi1) * Math.cos(Phi2) * Math.cos(DeltaLambda)));
        return (float) Math.toDegrees(Theta);
    }


    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Location error", "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                Log.e("GPS Location", "onResult: " + result);
                Log.e("GPS Location", "onResult Status: " + result.getStatus());
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.CANCELED:
                        showDialogForGPSIntent();
                        break;
                }
            }
        });


    }

    public void submitReviewCall() {

        customDialog = new CustomDialog(getContext());
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        final JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtCommentsRate.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SubmitRequestResponse", response.toString());
                utils.hideKeypad(context, activity.getCurrentFocus());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                destination.setText("");
                Repository.getInstance().getProfile(null);
                mapClear();
                value = 0;
                flowValue = 1;


                showMarkerSelectRoat();
                getProvidersList("");
                getServiceList();
                layoutChanges();
                showMarkerSelectRoat();

                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                errorHandler(error, "submitReviewCall",object.toString(),URLHelper.RATE_PROVIDER_API);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                utils.print("Entering dowload url", "entrng");
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {

            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                utils.print("Entering dwnload task", "download task");
            } catch (Exception e) {
                utils.print("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            utils.print("Resultmap", result);

            parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /*
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, PolylineOptions> {
        //ProgressDialog progressDialog = new ProgressDialog(activity);
        // Parsing the data in non-ui thread
        PolylineOptions lineOptions = null;
        boolean polyDrawn = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            points.clear();
        }

        @Override
        protected PolylineOptions doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                utils.print("routes", routes.toString());
                utils.print("routes size", routes.size() + "");
                ///////////////////////////////////
                if (routes != null) {
                    // Traversing through all the routes
                    for (int i = 0; i < routes.size(); i++) {
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = routes.get(i);

                        // Fetching all the points in i-th route
                        utils.print("path size", path.size() + "");
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            if (!point.get("lat").equalsIgnoreCase("") && !point.get("lng").equalsIgnoreCase("")) {
                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                                utils.print("abcde", points.toString());

                                if (j == 0) {
                                    sourceLatLng = new LatLng(lat, lng);
                                }
                                if (j == path.size()) {
                                    destLatLng = new LatLng(lat, lng);
                                }
                            }
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(8);
                        lineOptions.color(Color.BLUE);


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lineOptions;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(PolylineOptions lineOptions) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(sourceLatLng).title("Source").draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin_selected_marker));

            sourceMarker = mMap.addMarker(markerOptions);
            sourceMarker.setDraggable(true);
            MarkerOptions markerOptions1 = new MarkerOptions()
                    .position(destLatLng).draggable(true).title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dest_selected_marker));
            destinationMarker = mMap.addMarker(markerOptions1);
            destinationMarker.setDraggable(true);


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLngBounds bounds;
            builder.include(sourceMarker.getPosition());
            builder.include(destinationMarker.getPosition());
            bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 20);
            if (!flowStatus.equalsIgnoreCase("OPTIONAL")) {

            }
            mMap.getUiSettings().setMapToolbarEnabled(false);

            if (lineOptions == null) {

            } else {
                mMap.addPolyline(lineOptions);

                if ((customDialog != null) && (customDialog.isShowing())) {
                    customDialog.dismiss();
                }
            }
        }
    }


    boolean inMove = false;

    @Override
    public void onCameraIdle() {
        inMove = false;
        customMarker.close();

    }

    @Override
    public void onCameraMove() {

        if (!inMove) {
            inMove = true;
            customMarker.open();
            timerMoveMap = System.currentTimeMillis()/1000;

        }

    }

    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
        JSONArray jsonArray;
        private SparseBooleanArray selectedItems;
        int selectedPosition;

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }


        @Override
        public ServiceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.service_type_list_item, null);
            return new ServiceListAdapter.MyViewHolder(view);
        }

        //
        @Override
        public void onBindViewHolder(ServiceListAdapter.MyViewHolder holder, final int position) {
            String name = jsonArray.optJSONObject(position).optString("name");
            holder.serviceTitle.setText(name);

            if (name.equals("Taxi") && LocaleManager.getLanguage(context).equals("ar")) {
                holder.serviceTitle.setText("تاكسي");

            } else if (name.equals("Private ") && LocaleManager.getLanguage(context).equals("ar")) {
                holder.serviceTitle.setText("خصوصي");

            } else if (name.equals("Taxi") && LocaleManager.getLanguage(context).equals("ku")) {
                holder.serviceTitle.setText("تاكسي");

            } else if (name.equals("Private ") && LocaleManager.getLanguage(context).equals("ku")) {
                holder.serviceTitle.setText("تايبه تى");

            }
            if (position == currentPostion) {
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(position).optString("id"));
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image")).asBitmap()
                        .placeholder(R.drawable.car_select).fitCenter().dontAnimate().error(R.drawable.car_select).into(holder.serviceImg);
                holder.selector_background.setBackgroundResource(R.drawable.full_rounded_button);
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.text_color_white));
            } else {
                Glide.with(activity).load(jsonArray.optJSONObject(position).optString("image")).asBitmap()
                        .placeholder(R.drawable.car_select).fitCenter().dontAnimate().error(R.drawable.car_select).into(holder.serviceImg);
                holder.selector_background.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.black_text_color));
            }
            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == currentPostion) {
                        try {
                            lnrHidePopup.setVisibility(View.VISIBLE);
                            showProviderPopup(jsonArray.getJSONObject(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPostion = Integer.parseInt(view.getTag().toString());
                    SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                    SharedHelper.putKey(context, "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                    notifyDataSetChanged();

                    getProvidersList("service");
                }
            });
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView serviceTitle;
            ImageView serviceImg;
            LinearLayout linearLayoutOfList;
            FrameLayout selector_background;

            public MyViewHolder(View itemView) {
                super(itemView);
                serviceTitle = itemView.findViewById(R.id.serviceItem);
                serviceImg = itemView.findViewById(R.id.serviceImg);
                linearLayoutOfList = itemView.findViewById(R.id.LinearLayoutOfList);
                selector_background = itemView.findViewById(R.id.selector_background);
                height = itemView.getHeight();
                width = itemView.getWidth();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }

        if (customDialog != null && customDialog.isShowing()) {
            customDialog.cancel();
        }

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient = null;
        }

        if (mapFragment != null && mMap != null)
            mapFragment.onDestroy();

        super.onDestroy();
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(context)) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query, "");
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    class DownloadWeather extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(String... args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude +
                    "&units=metric&appid=" + "5f36898450370d957056a3c7dd1a87ed");
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            try {

                if (!xml.isEmpty()) {

                    JSONObject json = new JSONObject(xml);

                    if (json.length() > 0) {
                        JSONObject main = json.getJSONObject("main");
                        currentTemperatureField.setText(String.format("%.0f", main.getDouble("temp")) + "°");
                    }
                }
            } catch (JSONException e) {
                //   Toast.makeText(context, "Error, Check City", Toast.LENGTH_SHORT).show();
            }


        }


    }

    public void errorHandler(VolleyError error,String method_name,String payload,String url) {

        String json = "";
        NetworkResponse response = error.networkResponse;

        if (error instanceof TimeoutError || error instanceof NetworkError) {

            if (!ln_internet.isShown()) {
                ln_internet.setVisibility(View.VISIBLE);
            }

        }else if (response != null && response.data != null) {
            try {

              //  sendError( SharedHelper.getKey(context, "mobile"),SharedHelper.getKey(context, "request_id"),method_name,response.statusCode,response,payload,url);

                JSONObject errorObj = new JSONObject(new String(response.data));

                sendError( SharedHelper.getKey(context, "mobile"),SharedHelper.getKey(context, "request_id"),method_name,response.statusCode,response,payload,url);


                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                    try {
                        if (isAdded())
                            utils.displayMessage(getView(), errorObj.optString("error"));

                    } catch (Exception e) {
                       // if (isAdded())
                          ///  utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                    }
                } else if (response.statusCode == 401) {

                    GoToBeginActivity();
                 //   SharedHelper.putKey(context, "access_token", errorObj.optString("access_token"));
                    // refreshAccessToken(refreshAccessToken);
                } else if (response.statusCode == 422) {

                    json = trimMessage(new String(response.data));
                    if (json != "" && json != null) {
                        if (isAdded())
                            utils.displayMessage(getView(), json);
                    } else {
                      //  if (isAdded())
                        //    utils.displayMessage(getView(), getString(R.string.please_try_again));
                    }
                } else if (response.statusCode == 503) {
                    if (isAdded())
                        utils.displayMessage(getView(), getString(R.string.server_down));
                } else {
                   // if (isAdded())
                     //   utils.displayMessage(getView(), getString(R.string.please_try_again));
                }

            } catch (Exception e) {
                e.printStackTrace();
               // if (isAdded())
                 //   utils.displayMessage(getView(), getString(R.string.something_went_wrong));
            }

        } else {
           // if (isAdded())
              //  utils.displayMessage(getView(), getString(R.string.please_try_again));
        }


    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }


    public  void sendError(String mobile, String request_id, String method_name, int status_code, NetworkResponse exception, String payload,String url){

        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        JSONObject object = new JSONObject();
        try {
            object.put("mobile", mobile);
            object.put("request_id", request_id);
            object.put("method_name", method_name);
            object.put("status_code", status_code);
            object.put("exception", exception);
            object.put("payload", payload);
            object.put("url",url);
            object.put("app_version",""+version);
            object.put("user_agent","android-user");

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.sendERROR, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void setLocation() {

        pick_first = true;

        String key = getResources().getString(R.string.google_map_api);
        String url2 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + cmPosition.target.latitude + "," + cmPosition.target.longitude + "&key=" + key;


        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String A = jsonObject.getString("results");
                            JSONArray C = new JSONArray(A);
                            String x = C.get(0).toString();
                            JSONObject D = new JSONObject(x);
                            Log.d("formatted_address", D.getString("formatted_address"));

                            if (strPickType.equalsIgnoreCase("source")) {

                                // placePredictions  = new PlacePredictions();
                                source_address = D.getString("formatted_address");
                                source_lat = "" + cmPosition.target.latitude;
                                source_lng = "" + cmPosition.target.longitude;
                                placePredictions.strSourceAddress = source_address;
                                placePredictions.strSourceLatitude = source_lat;
                                placePredictions.strSourceLongitude = source_lng;
                                placePredictions.strSourceLatLng = source_lat + "," + source_lng;
                                strPickType = "destination";
                                customMarker.view_main_location_selector_ib.setImageResource(R.drawable.ic_dest_unselected_marker);
                                mMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(cmPosition.target)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin_selected_marker));

                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.scrollBy(0,-300));


                            } else {
                                mMap.clear();
                                rtlStaticMarker.setVisibility(View.GONE);
                                dest_address = D.getString("formatted_address");
                                dest_lat = "" + cmPosition.target.latitude;
                                dest_lng = "" + cmPosition.target.longitude;
                                destination.setText(dest_address);

                                flowValue = 1;
                                layoutChanges();

                                getServiceList();

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }
        );

        G.getInstance().addToRequestQueue(stringRequest);


    }



  }
