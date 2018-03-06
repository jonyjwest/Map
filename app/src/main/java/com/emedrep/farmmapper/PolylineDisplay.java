package com.emedrep.farmmapper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.emedrep.farmmapper.DB.CoordinateDataSource;
import com.emedrep.farmmapper.DB.LandDataSource;
import com.emedrep.farmmapper.Model.Coordinate;
import com.emedrep.farmmapper.Model.Land;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class PolylineDisplay extends AppCompatActivity  implements
        OnMapReadyCallback {
    List<Coordinate> listCoordinate;
    CoordinateDataSource coordinateDataSource;
    LandDataSource landDataSource;
    Land land;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polyline_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        landDataSource=new LandDataSource(this);

        coordinateDataSource =new CoordinateDataSource(this);
        Intent intent=getIntent();
        int landId=intent.getIntExtra("landId",0);
        land=landDataSource.getLandById(landId);
        listCoordinate=coordinateDataSource.getAllCoordinateByLandId(landId);
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
try {
    ArrayList<LatLng> latLngs = new ArrayList<>();
    for (int i = 0; i < listCoordinate.size(); i++) {
        Coordinate coordinate = listCoordinate.get(i);
        LatLng latLng = new LatLng(Double.parseDouble(coordinate.latitude), Double.parseDouble(coordinate.longitude));
        latLngs.add(latLng);
    }
    PolygonOptions polygonOptions=new PolygonOptions();
    polygonOptions.add(latLngs.toArray(new LatLng[latLngs.size()]));
    polygonOptions.strokeColor(ContextCompat.getColor(this,android.R.color.holo_orange_dark));
    polygonOptions.fillColor(ContextCompat.getColor(this,android.R.color.holo_orange_light));


    MarkerOptions markerOptions = new MarkerOptions();

    Coordinate coordinate = listCoordinate.get(0);
    LatLng latLng = new LatLng(Double.parseDouble(coordinate.latitude), Double.parseDouble(coordinate.longitude));
    markerOptions.position(latLng).title(land.getName()).snippet("This is land "+land.getName());;

    googleMap.addMarker(markerOptions);

    googleMap.addPolygon(polygonOptions);
//    CircleOptions circleOptions=new CircleOptions();
//    circleOptions.center(new LatLng(-34, 151));
//    circleOptions.radius(2000);
//    circleOptions.strokeColor(ContextCompat.getColor(this,android.R.color.holo_orange_dark));
//    circleOptions.fillColor(ContextCompat.getColor(this,android.R.color.holo_orange_light));
//
//    googleMap.addCircle(circleOptions);
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
    CameraUpdate updatePosition = CameraUpdateFactory.newLatLng(latLngs.get(0));

    googleMap.moveCamera(updatePosition);

    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

    googleMap.animateCamera(zoom);

}
catch (Exception ex){

}
    }




}


