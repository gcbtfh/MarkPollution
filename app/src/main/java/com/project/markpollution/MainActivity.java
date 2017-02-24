package com.project.markpollution;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.project.markpollution.CustomAdapter.CircleTransform;
import com.project.markpollution.CustomAdapter.FeedRecyclerViewAdapter;
import com.project.markpollution.CustomAdapter.PopupInfoWindowAdapter;
import com.project.markpollution.Interfaces.OnItemClickListener;
import com.project.markpollution.Objects.Category;
import com.project.markpollution.Objects.PollutionPoint;
import com.project.markpollution.Objects.Report;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private NavigationView navigationView;
    private android.support.design.widget.FloatingActionButton fab;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Spinner spnCate;
    private ImageView imgGetLocation;
    private TextView tvRefresh;
    private SlidingDrawer simpleSlidingDrawer;
    public static ArrayList<PollutionPoint> listPo;
    private List<Category> listCate;
    private List<PollutionPoint> listPoByCateID;
    private List<PollutionPoint> listSeriousPo;
    private List<PollutionPoint> listRecentPo;
    private List<PollutionPoint> listNearbyPo;
    private HashMap<String, PollutionPoint> hasmapMarkers = new HashMap<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String url_retrive_pollutionPoint = "http://2dev4u.com/dev/markpollution/RetrievePollutionPoint.php";
    private String url_retrieve_cate = "http://2dev4u.com/dev/markpollution/RetrieveCategory.php";
    private String url_RetrieveUserByID = "http://2dev4u.com/dev/markpollution/RetrieveUserById.php?id_user=";
    private String url_RetrievePollutionOrderByRate = "http://2dev4u.com/dev/markpollution/RetrievePollutionOrderByRate.php";
    private String url_RetrievePollutionOrderByTime = "http://2dev4u.com/dev/markpollution/RetrievePollutionOrderByTime.php";
    private FeedRecyclerViewAdapter feedAdapter;
    private RecyclerView recyclerViewFeed;
    private List<Marker> listMarkers;
    private boolean isFirstTimeLaunch = true;
    private boolean isFirstTimeLaunch_DeleteReportListener = true;
    private ArrayAdapter<Category> cateAdapter;
    public static boolean triggerRefreshData = false;
    private GoogleApiClient mGoogleApiClient, googleApiClient;
    private LocationRequest locationRequest;
    private final static int REQUEST_CHECK_SETTINGS = 9;
    private Location curLocation;
    private boolean isMarkPoint = true;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    FloatingActionMenu fab_Category;
    com.github.clans.fab.FloatingActionButton fab_land, fab_water, fab_thermal,fab_noise,fab_light,fab_air;
    private Animation animTextview;
    MediaPlayer mediaPlayer = null;
    private String emails,names;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchData();
        initView();
        configCheckLocation();
        setNavigationHeader();
        loadSpinnerCate();
        listenNewReport();
        listenDeleteReport();
        showAdminMenu();
        Snackbar snackbarwellcom = Snackbar
                .make(fab, getResources().getString(R.string.signin_as) +" "+ emails, Snackbar.LENGTH_LONG);

        snackbarwellcom.show();
    }

    private void initView() {
        mediaPlayer = MediaPlayer.create(this, R.raw.newreport);
        mediaPlayer.start();
        animTextview = AnimationUtils.loadAnimation(this, R.anim.scale);
        animTextview.reset();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle(getResources().getString(R.string.hello)+", "+names);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);
        fab_Category  = (FloatingActionMenu) findViewById(R.id.fab_category);
        fab_air = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_airPO);
        fab_noise = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_noisePO);
        fab_thermal = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_thermalPO);
        fab_water = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_waterPO);
        fab_land = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_landPO);
        fab_light = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_lightPO);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        imgGetLocation = (ImageView) findViewById(R.id.imgGetLocation);
        spnCate = (Spinner) findViewById(R.id.spnCateMap);
        tvRefresh = (TextView) findViewById(R.id.textViewRefresh);
        simpleSlidingDrawer = (SlidingDrawer) findViewById(R.id.simpleSlidingDrawer);
        final ImageButton handleButton = (ImageButton) findViewById(R.id.handle);
        simpleSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                handleButton.setImageResource(R.drawable.ic_expand_00000);
            }
        });
        simpleSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                handleButton.setImageResource(R.drawable.ic_expand_00010);
            }
        });
        recyclerViewFeed = (RecyclerView) findViewById(R.id.recyclerViewFeed);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewFeed.setLayoutManager(layout);

        // initialize firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mapFragment.getMapAsync(this);
        fab.setOnClickListener(this);
        fab_light.setOnClickListener(this);
        fab_land.setOnClickListener(this);
        fab_water.setOnClickListener(this);
        fab_thermal.setOnClickListener(this);
        fab_air.setOnClickListener(this);
        fab_noise.setOnClickListener(this);
        imgGetLocation.setOnClickListener(this);
    }

    // Fetch data from SigninActivity sent
    private void fetchData() {
        Intent intent1 = getIntent();
        String po_data = intent1.getStringExtra("po_data");
        if (!po_data.isEmpty()) {
            try {
                JSONObject jObj = new JSONObject(po_data);
                JSONArray arr = jObj.getJSONArray("result");
                listPo = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject po = arr.getJSONObject(i);
                    String id_po = po.getString("id_po");
                    String id_cate = po.getString("id_cate");
                    String id_user = po.getString("id_user");
                    double lat = po.getDouble("lat");
                    double lng = po.getDouble("lng");
                    String title = po.getString("title");
                    String desc = po.getString("desc");
                    String image = po.getString("image");
                    String time = po.getString("time");
                    listPo.add(new PollutionPoint(id_po, id_cate, id_user, lat, lng, title, desc, image, time));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNavigationHeader() {
        View view = navigationView.getHeaderView(0);
        TextView tvNavName = (TextView) view.findViewById(R.id.username);
        TextView tvNavEmail = (TextView) view.findViewById(R.id.email);
        ImageView ivNavAvatar = (ImageView) view.findViewById(R.id.profile_image);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        names = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        emails = intent.getStringExtra("email");
        String avatar = intent.getStringExtra("avatar");
        tvNavName.setText(name);
        tvNavEmail.setText(email);
        Picasso.with(this).load(Uri.parse(avatar)).resize(170, 170).transform(new CircleTransform()).into(ivNavAvatar);
    }

    private void loadSpinnerCate() {
        StringRequest stringReq = new StringRequest(Request.Method.GET, url_retrieve_cate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray arr = jObj.getJSONArray("result");
                    listCate = new ArrayList<>();
                    listCate.add(0, new Category("0", "Show All"));
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject cate = arr.getJSONObject(i);
                        listCate.add(new Category(cate.getString("id_cate"), cate.getString("name_cate")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                cateAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, listCate);
                cateAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                spnCate.setAdapter(cateAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(MainActivity.this).add(stringReq);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setInfoWindowAdapter(new PopupInfoWindowAdapter(this, LayoutInflater.from(this), hasmapMarkers));
        googleMap.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Application hasn't granted permission to access your location", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        filterPollutionByCate(googleMap);

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void addMarker(GoogleMap map, PollutionPoint po) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(po.getLat(), po.getLng()))
                .icon(BitmapDescriptorFactory.fromResource(setIconMarker(po.getId_cate())))
                .anchor(0.5f, 0.85f)
                .title(po.getTitle())
                .snippet(po.getDesc()));
        marker.setTag(po.getId());

        // when marker is created. Add it into List<Marker>
        listMarkers.add(marker);
        // when marker is created. Add it into HashMap to custom InfoWindow
        hasmapMarkers.put(marker.getId(), po);
    }

    private int setIconMarker(String id) {
        int idIcon = 0;
        switch (id) {
            case "1":
                idIcon = R.drawable.marker_land_icon;
                break;
            case "2":
                idIcon = R.drawable.marker_water_icon;
                break;
            case "3":
                idIcon = R.drawable.marker_air_icon;
                break;
            case "4":
                idIcon = R.drawable.marker_thermal_icon;
                break;
            case "5":
                idIcon = R.drawable.marker_light_icon;
                break;
            case "6":
                idIcon = R.drawable.marker_noise_icon;
                break;
            default:
                break;

        }
        return idIcon;
    }

    private void getPollutionByCateID(String CateId) {
        listPoByCateID = new ArrayList<>(); // reinitialize listPoByCateID
        for (PollutionPoint po : listPo) {
            if (po.getId_cate().equals(CateId)) {
                listPoByCateID.add(po);
            }
        }
        // extract listPoByCateID into map. Don't forget reinitialize list markers
        listMarkers = new ArrayList<>();
        mMap.clear();   // clear old map
        for (PollutionPoint po : listPoByCateID) {
            addMarker(mMap, po);
        }

        // update camera
        cameraViewAllMarkers(listMarkers);

        // load infoPanel (SlidingDrawer)
        loadSlidingDrawerFeed(listPoByCateID);
    }

    private void filterPollutionByCate(final GoogleMap googleMap) {
        spnCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Category cate = (Category) spnCate.getItemAtPosition(i);
                String CateID = cate.getId();

                if (!CateID.equals("0")) {
                    getPollutionByCateID(CateID);
                } else {
                    googleMap.clear();
                    listMarkers = new ArrayList<>();    // each time filter category reinitialize List markers
                    for (PollutionPoint po : listPo) {
                        addMarker(googleMap, po);
                    }
                    // update camera
                    cameraViewAllMarkers(listMarkers);

                    // load slidingDrawer
                    loadSlidingDrawerFeed(listPo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void getSeriousPollution() {
        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url_RetrievePollutionOrderByRate, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                listSeriousPo = new ArrayList<>();
                                JSONArray arr = response.getJSONArray("response");
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject po = arr.getJSONObject(i);
                                    listSeriousPo.add(new PollutionPoint(po.getString("id_po"), po.getString
                                            ("id_cate"), po.getString("id_user"), po.getDouble("lat"), po.getDouble
                                            ("lng"), po.getString("title"), po.getString("desc"), po.getString
                                            ("image"), po.getString("time")));
                                }
                                listMarkers = new ArrayList<>();    // reinitialize list marks
                                mMap.clear();
                                for (PollutionPoint po : listSeriousPo) {
                                    addMarker(mMap, po);
                                }
                                // update camera
                                cameraViewAllMarkers(listMarkers);

                                // load infoPanel
                                loadSlidingDrawerFeed(listSeriousPo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Volley", error.getMessage());
            }
        });

        Volley.newRequestQueue(this).add(objReq);
    }

    private void getRecentPollution() {
        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url_RetrievePollutionOrderByTime, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                listRecentPo = new ArrayList<>();
                                JSONArray arr = response.getJSONArray("response");
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject po = arr.getJSONObject(i);
                                    listRecentPo.add(new PollutionPoint(po.getString("id_po"), po.getString
                                            ("id_cate"), po.getString("id_user"), po.getDouble("lat"), po.getDouble
                                            ("lng"), po.getString("title"), po.getString("desc"), po.getString
                                            ("image"), po.getString("time")));
                                }
                                listMarkers = new ArrayList<>();    // reinitialize list marks
                                mMap.clear();
                                for (PollutionPoint po : listRecentPo) {
                                    addMarker(mMap, po);
                                }

                                // update camera
                                cameraViewAllMarkers(listMarkers);

                                // load infoPanel
                                loadSlidingDrawerFeed(listRecentPo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(MainActivity.this).add(objReq);
    }

    private void getNearByPollution() {
        // getNearbyPollution
        String radius = restoringPreferences() + "000";
        int ra = Integer.parseInt(radius);
        listNearbyPo = new ArrayList<>();
        for (PollutionPoint po : listPo) {
            LatLng latLng = new LatLng(po.getLat(), po.getLng());
            double distance = SphericalUtil.computeDistanceBetween(new LatLng(curLocation.getLatitude(),
                            curLocation.getLongitude()),
                    latLng);
            if (distance <= ra) {
                listNearbyPo.add(po);
            }
        }

        // add NearbyPo markers into map
        listMarkers = new ArrayList<>();
        mMap.clear();
        for (PollutionPoint po : listNearbyPo) {
            addMarker(mMap, po);
        }

        // Draw circle around my location
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()))
                .radius(5000)
                .strokeWidth(2)
                .strokeColor(Color.RED)
                .fillColor(R.color.fill_circle));

        // update camera
        LatLng bottomLeft = SphericalUtil.computeOffset(new LatLng(curLocation.getLatitude(), curLocation.getLongitude
                ()), 5000 * Math.sqrt(2.0), 225);
        LatLng topRight = SphericalUtil.computeOffset(new LatLng(curLocation.getLatitude(), curLocation.getLongitude
                ()), 5000 * Math.sqrt(2.0), 45);
        LatLngBounds latLngBounds = new LatLngBounds(bottomLeft, topRight);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 30));


        // load infoPanel (SlidingDrawer)
        loadSlidingDrawerFeed(listNearbyPo);
    }

    public String restoringPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String language = prefs.getString("radius", "");
        return language;
    }

    private void loadSlidingDrawerFeed(List<PollutionPoint> list) {
        simpleSlidingDrawer.open();

        feedAdapter = new FeedRecyclerViewAdapter(this, list);
        recyclerViewFeed.setAdapter(feedAdapter);
        feedAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(PollutionPoint po) {
                for (Marker m : listMarkers) {
                    if (m.getTag().toString().equals(po.getId())) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 12));
                        m.showInfoWindow();
                        simpleSlidingDrawer.close();    // close slidingDrawer when focus one marker
                        break;
                    }
                }
            }
        });
    }

    private void listenNewReport() {
        DatabaseReference refNewReport = databaseReference.child("NewReports");
        refNewReport.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Report report = dataSnapshot.getValue(Report.class);
                if (!report.getId_user().equals(getUserID())) {
                    if (!isFirstTimeLaunch) {
                        if (mediaPlayer != null) {
                            mediaPlayer.reset();
                            mediaPlayer.release();
                        }else {
                            mediaPlayer.start();
                        }
                        tvRefresh.setVisibility(View.VISIBLE);
                        tvRefresh.startAnimation(animTextview);
                        refreshData();
                    }
                }
                isFirstTimeLaunch = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void listenDeleteReport() {
        DatabaseReference refDeleteReport = databaseReference.child("DeleteReports");
        refDeleteReport.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Report report = dataSnapshot.getValue(Report.class);
                if (!report.getId_user().equals(getUserID())) {
                    if (!isFirstTimeLaunch_DeleteReportListener) {
                        // Refresh data
                        StringRequest strReq = new StringRequest(Request.Method.GET, url_retrive_pollutionPoint, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (!response.equals("Error when retrieve all pollution points")) {
                                    try {
                                        JSONObject json = new JSONObject(response);
                                        JSONArray arr = json.getJSONArray("result");
                                        listPo = new ArrayList<>();     // reinitialize list<Po>
                                        for (int i = 0; i < arr.length(); i++) {
                                            JSONObject po = arr.getJSONObject(i);
                                            String id_po = po.getString("id_po");
                                            String id_cate = po.getString("id_cate");
                                            String id_user = po.getString("id_user");
                                            double lat = po.getDouble("lat");
                                            double lng = po.getDouble("lng");
                                            String title = po.getString("title");
                                            String desc = po.getString("desc");
                                            String image = po.getString("image");
                                            String time = po.getString("time");

                                            listPo.add(new PollutionPoint(id_po, id_cate, id_user, lat, lng, title, desc, image, time));
                                        }
                                        spnCate.setAdapter(cateAdapter);    // notifyDataSetChanged for spinnerCate
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Volley", error.getMessage());
                            }
                        });

                        Volley.newRequestQueue(MainActivity.this).add(strReq);
                    }
                }
                isFirstTimeLaunch_DeleteReportListener = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshData() {
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest strReq = new StringRequest(Request.Method.GET, url_retrive_pollutionPoint, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("Error when retrieve all pollution points")) {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONArray arr = json.getJSONArray("result");
                                listPo = new ArrayList<>();     // reinitialize list<Po>
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject po = arr.getJSONObject(i);
                                    String id_po = po.getString("id_po");
                                    String id_cate = po.getString("id_cate");
                                    String id_user = po.getString("id_user");
                                    double lat = po.getDouble("lat");
                                    double lng = po.getDouble("lng");
                                    String title = po.getString("title");
                                    String desc = po.getString("desc");
                                    String image = po.getString("image");
                                    String time = po.getString("time");

                                    listPo.add(new PollutionPoint(id_po, id_cate, id_user, lat, lng, title, desc, image, time));
                                }
                                spnCate.setAdapter(cateAdapter);    // notifyDataSetChanged for spinnerCate
                                // show RecentPollution
                                getRecentPollution();

                                tvRefresh.setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Volley", error.getMessage());
                    }
                });

                Volley.newRequestQueue(MainActivity.this).add(strReq);
            }
        });
    }

    private void fetchDataFromServer() {
        if (triggerRefreshData) {
            StringRequest strReq = new StringRequest(Request.Method.GET, url_retrive_pollutionPoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.equals("Error when retrieve all pollution points")) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray arr = json.getJSONArray("result");
                            listPo = new ArrayList<>();     // reinitialize list<Po>
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject po = arr.getJSONObject(i);
                                String id_po = po.getString("id_po");
                                String id_cate = po.getString("id_cate");
                                String id_user = po.getString("id_user");
                                double lat = po.getDouble("lat");
                                double lng = po.getDouble("lng");
                                String title = po.getString("title");
                                String desc = po.getString("desc");
                                String image = po.getString("image");
                                String time = po.getString("time");

                                listPo.add(new PollutionPoint(id_po, id_cate, id_user, lat, lng, title, desc, image, time));
                            }
                            // restore triggerRefreshData
                            triggerRefreshData = false;
                            // notifyDataSetChanged for spinnerCate
                            spnCate.setAdapter(cateAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Volley", error.getMessage());
                }
            });

            Volley.newRequestQueue(MainActivity.this).add(strReq);
        }
    }

    private String getUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedpref_id_user", MODE_PRIVATE);
        return sharedPreferences.getString("sharedpref_id_user", "");
    }

    private void cameraViewAllMarkers(List<Marker> markerList) {
        /**create for loop for get the latLngbuilder from the marker list*/
        LatLngBounds.Builder Lbuilder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            Lbuilder.include(marker.getPosition());
        }
        final LatLngBounds bounds = Lbuilder.build();
        /**create the camera with bounds and padding to set into map*/
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                /**set animated zoom camera into map*/
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_apps);
            builder.setTitle(getResources().getString(R.string.confirm));
            builder.setMessage(getResources().getString(R.string.ask_quit));

            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    System.exit(0);
                }

            });

            builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this,AboutUsActivity.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            return true;
        }
        if(id == R.id.action_share)
        {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Good app : URL app");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recentPo) {
            getRecentPollution();
        } else if (id == R.id.nav_reportMgr) {
            startActivity(new Intent(this, ReportManagementActivity.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        } else if (id == R.id.nav_nearbyPo) {
            getNearByPollution();
        } else if (id == R.id.nav_seriousPo) {
            getSeriousPollution();
        } else if (id == R.id.nav_admin) {
            startActivity(new Intent(this, AdminActivity.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SetupsActivity.class));
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (id == R.id.nav_heatMaps) {
            showHeatMaps();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab :
               handlerFabbutton(view);
                break;
            case R.id.fab_landPO:
                fab_Category.close(true);
                break;
            case R.id.fab_waterPO:
                fab_Category.close(true);
                break;
            case R.id.fab_airPO:
                fab_Category.close(true);
                break;
            case R.id.fab_thermalPO:
                fab_Category.close(true);
                break;
            case R.id.fab_lightPO:
                fab_Category.close(true);
                break;
            case R.id.fab_noisePO:
                fab_Category.close(true);
                break;
            case R.id.imgGetLocation :
                imgGetLocation.setVisibility(View.GONE);
                break;

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, DetailReportActivity.class);
        intent.putExtra("id_po", marker.getTag().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
    private void handlerFabbutton(View view){
        Snackbar snackbar = Snackbar.make(view, R.string.tvNewreport, 10000);
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                //see Snackbar.Callback docs for event details
                fab.setImageResource(R.drawable.fab_add);
                imgGetLocation.setVisibility(View.GONE);
                isMarkPoint = true;
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });
        if (isMarkPoint) {
            isMarkPoint = false;
            imgGetLocation.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.checkmark);
            snackbar.setAction(getResources().getString(R.string.action_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isMarkPoint = true;
                            fab.setImageResource(R.drawable.fab_add);
                            imgGetLocation.setVisibility(View.GONE);
                        }
                    }).show();

        } else {
            fab.setImageResource(R.drawable.fab_add);
            Intent i = new Intent(MainActivity.this, SendReportActivity.class);
            i.putExtra("Lat", mMap.getCameraPosition().target.latitude);
            i.putExtra("Long", mMap.getCameraPosition().target.longitude);
            imgGetLocation.setVisibility(View.GONE);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            isMarkPoint = true;
        }
    }
    private void configCheckLocation() {
        // Config googleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        // Config connectionRequest
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
    }

    //    Runs when a GoogleApiClient object successfully connects.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Config locationSettingRequest | Need to reference locationRequest
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        // Retrieve result of locationSettingRequest
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // NO need to show the dialog;

                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //  GPS turned off, Show the user a dialog
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }


                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are unavailable so not possible to show any dialog now
                        break;
                }
            }
        });

        // get Current Location
        if (curLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            curLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        curLocation = location;
    }

    // Handle ResolutionRequired
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Location enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location is not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                locationRequest,
                this
        );
    }

    private void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        );
    }

    // Whenever comeback this Activity. RefreshData
    @Override
    protected void onResume() {
        super.onResume();

        fetchDataFromServer();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    // Disconnect googleApiClient when activity stopped
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();

        super.onStart();
    }

    private void showAdminMenu() {
        // Reference to Administative Portal
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.getItem(5);    // System
        SubMenu subMenu = item.getSubMenu();
        final MenuItem adminMenu = subMenu.getItem(0); // Admin Portal

        // Show or hide admin menu
        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url_RetrieveUserByID + getUserID(),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("success")) {
                        JSONArray arr = response.getJSONArray("response");
                        JSONObject user = arr.getJSONObject(0);
                        if (user.getInt("is_admin") == 1) {
                            adminMenu.setVisible(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(objReq);
    }

    private void logout() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                finish();
                startActivity(new Intent(MainActivity.this, SigninActivity.class));
            }
        });
    }

    private void showHeatMaps() {
        mMap.clear();
        LatLngBounds.Builder Lbuilder = new LatLngBounds.Builder();
        List<LatLng> listMarkersHeatMaps = new ArrayList<>();
        for (PollutionPoint po : listPo) {
            listMarkersHeatMaps.add(new LatLng(po.getLat(), po.getLng()));
            Lbuilder.include(new LatLng(po.getLat(), po.getLng()));
        }
        mProvider = new HeatmapTileProvider.Builder().data(listMarkersHeatMaps).radius(15).build();
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        final LatLngBounds bounds = Lbuilder.build();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }
        });
    }

}
