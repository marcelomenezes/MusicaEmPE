package xie.araca.musicaempe.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.adapter.EventsTabsAdapter;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ActivityMainBinding;

import xie.araca.musicaempe.databinding.ContentMainBinding;
import xie.araca.musicaempe.fragment.ArtistsFragment;
import xie.araca.musicaempe.fragment.EventsFragment;
import xie.araca.musicaempe.fragment.EventsListFragment;
import xie.araca.musicaempe.fragment.ExploreFragment;
import xie.araca.musicaempe.fragment.FavoriteFragment;
import xie.araca.musicaempe.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragmentHome = new HomeFragment();
    private Fragment fragmentEvents = new EventsFragment();
    private Fragment fragmentArtists = new ArtistsFragment();
    private Fragment fragmentExplore = new ExploreFragment();
    private Fragment fragmentFavorite = new FavoriteFragment();
    private FragmentManager fm = getSupportFragmentManager();
    private Fragment active = fragmentHome;
    private ActivityMainBinding binding;
    private ContentMainBinding contentBind;

    private ViewPager viewPager;
    private Fragment fragmentEventsList;
    private Fragment fragmentEventsMap;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragmentArtists, "2").hide(fragmentArtists).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentEvents, "3").hide(fragmentEvents).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentExplore, "4").hide(fragmentExplore).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentFavorite, "5").hide(fragmentFavorite).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentHome, "1").commit();
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
                    fm.beginTransaction().hide(active).show(fragmentArtists).commit();
                    active = fragmentArtists;
                    return true;
                case R.id.navigation_favorite:
                    fm.beginTransaction().hide(active).show(fragmentEvents).commit();
                    active = fragmentEvents;
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_event) {
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

        } else if (id == R.id.nav_send) {

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
}
