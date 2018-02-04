package com.nubiz.mybloodbank;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> locations = new ArrayList<>();
    private EditText edt_address;
    private Button btn_search;
    private ProgressBar progressBar;
    private Context context;
    private LatLng searchedLatLng;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        edt_address = (EditText) findViewById(R.id.edt_address);
        btn_search = (Button) findViewById(R.id.btn_search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


//        locations.add(new LatLng(28.451647, 77.071755));
        locations.add(new LatLng(28.4513026, 77.0695555));
        locations.add(new LatLng(28.4513686, 77.0709181));
        locations.add(new LatLng(28.451166, 77.072935));
        locations.add(new LatLng(28.452807, 77.073021));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                searchedLatLng = getLocationFromAddress(context, edt_address.getText().toString());

                if (searchedLatLng != null) {
                    AddMarker(searchedLatLng,edt_address.getText().toString() ,R.mipmap.user_map_icon);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(searchedLatLng));
                } else {
                    Snackbar snackbar = Snackbar.make(view, "No location found", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.btn_locate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create class object
                gps = new GPSTracker(context);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    AddMarker(new LatLng(latitude,longitude),"Me",R.mipmap.user_map_icon);
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Demo markers
        for (int i = 0; i < locations.size(); i++)
            AddMarker(locations.get(i),"Donor " + (i + 1),R.mipmap.donor_map_icon);

        //User location
        AddMarker(new LatLng(28.451647, 77.071755),"User Location" ,R.mipmap.user_map_icon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(28.451647, 77.071755)));
    }

    /**
     * Sets the location/markers on the map
     * @param marlerLatLng LatLng
     * @param markerTitle String
     * @param markerIcon markerIcon
     */
    public void AddMarker(LatLng marlerLatLng, String markerTitle, int markerIcon  ) {
        mMap.addMarker(new MarkerOptions()
                .position(marlerLatLng)
                .title(markerTitle))
                .setIcon(BitmapDescriptorFactory.fromResource(markerIcon));
    }

    /**
     * Converts address to LatLng object
     *
     * @param context
     * @param strAddress String, address
     * @return LatLng
     */
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size() == 0) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

}
