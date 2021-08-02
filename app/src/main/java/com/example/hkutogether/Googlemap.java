package com.example.hkutogether;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Googlemap extends FragmentActivity implements OnMapReadyCallback {
    public static GoogleMap map;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemap);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.addMarker(new MarkerOptions()
                .position(MainPage.currentLocation)
                .title("Your Current Location")
                .snippet(MainPage.currentAddress));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MainPage.currentLocation, 17));

        if (MainPage.f1location!=null) {
            map.addMarker(new MarkerOptions()
                    .position(MainPage.f1location)
                    .title("Your Friend")
                    .snippet(MainPage.currentfriend1));
        }
        if (MainPage.f2location!=null) {
            map.addMarker(new MarkerOptions()
                    .position(MainPage.f2location)
                    .title("Your Friend")
                    .snippet(MainPage.currentfriend2));
        }
        if (MainPage.f3location!=null) {
            map.addMarker(new MarkerOptions()
                    .position(MainPage.f3location)
                    .title("Your Friend")
                    .snippet(MainPage.currentfriend3));
        }
    }
}
