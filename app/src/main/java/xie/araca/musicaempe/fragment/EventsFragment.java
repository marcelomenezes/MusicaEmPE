package xie.araca.musicaempe.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.activity.MainActivity;
import xie.araca.musicaempe.activity.ProfileArtistActivity;
import xie.araca.musicaempe.activity.ProfileEventActivity;
import xie.araca.musicaempe.adapter.EventAdapter;
import xie.araca.musicaempe.adapter.EventsTabsAdapter;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.RecyclerItemClickListener;
import xie.araca.musicaempe.model.Event;
import xie.araca.musicaempe.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private EventAdapter adapter, returned;
    private ArrayList<Event> listEvent = new ArrayList<>();
    private RecyclerView recyclerView;
    private List<Event> loaded;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_events, container, false);
        /*
        EventsTabsAdapter eventsTabsAdapter = new EventsTabsAdapter(getChildFragmentManager());
        ViewPager viewPager = view.findViewById(R.id.pager_events);
        viewPager.setAdapter(eventsTabsAdapter);
        */

        recyclerView = view.findViewById(R.id.recycle_events);
        databaseReference = ConfigFirebase.getReferenceFirebase().child("events");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new EventAdapter(listEvent, getActivity());
        recyclerView.setAdapter(adapter);
        getEvents();

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(
                getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Event eventSelected = listEvent.get(position);
                        Intent intent = new Intent(getActivity(), ProfileEventActivity.class);
                        intent.putExtra("eventSelected", eventSelected);
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
        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }
    @Override
    public void onRefresh(){
        getEvents();
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
            List<Event> eventsFound = new ArrayList<>(listEvent);
            if (eventsFound.size() > 0) {
                for (Event event : listEvent) {
                    String name = event.getNameEvent().toUpperCase();
                    if (!name.contains(s.toUpperCase()))
                        eventsFound.remove(event);
                }
            }
            loaded = new ArrayList<>();
            loaded.addAll(eventsFound);


        }
        adapter = new EventAdapter(loaded, getActivity());
        recyclerView.setAdapter(adapter);
    }
    public void clearSearch(){
        adapter = new EventAdapter(listEvent, getActivity());
        recyclerView.setAdapter(adapter);
    }
}
