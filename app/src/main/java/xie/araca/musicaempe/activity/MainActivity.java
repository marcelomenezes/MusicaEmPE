package xie.araca.musicaempe.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ActivityMainBinding;

import xie.araca.musicaempe.databinding.ContentMainBinding;
import xie.araca.musicaempe.fragment.ArtistsFragment;
import xie.araca.musicaempe.fragment.EventsFragment;
import xie.araca.musicaempe.fragment.EventsListFragment;
import xie.araca.musicaempe.fragment.ExploreFragment;
import xie.araca.musicaempe.fragment.FavoriteFragment;
import xie.araca.musicaempe.fragment.HomeFragment;
import xie.araca.musicaempe.helper.UserFirebase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener{


    private HomeFragment fragmentHome = new HomeFragment();
    private EventsFragment fragmentEvents = new EventsFragment();
    private ArtistsFragment fragmentArtists = new ArtistsFragment();
    private ExploreFragment fragmentExplore = new ExploreFragment();
    private FavoriteFragment fragmentFavorite = new FavoriteFragment();
    private FragmentManager fm = getSupportFragmentManager();
    private Fragment active = fragmentHome;
    private ActivityMainBinding binding;
    private ContentMainBinding contentBind;

    private ViewPager viewPager;
    private EventsListFragment fragmentEventsList = new EventsListFragment();
    private Fragment fragmentEventsMap;

    private FirebaseAuth firebaseAuth;

    private String currentUser = UserFirebase.getCurrentUserId();

    private String nameUser;
    private String type;
    private String city = "";
    private String neighborhood;
    private String intro;
    private String rythm;
    private String email;


    private String typeValidate;

    private StorageReference storageReference;
    private String currentUserId;

    private GeofencingClient geofencingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView headernameUser = navHeaderView.findViewById(R.id.text_header_name);
        ImageView imageView = navHeaderView.findViewById(R.id.imageView_header);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragmentArtists, "2").hide(fragmentArtists).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentEvents, "3").hide(fragmentEvents).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentExplore, "4").hide(fragmentExplore).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentFavorite, "5").hide(fragmentFavorite).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentHome, "1").commit();

        storageReference = ConfigFirebase.getStorageReference();
        currentUserId = UserFirebase.getCurrentUserId();




        FirebaseUser user = UserFirebase.getCurrentUser();

        headernameUser.setText(UserFirebase.getCurrentUserDisplayName());

        geofencingClient = LocationServices.getGeofencingClient(this);



        Uri uri = user.getPhotoUrl();
        if (uri != null){
            Picasso.with(this)
                    .load(uri)
                    .into(imageView);
        }else{
            imageView.setImageResource(R.drawable.ic_action_user);
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragmentHome).commit();
                    active = fragmentHome;
                    return true;
                case R.id.navigation_artists:
                    fm.beginTransaction().hide(active).show(fragmentArtists).commit();
                    active = fragmentArtists;
                    return true;
                case R.id.navigation_events:
                    fm.beginTransaction().hide(active).show(fragmentEvents).commit();
                    active = fragmentEvents;
                    return true;
                case R.id.navigation_explore:
                    fm.beginTransaction().hide(active).show(fragmentExplore).commit();
                    active = fragmentExplore;
                    return true;
                case R.id.navigation_favorite:
                    fm.beginTransaction().hide(active).show(fragmentFavorite).commit();
                    active = fragmentFavorite;
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();

        Intent intent = getIntent();
        type = intent.getStringExtra("tipo");
        if ( type != null) {

            if (type.equals("Artista"))
                inflater.inflate(R.menu.main, menu);
            else if (type.equals("Produtor(a)"))
                inflater.inflate(R.menu.main, menu);
            else
                inflater.inflate(R.menu.main_user, menu);
        }else{
            SharedPreferences preferences = getSharedPreferences("type", MODE_PRIVATE);
            type = preferences.getString("tipo", null);

            if (type.equals("Artista"))
                inflater.inflate(R.menu.main, menu);
            else if (type.equals("Produtor(a)"))
                inflater.inflate(R.menu.main, menu);
            else if(type.equals("Usuário(a)"))
                inflater.inflate(R.menu.main_user, menu);

        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.hint_search));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search){
            return true;
        }
        else if (id == R.id.action_add_event) {
            addEvent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_config) {
            getValueConfig();

        } else if(id == R.id.nav_logout){

            firebaseAuth = ConfigFirebase.getFirebaseAuth();
            firebaseAuth.signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addEvent(){
        Intent intent = new Intent(this, ConfigEventActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        fragmentArtists.search(s);
        //fragmentEventsList.search(s);
        fragmentEvents.search(s);
        Log.d("evento", "hello");
        return true;
    }
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true; // para expandir a view
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        fragmentArtists.clearSearch();
        //fragmentEventsList.clearSearch();
        fragmentEvents.clearSearch();
        return true; // para voltar ao normal
    }

    public void getValueConfig(){
        DatabaseReference databaseReference = ConfigFirebase.getReferenceFirebase();
        DatabaseReference data = databaseReference.child("users").child(currentUser);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameUser = dataSnapshot.child("nameUser").getValue().toString();
                type = dataSnapshot.child("type").getValue().toString();
                city = dataSnapshot.child("city").getValue().toString();
                neighborhood = dataSnapshot.child("neighborhood").getValue().toString();
                intro = dataSnapshot.child("intro").getValue().toString();
                rythm = dataSnapshot.child("rythm").getValue().toString();

                if (type.equals("Artista")){
                    Intent intent = new Intent(MainActivity.this, ConfigArtistActivity.class);
                    intent.putExtra("nameUser", nameUser);
                    intent.putExtra("city", city);
                    intent.putExtra("neighborhood", neighborhood);
                    intent.putExtra("intro", intro);
                    intent.putExtra("rythm", rythm);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, ConfigUserActivity.class);
                    intent.putExtra("city", city);
                    intent.putExtra("neighborhood", neighborhood);
                    intent.putExtra("intro", intro);
                    intent.putExtra("nameUser", nameUser);
                    intent.putExtra("rythm", rythm);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}