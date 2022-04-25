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
import java.util.TreeMap;

public class Fragment_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude=-33.924167, longitude=150.882190; // default location
    private TreeMap<Integer, Double> scoresAndLatitudes;
    private TreeMap<Integer,Double> scoresAndLongitudes;
    private float zoomLevel = 5.0f;
    private CallBack_ScoreClicked callBack_ScoreClicked;
    private String currentScore="";

    public Fragment_Map() {
    }

    public void setCallBack_ScoreClicked(CallBack_ScoreClicked callBack_ScoreClicked) {
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
        mMap.addMarker(new MarkerOptions().position(topScoreLocation).title(currentScore));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(topScoreLocation, zoomLevel));
    }

    /**
     * This function sets the Google Map focus and add marker according to the score rank.
     * @param rank - This is the key of the latitude and longitude needed for the Google Map settings.
     */
    public void focusOnScoreLocationAndSetMarker(int rank) {
        mMap.clear();
        if(scoresAndLatitudes.size() > rank) {
            int key = (int) scoresAndLatitudes.descendingKeySet().toArray()[rank];
            latitude = (double) scoresAndLatitudes.get(key);
            longitude = (double) scoresAndLongitudes.get(key);
            currentScore ="Score Location: " + key;
        } else { // default location
            latitude=-33.924167;
            longitude=150.882190;
            currentScore = "Unscored yet :(";
        }
        onMapReady(mMap);
    }

    public void setMaps(TreeMap<Integer, Double> scoresAndLatitudes, TreeMap<Integer, Double> scoresAndLongitudes) {
        this.scoresAndLatitudes = scoresAndLatitudes;
        this.scoresAndLongitudes = scoresAndLongitudes;
    }
}
