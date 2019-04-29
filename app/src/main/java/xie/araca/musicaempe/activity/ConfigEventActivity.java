package xie.araca.musicaempe.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ContentConfigEventBinding;
import xie.araca.musicaempe.R;
import xie.araca.musicaempe.helper.Permission;
import xie.araca.musicaempe.model.Event;

public class ConfigEventActivity extends AppCompatActivity {

    private ContentConfigEventBinding binding;
    private Event event;

    private StorageReference storageReference;

    private String[] permissionsNeed = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        Button btSave = findViewById(R.id.bt_save_event);
        final EditText edNameEvent = findViewById(R.id.edittext_config_name_event);
        final EditText edDetailsEvent = findViewById(R.id.edittext_config_intro_event);

        ImageButton btChoosePicture = findViewById(R.id.imgbt_gallery_event);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new Event();
                //event.setEndDate(binding.textEndDateEvent.getText().toString());
               // event.setStartDate(binding.textStartDateEvent.getText().toString());
                //event.setNameEvent(binding.textConfigNameEvent.getText().toString());
                //event.setDetailsEvent(binding.textConfigIntroEvent.getText().toString());
                event.setNameEvent(edNameEvent.getText().toString());
                event.setDetailsEvent(edDetailsEvent.getText().toString());
                saveEvent();
            }
        });


        btChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();


    }

    private void saveEvent(){
        storageReference = ConfigFirebase.getStorageReference();
        StorageReference storage = storageReference
                .child("events")
                .child(event.getId());

        event.save();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("onActivityResult", "onActivityResult");

        //Testar processor de retorno dos dados
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {


        }
    }
}
