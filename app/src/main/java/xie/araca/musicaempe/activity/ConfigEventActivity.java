package xie.araca.musicaempe.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ContentConfigEventBinding;
import xie.araca.musicaempe.R;
import xie.araca.musicaempe.helper.GeofenceHelper;
import xie.araca.musicaempe.helper.Permission;
import xie.araca.musicaempe.helper.UserFirebase;
import xie.araca.musicaempe.model.Event;
import xie.araca.musicaempe.model.Geo;

public class ConfigEventActivity extends AppCompatActivity implements View.OnClickListener{

    private ContentConfigEventBinding binding;
    private Event event;

    private StorageReference storageReference;
    public static final String TAG = MapFragment.class.getSimpleName();

    private Bitmap image = null;
    private byte[] imageData = null;
    private Uri uri;

    private static final int SELECT_CAMERA = 100;
    private static final int SELECT_GALLERY = 200;

    private String[] permissionsNeed = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private DatePickerDialog startDateEventDialog;
    private DatePickerDialog endDateEventDialog;
    private SimpleDateFormat dateFormat;

    private EditText startDateEventEdt;
    private EditText endDateEventEdt;
    private EditText configNameEventEdt;
    private EditText configIntroEventEdt;
    private ImageView imageView;
    private ImageButton cameraBT;
    private ImageButton galleryBT;

    public LatLng newlatlng;
    private CharSequence saved;
    private Double l1;
    private Double l2;
    private String coordl1;
    private String coordl2;
    private int confirmedPresence = 0;
    //private String latitude;
    //private String longitude;

    private float GEOFENCE_RADIUS = 200;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private Geo geo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        Button btSave = findViewById(R.id.bt_save_event);
        final EditText edNameEvent = findViewById(R.id.edittext_config_name_event);
        final EditText edDetailsEvent = findViewById(R.id.edittext_config_intro_event);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        startDateEventEdt = findViewById(R.id.edittext_start_date_event);
        endDateEventEdt = findViewById(R.id.edittext_end_date_event);
        imageView = findViewById(R.id.image_profile_event_config);
        cameraBT = findViewById(R.id.imgbt_camera_event);
        galleryBT = findViewById(R.id.imgbt_gallery_event);
        configNameEventEdt = findViewById(R.id.edittext_config_name_event);
        configIntroEventEdt = findViewById(R.id.edittext_config_intro_event);

        imageView.setImageResource(R.drawable.ic_action_music_1);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);


        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new Event();
                event.setEndDate(endDateEventEdt.getText().toString());
                event.setStartDate(startDateEventEdt.getText().toString());
                event.setNameEvent(configNameEventEdt.getText().toString());
                event.setDetailsEvent(configIntroEventEdt.getText().toString());
                event.setNameEvent(edNameEvent.getText().toString());
                event.setDetailsEvent(edDetailsEvent.getText().toString());
                addGeofence(newlatlng, GEOFENCE_RADIUS);
                event.setGeofenceID(event.getId());
                if (saved ==  null){
                    event.setAddressEvent("Endereço não informado.");
                    event.setLatlng("");
                    event.setLongitude("");
                    event.setLatitude("");
                }else {
                    event.setAddressEvent(saved.toString());
                    event.setLatlng(newlatlng.toString());
                    event.setLatitude(coordl1);
                    event.setLongitude(coordl2);
                }
                event.setConfirmedPresence(confirmedPresence);
                if (imageData != null) {
                    saveEventPhoto(imageData);
                }else{
                    event.setPhotoEvent("");
                    event.save();
                    finish();
                }
                Toast.makeText(ConfigEventActivity.this, "Criando Evento", Toast.LENGTH_LONG).show();
            }
        });


        cameraBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePermission();
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECT_CAMERA);
                }else{
                    alertValidatePermission();
                }
            }
        });

        galleryBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePermission();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECT_GALLERY);
                }else{
                    alertValidatePermission();
                }
            }
        });

        setDate();



        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_address);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.PHOTO_METADATAS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Place: " + place.getAddress());
                Log.i(TAG, "Place: " + place.getLatLng());
                Log.i(TAG, "Place: " + place.getAttributions());
                newlatlng = place.getLatLng();
                saved = place.getAddress();
                l1 = newlatlng.latitude;
                l2 = newlatlng.longitude;
                coordl1 = l1.toString();
                coordl2 = l2.toString();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();


    }


    private void saveEventPhoto(byte[] imageData){
        storageReference = ConfigFirebase.getStorageReference();
        String UserId = UserFirebase.getCurrentUserId();
        final StorageReference imageRef = storageReference
                .child("images")
                .child("events")
                .child(UserId)
                .child(event.getId() + ".jpeg");
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ConfigEventActivity.this, "Evento Criado", Toast.LENGTH_SHORT).show();
                    uri = task.getResult();
                    event.setPhotoEvent(uri.toString());
                    event.save();
                    finish();
                    SharedPreferences.Editor editor = getSharedPreferences("photoEvent", MODE_PRIVATE).edit();
                    editor.putString("fotoEvento", uri.toString());
                    editor.commit();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("onActivityResult", "onActivityResult");

        //Testar processor de retorno dos dados
        if(resultCode == RESULT_OK){


            try{
                switch (requestCode){
                    case SELECT_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECT_GALLERY:
                        Uri selectedImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        break;
                }

                if(image != null){
                    imageView.setImageBitmap(image);

                    //retrive data from image
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    imageData = baos.toByteArray();

             /*       //save image in Firebase
                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("events")
                            .child(event.getId() + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ConfigEventActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                Uri uri = task.getResult();
                                updateProfileImage(uri);
                            }else {
                                Toast.makeText(ConfigEventActivity.this, "Failed to uploud Image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

*/
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setDate(){
        startDateEventEdt.setOnClickListener(this);
        endDateEventEdt.setOnClickListener(this);

        final Calendar newCalendar = Calendar.getInstance();

         startDateEventDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(ConfigEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int h, int min) {
                        newDate.set(Calendar.HOUR_OF_DAY, h);
                        newDate.set(Calendar.MINUTE, min);
                        startDateEventEdt.setText(dateFormat.format(newDate.getTime()));
                    }
                }, newCalendar.get(Calendar.HOUR_OF_DAY),
                        newCalendar.get(Calendar.MINUTE), true).show();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

         endDateEventDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(ConfigEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int h, int min) {
                        newDate.set(Calendar.HOUR_OF_DAY, h);
                        newDate.set(Calendar.MINUTE, min);
                        endDateEventEdt.setText(dateFormat.format(newDate.getTime()));
                    }
                }, newCalendar.get(Calendar.HOUR_OF_DAY),
                        newCalendar.get(Calendar.MINUTE), true).show();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view){
        if (view == startDateEventEdt){
            startDateEventDialog.show();
        }else if (view == endDateEventEdt){
            endDateEventDialog.show();
        }
    }

    private void alertValidatePermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar  o app é necessário  aceitar permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void validatePermission(){
        Permission.validatePermission(permissionsNeed, this, 1);
    }

    public void updateProfileImage(Uri uri){
        UserFirebase.updateProfilePicture(uri);
    }

    private void addGeofence(LatLng latLng, float radius){

        Geofence geofence = geofenceHelper.getGeofence(event.getId(), latLng, radius,Geofence.
                GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geo Added");

                        geo = new Geo();
                        geo.setId(event.getId());
                        geo.setNameEvent(event.getNameEvent());
                        geo.setDetailsEvent(event.getDetailsEvent());
                        geo.save();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure" + errorMessage);
                    }
                });
    }
}
