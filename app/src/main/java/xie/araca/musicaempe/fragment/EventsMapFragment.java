package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import xie.araca.musicaempe.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsMapFragment extends Fragment implements OnMapReadyCallback {


    public EventsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_map, container, false);

        //SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
        //        .findFragmentById(R.id.map);
       // mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap){

    }
}
