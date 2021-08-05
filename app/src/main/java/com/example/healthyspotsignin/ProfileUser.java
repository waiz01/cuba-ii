package com.example.healthyspotsignin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyspotsignin.databinding.ActivityMapsBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ProfileUser extends AppCompatActivity implements OnMapReadyCallback {

    TextView tvcoords, tvaddr;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    double lat,lon;




    String[] perms = {"android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE"};

    private FusedLocationProviderClient fusedLocationClient;

    GoogleSignInClient mGoogleSignInClient;
    String name, email,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        Drawable drawable= getResources().getDrawable(R.drawable.shj);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(newdrawable);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        ImageView imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        tvaddr = (TextView) findViewById(R.id.tvlocation);

        TextView tvName = (TextView) findViewById(R.id.tvname);
        TextView tvEmail= (TextView) findViewById(R.id.tvemail);
        TextView url1= (TextView) findViewById(R.id.urlphoto);

        Button neaby= (Button) findViewById(R.id.neaby);




        name = getIntent().getStringExtra("Name");
        email = getIntent().getStringExtra("Email");
        url = getIntent().getStringExtra("Image");

        Picasso.get().load(url).into(imgProfilePic);


        tvName.setText(name);
        tvEmail.setText(email);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, perms, 200);

            return;
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest = LocationRequest.create();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2000);


        locationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(), "Unable detect location", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                  //  tvcoords.setText("" + lat + "," + lon);
                    // ...
                }
            }
        };

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();


                Intent intent1 = new Intent(getApplicationContext(), MapsActivity.class);
                String lats = Double.toString(lat);
                String lons = Double.toString(lon);
                intent1.putExtra("latitude", lats);
                intent1.putExtra("longitude", lons);

                neaby.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        startActivity(intent1);

                    }
                });

             //   tvcoords.setText("" + lat + "," + lon);
                LatLng sydney = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
                // Zoom in, animating the camera.
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);

                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(lat,lon,1);
                    Address address = addressList.get(0);
                    String line  = address.getAddressLine(0);
                    String area = address.getAdminArea();
                    String locality = address.getLocality();
                    String country = address.getCountryName();
                    String postcode = address.getPostalCode();

                    tvaddr.setText(line+"\n"+area+"\n"+locality+"\n"+postcode+"\n"+country);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, perms, 200);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onMapReady(GoogleMap googleMap ) {



        mMap = googleMap;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.signout:
                signOut();
                return true;
            case R.id.aboutus:
                Intent intent = new Intent(this,AboutActivity.class);

                intent.putExtra("Name", name);
                intent.putExtra("Email", email);
                intent.putExtra("Image", url);

                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext() , email + " Signed Out" , Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ProfileUser.this, MainActivity.class));
                    }
                });
    }
}