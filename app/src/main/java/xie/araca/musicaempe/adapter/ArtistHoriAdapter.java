package xie.araca.musicaempe.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.holder.ArtistHolder;
import xie.araca.musicaempe.model.User;

public class ArtistHoriAdapter extends RecyclerView.Adapter<ArtistHolder>{

    private final List<User> mUsers;

    private DatabaseReference firebase;
    private Context context;


    public ArtistHoriAdapter(List<User> users, Context c) {

        this.mUsers = users;
        this.context = c;
    }

    @Override
    public ArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listArtist = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_artist_horizontal, parent, false);

        return new ArtistHolder(listArtist);
    }

    @Override
    public void onBindViewHolder(ArtistHolder holder, int position) {
        //firebase = ConfigFirebase.getReferenceFirebase()
        //      .child("users");

        User user = mUsers.get(position);
        holder.artistName.setText(user.getNameUser());
        holder.artistCity.setText(user.getCity());

        if (user.getPhoto() != null ){
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context)
                    .load(uri)
                    .into(holder.artistPicture);
        }else {
            holder.artistPicture.setImageResource(R.drawable.user);
        }
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