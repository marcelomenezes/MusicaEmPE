package xie.araca.musicaempe.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.databinding.ListArtistsBinding;

public class ArtistHolder extends RecyclerView.ViewHolder{

    private ListArtistsBinding binding;

    public TextView artistName;
    public TextView artistCity;
    public ImageView artistPicture;

    public ArtistHolder(View itemView){
        super(itemView);
        artistName = itemView.findViewById(R.id.text_artists);
        artistCity = itemView.findViewById(R.id.text_city);
        artistPicture = itemView.findViewById(R.id.icon_artist);
    }
}
