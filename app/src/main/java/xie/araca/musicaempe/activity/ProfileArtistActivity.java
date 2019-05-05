package xie.araca.musicaempe.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.model.User;

public class ProfileArtistActivity extends AppCompatActivity {
    private User artistSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_artist);

        //Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_artist);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            artistSelected = (User) bundle.getSerializable("artistSelected");

        }
        getSupportActionBar().setTitle(artistSelected.getNameUser());



    }

}
