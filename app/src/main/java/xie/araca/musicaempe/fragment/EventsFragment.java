package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.activity.MainActivity;
import xie.araca.musicaempe.adapter.EventsTabsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    private View view;
    private ViewPager viewPager;

    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_events, container, false);
        EventsTabsAdapter eventsTabsAdapter = new EventsTabsAdapter(getChildFragmentManager());
        viewPager = view.findViewById(R.id.pager_events);
        viewPager.setAdapter(eventsTabsAdapter);
        return view;
    }

}
