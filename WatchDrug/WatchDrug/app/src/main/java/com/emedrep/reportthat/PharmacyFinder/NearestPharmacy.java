package com.emedrep.reportthat.PharmacyFinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.emedrep.reportthat.Adapter.PharmacyAdapter;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.Pharmacy;
import com.emedrep.watchdrug.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * create an instance of this fragment.
 */
public class NearestPharmacy extends Fragment {
    ListView listViewPharmacy;
    List<Pharmacy> listPharmacy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearest_pharmacy, container, false);
       // getActivity().setTitle("Closest Pharmacies");
         listViewPharmacy=(ListView)view.findViewById(R.id.listViewPharmacy);
         listPharmacy= Utilities.getClosestPharmacies(getActivity());
           if(listPharmacy!=null) {
               PharmacyAdapter pharmacyAdapter = new PharmacyAdapter(getActivity(), R.layout.cell_pharmacy_listview, listPharmacy);
               listViewPharmacy.setAdapter(pharmacyAdapter);
               listViewPharmacy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      // Pharmacy pharmacy=(Pharmacy)parent.getSelectedItem();
//                       Utilities.StartActivity(getActivity(),PharmacyDetails.class);
                   }
               });

           }
        return view;
    }
}
