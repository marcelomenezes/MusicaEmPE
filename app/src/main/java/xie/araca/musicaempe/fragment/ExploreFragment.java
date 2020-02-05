package xie.araca.musicaempe.fragment;


import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.Permission;
import xie.araca.musicaempe.model.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private SupportMapFragment supportMapFragment;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;
    private GoogleMap mMap;

    public ExploreFragment() {
        // Required empty public constructor
    }

    private String[] permissionsNeed = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        databaseReference = ConfigFirebase.getReferenceFirebase().child("events");



        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
                    /*new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng latLng = new LatLng(1.289545, 103.849972);
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Singapore"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            });*/
        }

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, supportMapFragment).commit();


        validatePermission();
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
                    //mMap.setMyLocationEnabled(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location){

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21 ));

    }

    @Override
    public void onStatusChanged(String string, int i, Bundle bundle){

    }

    @Override
    public void onProviderEnabled(String s){

    }

    @Override
    public void onProviderDisabled(String s){

    }

    public void validatePermission(){
        Permission.validatePermission(permissionsNeed, getActivity(), 1);
    }

}
