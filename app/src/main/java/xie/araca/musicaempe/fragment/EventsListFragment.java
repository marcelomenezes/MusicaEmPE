package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import xie.araca.musicaempe.adapter.EventAdapter;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.model.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private EventAdapter adapter, returned;
    private ArrayList<Event> listEvent = new ArrayList<>();
    private RecyclerView recyclerView;
    private List<Event> loaded;

    public EventsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        recyclerView = view.findViewById(R.id.recycle_events_list);
        databaseReference = ConfigFirebase.getReferenceFirebase().child("events");
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
