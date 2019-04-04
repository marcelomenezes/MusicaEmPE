package xie.araca.musicaempe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ListArtistsBinding;
import xie.araca.musicaempe.holder.ArtistHolder;
import xie.araca.musicaempe.model.User;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistHolder>{
    private final List<User> mUsers;

    private DatabaseReference firebase;
    private Context context;


    public ArtistAdapter(List<User> users, Context c) {

        this.mUsers = users;
        this.context = c;
    }

    @Override
    public ArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listArtist = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_artists, parent, false);

        return new ArtistHolder(listArtist);
    }

    @Override
    public void onBindViewHolder(ArtistHolder holder, int position) {
        //firebase = ConfigFirebase.getReferenceFirebase()
          //      .child("users");


        User user = mUsers.get(position);
        holder.artistName.setText(user.getNameUser());
        holder.artistCity.setText(user.getEmail());
        //holder.artistName.setText(firebase.child("username").toString());
        //holder.artistCity.setText(firebase.child("email").toString());
        //holder.artistPicture

    }

    @Override
    public int getItemCount() {

        return mUsers.size();
        //return mUsers != null ? mUsers.size() : 0;
    }

    public void updateList(User user) {
        insertItem(user);
    }

    // Método responsável por inserir um novo usuário na lista
    //e notificar que há novos itens.
    private void insertItem(User user) {
        mUsers.add(user);
        notifyItemInserted(getItemCount());
    }

    private void updateItem(int position) {
        User userModel = mUsers.get(position);
        notifyItemChanged(position);
    }
}
