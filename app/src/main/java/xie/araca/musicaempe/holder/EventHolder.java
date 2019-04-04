package xie.araca.musicaempe.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.databinding.ListArtistsBinding;

public class EventHolder extends RecyclerView.ViewHolder{


    private ListArtistsBinding binding;

    public TextView eventName;
    public TextView eventCity;
    public ImageView eventPicture;

    public EventHolder(View itemView){
        super(itemView);
        eventName = itemView.findViewById(R.id.text_event);
        eventCity = itemView.findViewById(R.id.text_city_event);
        eventPicture = itemView.findViewById(R.id.icon_artist);
    }
}
