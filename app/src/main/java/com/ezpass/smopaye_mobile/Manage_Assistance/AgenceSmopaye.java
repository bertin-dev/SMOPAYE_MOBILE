package com.ezpass.smopaye_mobile.Manage_Assistance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Config.Global;
import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AgenceSmopaye extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private ListView listView;
    private GoogleMap mMap;
    //markers all define place
    private ArrayList<LatLng> arrayList = new ArrayList<>();
    private LatLng camair = new LatLng(Global.Latitude_camair, Global.Longitude_camair);
    private LatLng omnisport = new LatLng(Global.Latitude_omnisport, Global.Longitude_omnisport);
    private LatLng soa_campus = new LatLng(Global.Latitude_soa_campus, Global.Longitude_soa_campus);
    private LatLng soa_marche = new LatLng(Global.Latitude_marche_soa, Global.Longitude_marche_soa);
    private Double distance = 0.0;
    private DecimalFormat df = new DecimalFormat("0.00"); // import java.text.DecimalFormat;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private ArrayList<Object> list;
    private LatLng currentPosition;

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_agence_smopaye);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.pointDeVenteSmopaye));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }else{
            startLocationService();
        }


        //marker position
        arrayList.add(camair);
        arrayList.add(omnisport);
        arrayList.add(soa_campus);
        arrayList.add(soa_marche);

        listView = findViewById(R.id.exp_list_view);
        list = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               /* if (listView1.getItemAtPosition(position).toString().equalsIgnoreCase("YAOUNDE")){
                    Intent intent = new Intent(getApplicationContext(), DetailsAgenceSmopaye.class);
                    startActivity(intent);
                }*/

                if (position == 1) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 3) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 4) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 6) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 7) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 8) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 9) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }


            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
    }




    private void startLocationService() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that this current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        //
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            } else {
                Toast.makeText(this, getString(R.string.permissionRefuse), Toast.LENGTH_SHORT).show();
            }
        }
    }




    private String myConvert(LatLng depart, LatLng arrive){

        if((SphericalUtil.computeDistanceBetween(depart, arrive)/1000) < 1){
            return df.format(SphericalUtil.computeDistanceBetween(depart, arrive)) + " m";
        } else{
            return df.format(SphericalUtil.computeDistanceBetween(depart, arrive)/1000) + " km";
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 500);
            return;
        }

        mMap.clear();
        //marker
        for(int i=0;i<arrayList.size();i++){
            mMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title(getString(R.string.point_marchands)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
        }

        //distance = SphericalUtil.computeDistanceBetween(camair, omnisport);
        //Toast.makeText(getActivity(), distance/1000 + " km", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onResume() {
        super.onResume();
        //Now lets connect to the API
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if(mGoogleApiClient != null){
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            //Toast.makeText(getActivity(), "Veuillez Activer votre GPS", Toast.LENGTH_SHORT).show();
        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            //Toast.makeText(getContext(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();




            currentPosition = new LatLng(currentLatitude, currentLongitude);
            //Toast.makeText(getActivity(), currentPosition.toString(), Toast.LENGTH_SHORT).show();
            list.clear();
            list.add(new String(getString(R.string.centre)));
            list.add(new AgenceSmopayeModel(getString(R.string.yde), getString(R.string.camair), myConvert(camair, currentPosition)));
            list.add(new AgenceSmopayeModel(getString(R.string.yde), getString(R.string.omnisport), myConvert(omnisport, currentPosition)));
            list.add(new AgenceSmopayeModel(getString(R.string.soa), getString(R.string.soa_campus), myConvert(soa_campus, currentPosition)));
            list.add(new AgenceSmopayeModel(getString(R.string.soa), getString(R.string.soa_marche), myConvert(soa_marche, currentPosition)));


            listView.setAdapter(new AgenceSmopayeAdapter(this.getApplicationContext(), list));


        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        //Toast.makeText(getContext(), currentLatitude + " PIPO " + currentLongitude + "", Toast.LENGTH_LONG).show();
        currentPosition = new LatLng(currentLatitude, currentLongitude);
    }


    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            toolbar.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDarkRed));

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
        } else{
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
