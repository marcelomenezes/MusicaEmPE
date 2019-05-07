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
import xie.araca.musicaempe.model.User;

public class ProfileArtistActivity extends AppCompatActivity {
    private User artistSelected;

    private TextView cityText;
    private TextView neighbourhoodText;
    private TextView introText;
    private TextView rythmText;
    private TextView socialText;
    private CircleImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_artist);

        //Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_artist);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        cityText = findViewById(R.id.text_city_profile);
        //neighbourhoodText = findViewById(R.id.text_nei)
        introText = findViewById(R.id.text_intro_profile);
        rythmText = findViewById(R.id.text_list_rythms);
        socialText = findViewById(R.id.list_social);
        profilePicture = findViewById(R.id.image_profile_artist);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            artistSelected = (User) bundle.getSerializable("artistSelected");


        }
        cityText.setText(artistSelected.getCity());
        introText.setText(artistSelected.getIntro());
        rythmText.setText(artistSelected.getRythm());

        Glide.with(ProfileArtistActivity.this)
                .load(artistSelected.getPhoto())
                .into(profilePicture);

        getSupportActionBar().setTitle(artistSelected.getNameUser());
    }

}
