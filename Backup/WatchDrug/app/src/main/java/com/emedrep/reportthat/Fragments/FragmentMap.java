package com.emedrep.reportthat.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.emedrep.reportthat.Library.Constant;
import com.emedrep.watchdrug.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class FragmentMap  extends Fragment implements OnMapReadyCallback {
    SharedPreferences prefs;

    SupportMapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }


    // @Override
    public void onMapReady(GoogleMap googleMap) {

        Bundle bundle = this.getArguments();
        String myValue = bundle.getString("cordinateMeta");
        String[] arrValue = myValue.split("_");
        String latValue = arrValue[0];
        String longValue = arrValue[1];
        if (latValue == null || longValue == null) {
            Toast.makeText(getActivity(), "GPS Information not available for this image", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            double latitude = Double.parseDouble(latValue);
            double longitude = Double.parseDouble(longValue);

            LatLng position = new LatLng(latitude, longitude);
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
//                .title("Marker"));


            // Instantiating MarkerOptions class
            MarkerOptions options = new MarkerOptions();

            // Setting position for the MarkerOptions
            options.position(position);

            // Setting title for the MarkerOptions
            options.title("Position");

            // Setting snippet for the MarkerOptions
            //options.snippet("Latitude:" + latitude + ",Longitude:" + longitude);
            googleMap.addMarker(options);

            // Creating CameraUpdate object for position
            CameraUpdate updatePosition = CameraUpdateFactory.newLatLng(position);

            // Creating CameraUpdate object for zoom
            // CameraUpdate updateZoom = CameraUpdateFactory.zoomBy(5);

            // Updating the camera position to the user input latitude and longitude
            googleMap.moveCamera(updatePosition);

            // Applying zoom to the marker position
            //googleMap.animateCamera(updateZoom);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

            googleMap.animateCamera(zoom);
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "GPS Information not available for this image", Toast.LENGTH_LONG).show();
            return;
        }
    }
}
