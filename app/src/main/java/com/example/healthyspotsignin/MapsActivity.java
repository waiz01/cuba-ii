package com.example.healthyspotsignin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.SphericalUtil;

import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MarkerOptions marker;
    LatLng centerlocation;

    Vector<MarkerOptions> markerOptions;

    private String URL = "https://healthyspott.000webhostapp.com/all.php";
    RequestQueue requestQueue;
    Gson gson;
    Hospital[] hospitals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        gson = new GsonBuilder().create();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        centerlocation = new LatLng(4.5,102);

        markerOptions = new Vector<>();


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        //mMap.addMarker(marker);
        for (MarkerOptions mark : markerOptions) {
            mMap.addMarker(mark);
        }

        enableMyLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerlocation, 7));
        sendRequest();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            String perms[] = {"android.permission.ACCESS_FINE_LOCATION"};
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, perms, 200);
        }
    }

    public void sendRequest() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,URL,onSuccess,onError);
        requestQueue.add(stringRequest);

    }
    public Response.Listener<String> onSuccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            hospitals = gson.fromJson(response, Hospital[].class);
            Log.d("Hospital", "Number of Hospital Data Point : " + hospitals.length);

            if (hospitals.length < 1) {
                Toast.makeText(getApplicationContext(), "Problem retrieving JSON data", Toast.LENGTH_LONG).show();
                return;
            }

            for(Hospital info: hospitals){

                Double lat = Double.parseDouble(info.lat);
                Double log = Double.parseDouble(info.log);
                String title = info.hospName;
                String snippet = info.states;

                MarkerOptions marker = new MarkerOptions()
                        .title(title)
                        .position(new LatLng(lat,log))
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                marker.visible(false);// We dont need to show, if its less than 100 meter we can show, otherwise we will just create and we will make it visble or not later

                Marker locationMarker = mMap.addMarker(marker);
                Bundle bundle = getIntent().getExtras();
                double latUser = 0, logUser = 0;
                if (bundle != null) {
                    latUser = Double.parseDouble(bundle.getString("latitude"));
                    logUser = Double.parseDouble(bundle.getString("longitude"));
                }

                // latUser = Double.parseDouble(latitudeS);
                //logUser = Double.parseDouble(longitudeS);

                LatLng yourLatLang = new LatLng(latUser, logUser);
                if (SphericalUtil.computeDistanceBetween(yourLatLang, locationMarker.getPosition()) < 15000) {
                    locationMarker.setVisible(true);
                }
                //mMap.addMarker(marker);


            }

        }
    };

    public Response.ErrorListener onError = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

}