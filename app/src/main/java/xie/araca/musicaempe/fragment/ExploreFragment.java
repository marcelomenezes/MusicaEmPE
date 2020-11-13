package xie.araca.musicaempe.fragment;


import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.GeofenceHelper;
import xie.araca.musicaempe.helper.Permission;
import xie.araca.musicaempe.model.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "ExploreFragment";

    private SupportMapFragment supportMapFragment;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;
    private GoogleMap mMap;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private String GEOFENCE_ID;

    private float GEOFENCE_RADIUS = 200;

    public ExploreFragment() {
        // Required empty public constructor
    }

    private String[] permissionsNeed = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
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

        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceHelper = new GeofenceHelper(getContext());

        validatePermission();
        return view;


    }


    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        enableUserLocation();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Event event = data.getValue(Event.class);
                    double latitude = Double.parseDouble(event.getLatitude());
                    double longitude = Double.parseDouble(event.getLongitude());
                    LatLng location = new LatLng(latitude, longitude);

                    GEOFENCE_ID = event.getId();

                    mMap.addMarker( new MarkerOptions().position(location).title(event.getNameEvent()));
                    addCircle(location, GEOFENCE_RADIUS);
                    addGeofence(location, GEOFENCE_RADIUS);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                        //mMap.setMyLocationEnabled(true);
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mMap != null) {
                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        // Permission to access the location is missing. Show rationale and request permission
                       Toast.makeText(getContext(),"Aceita pai", Toast.LENGTH_SHORT).show();
                       validatePermission();
                    }

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

    public void enableUserLocation(){


    }

    private void addGeofence(LatLng latLng, float radius){

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius,Geofence.
                GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geo Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure" + errorMessage);
                    }
                });
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

}
