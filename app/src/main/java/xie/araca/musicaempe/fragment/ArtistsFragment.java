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

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.adapter.ArtistAdapter;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsFragment extends Fragment {

    private ArtistAdapter adapter;
    private ArrayList<User> listArtist = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    public ArtistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        databaseReference = ConfigFirebase.getReferenceFirebase().child("users");

        RecyclerView recyclerView = view.findViewById(R.id.recycle_artists_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new ArtistAdapter(listArtist, getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getArtists();
    }

    @Override
    public void onStop(){
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }

    public void getArtists(){
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    User user = dados.getValue(User.class);
                    listArtist.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
