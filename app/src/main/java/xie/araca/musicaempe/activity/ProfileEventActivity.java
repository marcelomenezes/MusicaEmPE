package xie.araca.musicaempe.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import xie.araca.musicaempe.R;
import xie.araca.musicaempe.model.Event;

public class ProfileEventActivity extends AppCompatActivity {


    private TextView startEventText;
    private TextView endEventText;
    private TextView detailsEventText;
    private TextView addressEventText;
    private TextView nameEventText;
    private CircleImageView imageEvent;

    private Event eventSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_event);

        //Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_event);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        startEventText = findViewById(R.id.text_de_data_evento_view);
        endEventText = findViewById(R.id.text_ate_data_evento_view);
        detailsEventText = findViewById(R.id.text_detalhes_evento);
        addressEventText = findViewById(R.id.text_endereco_evento);
        imageEvent = findViewById(R.id.image_profile_event);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            eventSelected = (Event) bundle.getSerializable("eventSelected");

        }
        getSupportActionBar().setTitle(eventSelected.getNameEvent());


        startEventText.setText(eventSelected.getStartDate());
        endEventText.setText(eventSelected.getEndDate());
        detailsEventText.setText(eventSelected.getDetailsEvent());
        addressEventText.setText(eventSelected.getAddressEvent());

        Glide.with(ProfileEventActivity.this)
                .load(eventSelected.getPhotoEvent())
                .into(imageEvent);

    }

}
