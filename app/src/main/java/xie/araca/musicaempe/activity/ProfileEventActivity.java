package xie.araca.musicaempe.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.model.Event;

public class ProfileEventActivity extends AppCompatActivity {

    private Event eventSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_event);

        //Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_event);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            eventSelected = (Event) bundle.getSerializable("eventSelected");

        }
        getSupportActionBar().setTitle(eventSelected.getNameEvent());


    }

}
