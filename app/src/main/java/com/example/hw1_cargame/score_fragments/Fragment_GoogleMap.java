package com.example.hw1_cargame.score_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hw1_cargame.R;
import com.example.hw1_cargame.msp_objects.ScoreRecord;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

public class Fragment_GoogleMap extends Fragment {

    private AppCompatActivity activity;

    private MapView     mMapView;
    private GoogleMap   googleMap;

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_googlemap, container, false);
        findViews(view);
        initViews(savedInstanceState);





        return view;
    }

    private void initViews(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
            }
        });
    }

    public void zoomOnMap(double lat, double lon) {

    }

    private void findViews(View view) {
        mMapView = (MapView) view.findViewById(R.id.mapView);
    }

    public void setLocation(ScoreRecord r) {
        googleMap.clear();
        // For dropping a marker at a point on the Map
        LatLng location = new LatLng(r.getLat(), r.getLon());
        googleMap.addMarker(new MarkerOptions().position(location).title(""+r.getScore()).snippet(r.getDate()));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}
