package xie.araca.musicaempe.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.holder.EventHolder;
import xie.araca.musicaempe.model.Event;

public class EventHoriAdapter extends RecyclerView.Adapter<EventHolder>{

    private final List<Event> mEvents;

    private DatabaseReference firebase;
    private Context context;


    public EventHoriAdapter(List<Event> events, Context c) {

        this.mEvents = events;
        this.context = c;
    }
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View view = null;


        ViewHolder holder = null;

       // Verifica se não tem view criada

        if( view == null) {

            //Inicializa o objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Monta a partir do xml
            view = inflater.inflate(R.layout.list_events, parent, false);
            holder = new ViewHolder();


            //Recupera os elementos para exibição
            holder.eventName =  view.findViewById(R.id.text_event);
            holder.eventCity =  view.findViewById(R.id.text_city_event);



            //verifica se existe eventos listados
            if (mEvents.size() > 0) {


                //Configurar TextView para exibir os eventos
                Event event = mEvents.get(position);
                holder.eventName.setText(event.getNameEvent());
                holder.eventCity.setText(event.getDetailsEvent());


            }
        }else{
            view = convertView;
        }


        return view;
    }

    static  class ViewHolder{
        TextView eventName;
        TextView eventCity;

        ImageView imagemEvento;
    }
*/

    //TODO in case of making a search inside tabsadapter

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listEvent = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_event_horizontal, parent, false);

        return new EventHolder(listEvent);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        //firebase = ConfigFirebase.getReferenceFirebase()
        //      .child("users");


        Event event = mEvents.get(position);
        holder.eventName.setText(event.getNameEvent());
        holder.eventCity.setText(event.getDetailsEvent());
        if (event.getPhotoEvent() != null ){
            Uri uri = Uri.parse(event.getPhotoEvent());
            Glide.with(context)
                    .load(uri)
                    .into(holder.eventPicture);
        }else {
            holder.eventPicture.setImageResource(R.drawable.user);
        }
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
