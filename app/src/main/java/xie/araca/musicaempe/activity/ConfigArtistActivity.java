package xie.araca.musicaempe.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.Permission;
import xie.araca.musicaempe.helper.UserFirebase;

public class ConfigArtistActivity extends AppCompatActivity {

    private String[] permissionsNeed = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    private static final int SELECT_CAMERA = 100;
    private static final int SELECT_GALLERY = 200;

    private ImageView imageView;
    private EditText city;

    private StorageReference storageReference;
    private String currentUserId;

    private String nameUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);
        imageView = findViewById(R.id.image_profile_artist_config);
        city = findViewById(R.id.edittext_config_intro_artist);
        ImageButton btChoosePicture = findViewById(R.id.imgbt_gallery_artist);
        ImageButton btTakePicture = findViewById(R.id.imgbt_camera_artist);

        //Initial Congig
        storageReference = ConfigFirebase.getStorageReference();
        currentUserId = UserFirebase.getCurrentUserId();

        //Retrieve User data
        FirebaseUser user = UserFirebase.getCurrentUser();

        Uri uri = user.getPhotoUrl();
        if (uri != null){
            Glide.with(ConfigArtistActivity.this)
                    .load(uri)
                    .into(imageView);
        }else{
            imageView.setImageResource(R.drawable.user);
        }

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("nameUser");

        city.setText(nameUser);


        btChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePermission();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECT_GALLERY);
                }
            }
        });

        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePermission();
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECT_CAMERA);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap image = null;

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
                    byte[] imageData = baos.toByteArray();

                    //save image in Firebase
                    StorageReference imageRef = storageReference
                            .child("images")
                            .child("profile")
                            .child(currentUserId + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigArtistActivity.this, "Failed to uploud Image", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfigArtistActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                            Uri uri = taskSnapshot.getDownloadUrl();
                            updateProfileImage(uri);
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults){
            if (permissionResult == PackageManager.PERMISSION_DENIED){
                validatePermission();
            }
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
}
