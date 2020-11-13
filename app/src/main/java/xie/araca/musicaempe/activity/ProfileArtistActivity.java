package xie.araca.musicaempe.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.UserFirebase;
import xie.araca.musicaempe.model.User;

public class ProfileArtistActivity extends AppCompatActivity {
    private User artistSelected;
    private User userLoggedIn;

    private TextView cityText;
    private TextView neighbourhoodText;
    private TextView introText;
    private TextView rythmText;
    private TextView socialText;
    private CircleImageView profilePicture;
    private Button btFollow;

    private DatabaseReference firebaseReference;
    private DatabaseReference followersReference;
    private DatabaseReference userRef;
    private DatabaseReference userLoggedInReference;
    private String idUserLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_artist);

        //Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_artist);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        firebaseReference = ConfigFirebase.getReferenceFirebase();
        followersReference = firebaseReference.child("followers");
        userRef = firebaseReference.child("users");
        idUserLoggedIn = UserFirebase.getCurrentUserId();

        cityText = findViewById(R.id.text_city_profile);
        //neighbourhoodText = findViewById(R.id.text_nei)
        introText = findViewById(R.id.text_intro_profile);
        rythmText = findViewById(R.id.text_list_rythms);
        socialText = findViewById(R.id.list_social);
        profilePicture = findViewById(R.id.image_profile_artist);
        btFollow = findViewById(R.id.bt_follow_profile);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            artistSelected = (User) bundle.getSerializable("artistSelected");


        }
        cityText.setText(artistSelected.getCity());
        introText.setText(artistSelected.getIntro());
        rythmText.setText(artistSelected.getRythm());
        btFollow.setText("Carregando");

        Glide.with(ProfileArtistActivity.this)
                .load(artistSelected.getPhoto())
                .into(profilePicture);

        getSupportActionBar().setTitle(artistSelected.getNameUser());

    }

    @Override
    public void onStart(){
        super.onStart();

        retrieveUserLoggedInData();
    }

    private void retrieveUserLoggedInData(){
        userLoggedInReference = userRef.child(idUserLoggedIn);
        userLoggedInReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userLoggedIn = dataSnapshot.getValue(User.class);

                verifyFollowArtist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void verifyFollowArtist(){

        DatabaseReference followerRef = followersReference
                .child(idUserLoggedIn)
                .child(artistSelected.getId());

        followerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    enableButtonFollow(true);
                }else{
                    enableButtonFollow(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enableButtonFollow(boolean followArtist){

        if(followArtist){
            btFollow.setText("Seguindo");
            btFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retrieveFollower(userLoggedIn, artistSelected);
                }
            });
        }
        else{
            btFollow.setText("Seguir");

            //saveFollower
            btFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    saveFollower(userLoggedIn, artistSelected);
                }
            });
        }
    }

    private void saveFollower(User uLogged, User artistToBeFollowed){

        HashMap<String, Object> dataArtist = new HashMap<>();
        dataArtist.put("id", artistToBeFollowed.getId());
        dataArtist.put("nameUser", artistToBeFollowed.getNameUser());
        dataArtist.put("photo", artistToBeFollowed.getPhoto());
        dataArtist.put("city", artistToBeFollowed.getCity());

        DatabaseReference followedRef = followersReference
                .child(uLogged.getId())
                .child(artistToBeFollowed.getId());
        followedRef.setValue(dataArtist);

        btFollow.setText("Seguindo");

        int following = uLogged.getNumberOfFollowing();
        HashMap<String, Object> dataFollowing = new HashMap<>();
        dataFollowing.put("numberOfFollowing", following + 1);
        DatabaseReference userFollowing = userRef
                .child(uLogged.getId());
        userFollowing.updateChildren(dataFollowing);

        int followers = artistToBeFollowed.getNumberOfFollowers();
        HashMap<String, Object> dataFollowers = new HashMap<>();
        dataFollowers.put("numberOfFollowers", followers + 1);
        DatabaseReference userFollowers = userRef
                .child(artistToBeFollowed.getId());
        userFollowers.updateChildren(dataFollowers);
    }

    private void retrieveFollower(User uLogged, User artistFollowed){

        DatabaseReference followRef = followersReference
                .child(uLogged.getId())
                .child(artistFollowed.getId());
        followRef.removeValue();

        btFollow.setText("Seguir");

        int followingRetrieve = uLogged.getNumberOfFollowing();
        HashMap<String, Object> dataFollowing = new HashMap<>();
        dataFollowing.put("numberOfFollowing", followingRetrieve-1);
        DatabaseReference userFollowing = userRef
                .child(uLogged.getId());
        userFollowing.updateChildren(dataFollowing);

        int followersRetrieve = artistFollowed.getNumberOfFollowing();
        HashMap<String, Object> dataFollowers = new HashMap<>();
        dataFollowers.put("numberOfFollowers", followersRetrieve-1);
        DatabaseReference userFollowers = userRef
                .child(artistFollowed.getId());
        userFollowers.updateChildren(dataFollowers);
    }

}
