package xie.araca.musicaempe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
public class ArtistsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArtistAdapter adapter, returned;
    private List<User> loaded;
    private ArrayList<User> listArtist = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;

    public ArtistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);


        recyclerView = view.findViewById(R.id.recycle_artists_list);
        databaseReference = ConfigFirebase.getReferenceFirebase().child("users");
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

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onRefresh(){
        getArtists();
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
    public void search(String s) {
        if (s == null || s.trim().equals("")) {
            clearSearch();
            return;
        }else {
            List<User> usersFound = new ArrayList<>(listArtist);
            if (usersFound.size() > 0) {
                for (User user : listArtist) {
                    String name = user.getNameUser().toUpperCase();
                    if (!name.contains(s.toUpperCase()))
                        usersFound.remove(user);
                }
            }
            loaded = new ArrayList<>();
            loaded.addAll(usersFound);


        }

        returned = new ArtistAdapter(loaded, getActivity());
        recyclerView.setAdapter(returned);
        returned.notifyDataSetChanged();
    }
    public void clearSearch(){
        returned = new ArtistAdapter(listArtist, getActivity());
        recyclerView.setAdapter(returned);
    }

}
