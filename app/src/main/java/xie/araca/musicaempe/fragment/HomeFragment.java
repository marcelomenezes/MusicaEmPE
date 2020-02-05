package xie.araca.musicaempe.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.activity.ProfileArtistActivity;
import xie.araca.musicaempe.activity.ProfileEventActivity;
import xie.araca.musicaempe.adapter.ArtistHoriAdapter;
import xie.araca.musicaempe.adapter.EventHoriAdapter;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.RecyclerItemClickListener;
import xie.araca.musicaempe.model.Event;
import xie.araca.musicaempe.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView, recyclerViewEvents;
    private ArtistHoriAdapter adapter;
    private EventHoriAdapter adapterEvent;

    private DatabaseReference databaseReference, databaseReferenceEvents;
    private ArrayList<User> listArtist = new ArrayList<>();
    private ArrayList<Event> listEvent = new ArrayList<>();
    private ValueEventListener valueEventListener, valueEventListenerEvent;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Recyclerview para lista de artistas horizontais
        recyclerView = view.findViewById(R.id.recycle_artists_list_horizontal);
        databaseReference = ConfigFirebase.getReferenceFirebase().child("users");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ArtistHoriAdapter(listArtist, getActivity());
        recyclerView.setAdapter(adapter);
        getArtists();

        //Recyclerview para lista de eventos horizontais
        recyclerViewEvents = view.findViewById(R.id.recycle_events_list_horizontal);
        databaseReferenceEvents = ConfigFirebase.getReferenceFirebase().child("events");
        LinearLayoutManager layoutManagerEvent = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewEvents.setLayoutManager(layoutManagerEvent);
        recyclerViewEvents.setHasFixedSize(true);
        adapterEvent = new EventHoriAdapter(listEvent, getActivity());
        recyclerViewEvents.setAdapter(adapterEvent);
        getEvents();


        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(
                getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User artistSelected = listArtist.get(position);
                        Intent intent = new Intent(getActivity(), ProfileArtistActivity.class);
                        intent.putExtra("artistSelected", artistSelected);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        recyclerViewEvents.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewEvents,
                new RecyclerItemClickListener.OnItemClickListener(){
                    @Override
                    public void onItemClick(View view, int position){
                        Event eventSelected = listEvent.get(position);
                        Intent intent = new Intent(getActivity(), ProfileEventActivity.class);
                        intent.putExtra("eventSelected", eventSelected);
                        startActivity(intent);
                    }
                    @Override
                    public void onLongItemClick(View view, int position){

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                    }
                }
        ));
        return view;
    }

    @Override
    public void onStop(){
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
        databaseReferenceEvents.removeEventListener(valueEventListenerEvent);
    }
    @Override
    public void onRefresh(){
        getArtists();
        getEvents();
    }



    public void getArtists(){
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    User user = data.getValue(User.class);
                    if (user.getType().equals( "Artista"))
                        listArtist.add(user);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getEvents() {
        valueEventListenerEvent = databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Event event = data.getValue(Event.class);
                    listEvent.add(event);
                }
                adapterEvent.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
