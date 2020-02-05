package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.model.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsMapFragment extends Fragment implements OnMapReadyCallback {

    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;
    private GoogleMap mMap;
    public EventsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_map, container, false);
        databaseReference = ConfigFirebase.getReferenceFirebase().child("events");

        //SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
        //        .findFragmentById(R.id.map);
       // mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Event event = data.getValue(Event.class);
                    double latitude = Double.parseDouble(event.getLatitude());
                    double longitude = Double.parseDouble(event.getLongitude());
                    LatLng location = new LatLng(latitude, longitude);

                    mMap.addMarker( new MarkerOptions().position(location).title(event.getNameEvent()));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        getMapEvents();
    }

    public void getMapEvents(){

    }
}
