package xie.araca.musicaempe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.holder.ArtistHolder;
import xie.araca.musicaempe.holder.EventHolder;
import xie.araca.musicaempe.model.Event;
import xie.araca.musicaempe.model.User;

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

    private final List<Event> mEvents;

    private DatabaseReference firebase;
    private Context context;


    public EventAdapter(List<Event> events, Context c) {

        this.mEvents = events;
        this.context = c;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listEvent = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_events, parent, false);

        return new EventHolder(listEvent);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        //firebase = ConfigFirebase.getReferenceFirebase()
        //      .child("users");


        Event event = mEvents.get(position);
        holder.eventName.setText(event.getNameEvent());
        holder.eventCity.setText(event.getDetailsEvent());
        //holder.artistName.setText(firebase.child("username").toString());
        //holder.artistCity.setText(firebase.child("email").toString());
        //holder.artistPicture

    }

    @Override
    public int getItemCount() {

        return mEvents.size();
        //return mUsers != null ? mUsers.size() : 0;
    }

    public void updateList(Event event) {
        insertItem(event);
    }

    // Método responsável por inserir um novo usuário na lista
    //e notificar que há novos itens.
    private void insertItem(Event event) {
        mEvents.add(event);
        notifyItemInserted(getItemCount());
    }

    private void updateItem(int position) {
        Event eventModel = mEvents.get(position);
        notifyItemChanged(position);
    }
}

