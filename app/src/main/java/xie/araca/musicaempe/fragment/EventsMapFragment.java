package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xie.araca.musicaempe.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsMapFragment extends Fragment {


    public EventsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events_map, container, false);
    }

}
