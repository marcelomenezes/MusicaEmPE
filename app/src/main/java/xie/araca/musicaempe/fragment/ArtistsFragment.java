package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.adapter.ArtistAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsFragment extends Fragment {

    private ArtistAdapter adapter;


    public ArtistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycle_artists_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArtistAdapter(new ArrayList<>(0));
        recyclerView.setAdapter(adapter);
        return view;
    }

}
