package com.example.huntergame_v2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.huntergame_v2.CallBack_ScoreClicked;
import com.example.huntergame_v2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Fragment_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude,longitude;
    private float zoomLevel = 5.0f;
    private CallBack_ScoreClicked callBack_ScoreClicked;

    public void setCallBack_ScoreClicked(CallBack_ScoreClicked callBack_ActivityTitle) {
        this.callBack_ScoreClicked = callBack_ScoreClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_map, container, false);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        mapFragment.getMapAsync(this);
        return view ;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng topScoreLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(topScoreLocation).title("1st Score Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(topScoreLocation, zoomLevel));
    }

    public void setChosenMap(double latitude, double longitude) {
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public void focusOnScoreLocationAndZoomIn() {
        if(zoomLevel == 20.f)
            zoomLevel = 5.0f;
        else
            this.zoomLevel += 2.5f;
        onMapReady(mMap);
    }
}
