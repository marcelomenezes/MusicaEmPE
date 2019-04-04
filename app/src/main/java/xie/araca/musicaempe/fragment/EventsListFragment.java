package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.adapter.ArtistAdapter;
import xie.araca.musicaempe.adapter.EventAdapter;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.model.Event;
import xie.araca.musicaempe.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment {

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private EventAdapter adapter;
    private ArrayList<Event> listEvent = new ArrayList<>();
    private ArrayList<Event> listFiltered, loadList;
    private RecyclerView recyclerView;

    public EventsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        databaseReference = ConfigFirebase.getReferenceFirebase().child("events");

        recyclerView = view.findViewById(R.id.recycle_events_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new EventAdapter(listEvent, getActivity());
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getEvents();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }

    public void getEvents() {
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Event event = data.getValue(Event.class);
                    listEvent.add(event);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void search(String s) {
        if (s == null || s.trim().equals("")) {
            clearSearch();
            return;
        }else {
            getEvents();
            List<Event> eventsFound = new ArrayList<>(listEvent);
                if (eventsFound.size() > 0) {
                    for (Event event : listEvent) {
                        String name = event.getNameEvent();
                        if (!name.contains(s))
                            eventsFound.remove(event);
                     }
                }
                adapter = new EventAdapter(eventsFound, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }
    public void clearSearch(){

    }
}
