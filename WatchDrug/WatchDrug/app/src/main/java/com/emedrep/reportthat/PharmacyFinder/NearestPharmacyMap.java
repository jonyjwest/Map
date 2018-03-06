package com.emedrep.reportthat.PharmacyFinder;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.LatLngBean;
import com.emedrep.reportthat.Model.Pharmacy;
import com.emedrep.watchdrug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * create an instance of this fragment.
 */


public class NearestPharmacyMap extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private ArrayList<LatLng> listLatLng;
    private RelativeLayout rlMapLayout;
    HashMap<Marker,LatLngBean> hashMapMarker = new HashMap<Marker,LatLngBean>();
    SupportMapFragment mapFragment;
    static SharedPreferences prefs;


    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(Constant.PREFERENCE_NAME, 0);
         view = inflater.inflate(R.layout.fragment_nearest_pharmacy_map, container, false);
        rlMapLayout=(RelativeLayout)view.findViewById(R.id.rlMapLayout);
       // getActivity().setTitle("Map");

        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    private void setData()
    {
         List<Pharmacy> pharmacyList= Utilities.getClosestPharmacies(getActivity());
        ArrayList<LatLngBean> arrayList=new ArrayList<>();
   if(pharmacyList!=null) {
       for (int i = 0; i < pharmacyList.size(); i++) {
           LatLngBean bean = new LatLngBean();
           bean.setTitle(pharmacyList.get(i).getPremiseName());
           bean.setSnippet("Phone:"+pharmacyList.get(i).getPharmacistPhone().trim()+" Address:"+pharmacyList.get(i).getPremiseAddress());

           bean.setLatitude(pharmacyList.get(i).getLatitude());
           bean.setLongitude(pharmacyList.get(i).getLongitude());
           arrayList.add(bean);
       }
   }

//        LatLngBean bean1=new LatLngBean();
//        bean1.setTitle("Viva Pharmacy & Stores");
//        bean1.setSnippet("Hello,Surat");
//        bean1.setLatitude("6.6342587");
//        bean1.setLongitude("3.3584768");
//        arrayList.add(bean1);
//
//        LatLngBean bean2=new LatLngBean();
//        bean2.setTitle("First Call Pharmacy");
//        bean2.setSnippet("Hello,Vadodara");
//        bean2.setLatitude("6.6173141");
//        bean2.setLongitude("3.3706523");
//        arrayList.add(bean2);

        LoadingGoogleMap(arrayList);
    }

    private void setUpMapIfNeeded()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());

        // Google Play Services are not available
        if(status!= ConnectionResult.SUCCESS)
        {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        }
        else
        {
            if (googleMap == null)
            {
               // googleMap = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();

                if (googleMap != null)
                {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                }
            }
        }
    }


    void LoadingGoogleMap(ArrayList<LatLngBean> arrayList)
    {
        if (googleMap != null)
        {
            googleMap.clear();
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            if(arrayList.size()>0)
            {
                try
                {
                    listLatLng=new ArrayList<LatLng>();
                    for (int i = 0; i < arrayList.size(); i++)
                    {
                        LatLngBean bean=arrayList.get(i);
                        if(bean.getLatitude().length()>0 && bean.getLongitude().length()>0)
                        {
                            double lat=Double.parseDouble(bean.getLatitude());
                            double lon=Double.parseDouble(bean.getLongitude());
//                         if(i==0){
//                             Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_man_01);
//                             Marker marker = googleMap.addMarker(new MarkerOptions()
//                                     .position(new LatLng(lat,lon))
//                                     .title(bean.getTitle())
//                                     .snippet(bean.getSnippet())
//                                     .icon(BitmapDescriptorFactory.fromBitmap(icon)));
//                             hashMapMarker.put(marker,bean);
//                         }
//                         else {

                             Marker marker = googleMap.addMarker(new MarkerOptions()
                                     .position(new LatLng(lat,lon))
                                     .title(bean.getTitle())
                                     .snippet(bean.getSnippet())
                                     .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                             hashMapMarker.put(marker,bean);
                       //  }


                            //Add Marker to Hashmap


                            //Set Zoom Level of Map pin
                            LatLng object=new LatLng(lat, lon);
                            listLatLng.add(object);
                        }
                    }
                    SetZoomlevel(listLatLng);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker position)
                    {
                        LatLngBean bean=hashMapMarker.get(position);
                        Toast.makeText(getActivity(), bean.getTitle(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

        else
        {
            Toast.makeText(getActivity(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * @author Hasmukh Bhadani
     * Set Zoom level all pin withing screen on GoogleMap
     */
    public void  SetZoomlevel(ArrayList<LatLng> listLatLng)
    {
        if (listLatLng != null && listLatLng.size() == 1)
        {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
        }
        else if (listLatLng != null && listLatLng.size() > 1)
        {
            final LatLngBounds.Builder builder = LatLngBounds.builder();
            for (int i = 0; i < listLatLng.size(); i++)
            {
                builder.include(listLatLng.get(i));
            }

            final ViewTreeObserver treeObserver = rlMapLayout.getViewTreeObserver();
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout()
                {
                    if(googleMap != null){
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),view.findViewById(R.id.map)
                                .getWidth(),view.findViewById(R.id.map).getHeight(), 80));
                        rlMapLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       this.googleMap = googleMap;

        setUpMapIfNeeded();
        setData();
    }
}

