package xie.araca.musicaempe.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import xie.araca.musicaempe.fragment.EventsListFragment;
import xie.araca.musicaempe.fragment.EventsMapFragment;

public class EventsTabsAdapter extends FragmentStatePagerAdapter{

    private ArrayList<Fragment> fragmentsUsed;

    public EventsTabsAdapter(FragmentManager fm){
        super(fm);
        EventsListFragment eventsList = new EventsListFragment();
        EventsMapFragment eventsMap = new EventsMapFragment();
        this.fragmentsUsed = new ArrayList<>();
        fragmentsUsed.add(eventsList);
        fragmentsUsed.add(eventsMap);

    }
    public Fragment getFragment(Integer indice){
        return fragmentsUsed.get(indice);
    }

    @Override    public Fragment getItem(int position) {

        return fragmentsUsed.get(position);
    }
    @Override
    public int getCount() {
        return 2;
    }
    @Override    public CharSequence getPageTitle(int position) {        switch (position){
        case 0: return "Eventos";
        case 1: return "Mapa de Eventos";
        default: return null;
    }

    }
}
