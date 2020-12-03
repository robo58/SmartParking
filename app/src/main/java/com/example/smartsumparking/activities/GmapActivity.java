package com.example.smartsumparking.activities;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartsumparking.models.Parking;
import com.example.smartsumparking.models.ParkingSpace;
import com.example.smartsumparking.services.ExampleJobService;
import com.example.smartsumparking.fragments.LoginFragment;
import com.example.smartsumparking.fragments.MessageFragment;
import com.example.smartsumparking.R;
import com.example.smartsumparking.fragments.ReservationsFragment;
import com.example.smartsumparking.helpers.Settings;
import com.example.smartsumparking.helpers.SharedPref;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import directionhelpers.FetchURL;
import directionhelpers.GetDirectionsData;
import directionhelpers.TaskLoadedCallback;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;



public class GmapActivity extends FragmentActivity implements OnRequestPermissionsResultCallback,OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "GmapActivity";
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields;
    Intent intent;
    Parking[] parkings;
    ImageView close;
    SharedPref sharedpref;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    RequestQueue rq;
    private static final String SHOWCASE_ID = "sequence example";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Socket mSocket;
    private Boolean mLocationPermissionsGranted = false;
    private Button searchbtn;
    PlacesClient placesClient;
    GoogleMap map;
    String apiKey;
    MarkerOptions m1,m2;
    Marker marker;
    Circle mCircle;
    ImageView mButtonOne;
    BottomNavigationView bottomtip;
    boolean admin = false;
    JSONArray park;
    String parkingName,availableSpaces,workhours,price;
    LatLng sumParking = new LatLng(43.346279,17.797821);
    private Polyline currentPolyline;
    private Object[] dataTransfer;
    TextView info_headline,info_counter,info_workhours_count,info_price_count;
    ImageView info_icon;
    ConstraintLayout info;
    BottomNavigationView bottomNavigationView;
    FrameLayout frame_container;
    ConstraintLayout map_frame;
    Spinner parkingList;
    List<String> listParkings;
    List<Marker> availableParkings;
    ArrayAdapter<String> listAdapter;
    List<String> reservedSpaces;
    FirebaseFirestore db;
    String pSlobodno;
    public Emitter.Listener pChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        System.out.println("promjena kod mjesta "+ data.getString("parking_space_name"));
                        for(Parking parking : parkings){
                            if(parking.getId() == data.getInt("parking_id")){
                                    for(ParkingSpace space : parking.getParkingSpaces()){
                                        if(space.getName().equals(data.getString("parking_space_name"))){
                                            if (data.getInt("occupied") == 1) {
                                                space.getParkingSpaceMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                parking.setOccupied(parking.getOccupied()+1);
                                            } else {
                                                space.getParkingSpaceMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                                parking.setOccupied(parking.getOccupied()-1);
                                            }
                                        }
                                    }
                            }
                            info_counter.setText((parking.getCapacity() - parking.getOccupied()) + "/" + parking.getCapacity());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + apiKey;
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            System.out.println("location found");
                            Location currentLocation = (Location) task.getResult();
                            m1 = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).title("My location");
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),17));
                            Toast.makeText(GmapActivity.this, "Moved to your location", Toast.LENGTH_SHORT).show();
                        }else{
                            System.out.println("cant get current location");
                            Toast.makeText(GmapActivity.this, "Unable to get your location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            e.getStackTrace();
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_gmap);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mButtonOne= findViewById(R.id.imageView);
        bottomtip=findViewById(R.id.bottom_navigation);
        frame_container = findViewById(R.id.frame_container);
        frame_container.setVisibility(View.GONE);
        map_frame = findViewById(R.id.map_frame);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_map);
        this.searchbtn = (Button) findViewById(R.id.findLocationbtn);
        apiKey = getString(R.string.map_key);
        info = findViewById(R.id.info);
        close = findViewById(R.id.ic_close);
        info_counter = findViewById(R.id.info_counter);
        info_headline = findViewById(R.id.info_headline);
        info_price_count = findViewById(R.id.info_price_count);
        info_workhours_count = findViewById(R.id.info_workhours_count);
        info_icon = findViewById(R.id.info_icon);
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        info.setVisibility(View.GONE);
        parkingList = findViewById(R.id.parking_list);
        listParkings = new ArrayList<>();
        listParkings.add(" - Parking list - ");
        availableParkings = new ArrayList<>();
        reservedSpaces = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        this.searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        rq = Volley.newRequestQueue(this);
        try {
            mSocket = IO.socket("https://demo.smart.sum.ba/parking-events");
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);
        getReserved();
        getUserStatus();

        final String tips_shared_pref = "usertips";
        SharedPreferences tips = getSharedPreferences(tips_shared_pref, 0);
        if(tips.getBoolean("firstime",true)){
        presentShowcaseSequence();
        tips.edit().putBoolean("firstime", false).apply();
    }
        isAdmin();
    }

    private void presentShowcaseSequence() {


        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(mButtonOne, "Noćna vožnja? Prilagođavajte izgled mape prema svojim potrebama", "DALJE");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(searchbtn)
                        .setDismissText("DALJE")
                        .setContentText(" Pronađite traženu lokaciju u tražilici")

                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(bottomtip)
                        .setDismissText("OK")
                        .setContentText("Za rezervaciju parking mjesta prvo napravite svoj račun pomoću Vaše mail adrese")
                        .withRectangleShape(true)
                        .build()
        );


        sequence.start();

    }


    public void loadLocale(){
        SharedPreferences prefs=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language=prefs.getString("My_Lang","");
        setLocale(language);

    }
    private void setLocale(String lang){
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }

    public void getReserved(){
        db.collection("reservations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        setReserved(document.getData().get("parking_spot").toString());
                    }
                }
            }
        });
    }

    public void setReserved(String pName){
        if(!(reservedSpaces.contains(pName))){
            reservedSpaces.add(pName);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                System.out.println(place.getName());
                geolocate(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Toast.makeText(this,"Greska", Toast.LENGTH_SHORT).show();
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng sumParking = new LatLng(43.346279,17.797821);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sumParking,17));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.mLocationPermissionsGranted = true;
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
            marker = map.addMarker(new MarkerOptions().position(sumParking).visible(false));
            mCircle = map.addCircle(new CircleOptions().center(sumParking).visible(false));
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST);
        }
        sendRequest();
        mSocket.on("parking-lot-state-change", pChange);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                getUserStatus();
                isAdmin();
                switch (menuItem.getItemId()){
                    case R.id.menu_map:
                        frame_container.setVisibility(View.GONE);
                        map_frame.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.menu_user:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new LoginFragment()).commit();
                        map_frame.setVisibility(View.GONE);
                        frame_container.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.menu_reservations:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new ReservationsFragment()).commit();
                        map_frame.setVisibility(View.GONE);
                        frame_container.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.menu_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new Settings()).commit();
                        map_frame.setVisibility(View.GONE);
                        frame_container.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.menu_adminUI:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new MessageFragment()).commit();
                            map_frame.setVisibility(View.GONE);
                            frame_container.setVisibility(View.VISIBLE);
                            return true;
                }
                return false;
            }
        });
        showParkingList();
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
    }

    void isAdmin() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            db.collection("users").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String,Boolean> role = (HashMap<String, Boolean>) document.getData().get("roles");
                            Boolean admin = role.get("admin");
                            setAdmin(admin);
                        }
                    }
                }
            });
        }else{
            setAdmin(false);
        }
    }

    private void setAdmin(Boolean admin) {
        System.out.println("user is " + (admin ? "admin" : "not admin"));
        this.admin = admin;
        if(admin){
            bottomNavigationView.getMenu().findItem(R.id.menu_adminUI).setVisible(true);
        }else{
            //bottomNavigationView.getMenu().removeItem(R.id.menu_adminUI);
            bottomNavigationView.getMenu().findItem(R.id.menu_adminUI).setVisible(false);

        }
    }

    void getUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            bottomNavigationView.getMenu().findItem(R.id.menu_adminUI).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.menu_reservations).setVisible(false);
        }else{
            isAdmin();
            bottomNavigationView.getMenu().findItem(R.id.menu_reservations).setVisible(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputmanager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanager != null) {
                inputmanager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception var2) {
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.mLocationPermissionsGranted = true;
                map.setMyLocationEnabled(true);
                map.setOnMyLocationButtonClickListener(this);
                map.setOnMyLocationClickListener(this);
                getDeviceLocation();
                Toast.makeText(this, "Moved to your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BitmapDescriptor getIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void sendRequest(){
        String url = "https://demo.smart.sum.ba/parking?withParkingSpaces=1";
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length()>0){
                            parkings = new Parking[response.length()];
                            for(int i = 0;i<response.length();i++){
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    parkingName = jsonObj.getString("name") + " na " + jsonObj.getString("address");
                                    availableSpaces = jsonObj.getString("normal_available") + "/" + jsonObj.getString("capacity");
                                    price = "0 KM po satu";
                                    workhours = "0:00 - 24:00";
                                    park = jsonObj.getJSONArray("parkingSpaces");
                                    ParkingSpace[] parkingSpaces = new ParkingSpace[park.length()];
                                    for(int j = 0; j< park.length();j++){
                                        JSONObject p = park.getJSONObject(j);
                                        parkingSpaces[j] = new ParkingSpace(p.getInt("id"), p.getInt("id_parking_lot"), p.getInt("occupied") == 1,
                                                new LatLng(p.getDouble("lat"), p.getDouble("lng")), p.getString("parking_space_name"));
                                        MarkerOptions markerOptions = new MarkerOptions().position(parkingSpaces[j].getLocation()).title(parkingSpaces[j].getName()).icon(parkingSpaces[j].getIcon()).visible(false);
                                        parkingSpaces[j].setParkingSpaceMarker(map.addMarker(markerOptions));
                                    }
                                    parkings[i] = new Parking(jsonObj.getInt("id"),jsonObj.getString("name"), jsonObj.getString("address"),
                                            new LatLng(jsonObj.getDouble("lat"),jsonObj.getDouble("lng")), jsonObj.getInt("capacity_normal"), jsonObj.getInt("normal_occupied"), parkingSpaces);
                                    parkings[i].setParkingMarker(map.addMarker(
                                            new MarkerOptions().position(parkings[i].getLocation())
                                                    .title(parkings[i].getName()).icon(getIconFromDrawable(getResources().getDrawable(parkings[i].getIcon()))).snippet("Click to show info and spots")));
                                    listParkings.add(parkings[i].getName());

                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object." + e.getMessage());
                                }
                            }
                        }else{
                            System.out.println("nema parkinga");
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }
        );

        rq.add(arrReq);
        }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location.getLatitude() +", "+location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Moved to your location", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        hideKeyboard();
        return false;
    }

    private void geolocate(String searchString){
        Geocoder geocoder = new Geocoder(GmapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            e.getStackTrace();
        }
        if(list.size()>0){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            Address address = list.get(0);
            //Toast.makeText(this, address.getAddressLine(0).toString(), Toast.LENGTH_SHORT).show();
            m2 = new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).title(address.getLocality());
            //the include method will calculate the min and max bound.
            builder.include(m1.getPosition());
            builder.include(m2.getPosition());

            final LatLngBounds bounds = builder.build();

            final int zoomWidth = getResources().getDisplayMetrics().widthPixels;
            final int zoomHeight = getResources().getDisplayMetrics().heightPixels;
            final int zoomPadding = (int) (zoomWidth * 0.10);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,zoomWidth,zoomHeight,zoomPadding));
            map.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).title(address.getLocality()));
            new FetchURL(GmapActivity.this).execute(getUrl(m1.getPosition(), m2.getPosition(), "driving"), "driving");
            dataTransfer = new Object[4];
            String url = getUrl(m1.getPosition(), m2.getPosition(), "driving");
            GetDirectionsData getDirectionsData = new GetDirectionsData();
            marker.setPosition(m2.getPosition());
            marker.setVisible(true);
            dataTransfer[0] = map;
            dataTransfer[1] = url;
            dataTransfer[2] = m2.getPosition();
            dataTransfer[3] = marker;
            mCircle.setCenter(marker.getPosition());
            mCircle.setRadius(300.00);
            mCircle.setFillColor(0x44ff0000);
            mCircle.setStrokeColor(0xffff0000);
            mCircle.setStrokeWidth(8);
            mCircle.setVisible(true);
            float[] distance = new float[2];
            Location.distanceBetween( sumParking.latitude, sumParking.longitude,
                    mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);
            if( distance[0] > mCircle.getRadius()  ){
                Toast.makeText(getBaseContext(), "No parkings nearby", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Found a parking nearby" , Toast.LENGTH_LONG).show();
                //map.addMarker(new MarkerOptions().position(sumParking));
                //new FetchURL(GmapActivity.this).execute(getUrl(m2.getPosition(), sumParking, "driving"), "driving");
            }
            getDirectionsData.execute(dataTransfer);
        }
    }

    public void showParkingInfo(final Parking parking){
        info.setVisibility(View.VISIBLE);
        info_headline.setText(parking.getName());
        info_workhours_count.setText(workhours);
        info_price_count.setText(price);
        info_counter.setText((parking.getCapacity() - parking.getOccupied()) + "/" + parking.getCapacity());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setVisibility(View.GONE);
                for(int k=0;k<parking.getParkingSpaces().length;k++){
                    parking.getParkingSpaces()[k].getParkingSpaceMarker().setVisible(false);
                }
                parking.getParkingMarker().setVisible(true);
                parkingList.setSelection(0);
            }
        });
    }

 public void showParkingList(){
     listAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,listParkings);
     listAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
     parkingList.setAdapter(listAdapter);
     parkingList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
             if(!adapterView.getSelectedItem().toString().equals(" - Parking list - ")) {
                 System.out.println(adapterView.getSelectedItem().toString());
                 showParkingListHelper(adapterView.getSelectedItem().toString());
             }
         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView) {

         }
     });
 }

 public void  showParkingListHelper(String parkingName){
     for (Parking parking : parkings) {
         if (parkingName.equals(parking.getName())) {
             map.moveCamera(CameraUpdateFactory.newLatLng(parking.getLocation()));
             for (int k = 0; k < parking.getParkingSpaces().length; k++) {
                 parking.getParkingSpaces()[k].getParkingSpaceMarker().setVisible(true);
             }
             showParkingInfo(parking);
             parking.getParkingMarker().setVisible(false);
         }
     }

 }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.hybrid_map:
                        sharedpref = new SharedPref(getApplicationContext());
                        sharedpref.setNightModeState(false);
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        return true;
                    case R.id.satellite_map:
                        sharedpref = new SharedPref(getApplicationContext());
                        sharedpref.setNightModeState(false);
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        return true;
                    case R.id.terrain_map:
                        sharedpref = new SharedPref(getApplicationContext());
                        sharedpref.setNightModeState(false);
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        return true;
                    case R.id.night_map:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        sharedpref = new SharedPref(getApplicationContext());
                        sharedpref.setNightModeState(true);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = map.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            GmapActivity.this, R.raw.mapstyle));

                            if (!success) {
                                Log.e("GmapActivity", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("GmapActivity", "Can't find style. Error: ", e);
                        }
                        return true;

                    case R.id.retro_map:
                        sharedpref = new SharedPref(getApplicationContext());
                        sharedpref.setNightModeState(false);
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = map.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            GmapActivity.this, R.raw.mapstylee));

                            if (!success) {
                                Log.e("GmapActivity", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("GmapActivity", "Can't find style. Error: ", e);
                        }
                        return true;

                    case R.id.dark_map:
                        sharedpref = new SharedPref(getApplicationContext());
                        sharedpref.setNightModeState(true);
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = map.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            GmapActivity.this, R.raw.mapstyledark));

                            if (!success) {
                                Log.e("GmapActivity", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("GmapActivity", "Can't find style. Error: ", e);
                        }
                        return true;



                    default:
                        return false;
                }
            }

        });
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println(marker.getPosition());
        System.out.println("clicked");
        for (Parking parking : parkings) {
            System.out.println(parking.getParkingMarker().getPosition());
            if (parking.getParkingMarker().getPosition().equals(marker.getPosition())) {
                parkingList.setSelection(parking.getId());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob(){
        ComponentName componentName = new ComponentName(this, ExampleJobService.class);
        JobInfo info = new JobInfo.Builder(123,componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG,"Job scheduled");
        }else {
            Log.d(TAG,"Job not scheduled");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob(){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG,"Job cancelled");
    }

}
