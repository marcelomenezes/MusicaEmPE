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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.Permission;
import xie.araca.musicaempe.helper.UserFirebase;
import xie.araca.musicaempe.model.User;

public class ConfigUserActivity extends AppCompatActivity {

    private String[] permissionsNeed = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    private static final int SELECT_CAMERA = 100;
    private static final int SELECT_GALLERY = 200;

    private ImageView imageView;
    private EditText textNameUser;
    private EditText city;

    private StorageReference storageReference;
    private String currentUserId;

    private String nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);
        imageView = findViewById(R.id.image_profile_user_config);
        textNameUser = findViewById(R.id.edittext_config_name_profile_user);
        city = findViewById(R.id.edittext_config_city_profile_user);
        ImageButton btChoosePicture = findViewById(R.id.imgbt_gallery_user);
        ImageButton btTakePicture = findViewById(R.id.imgbt_camera_user);

        //Initial Congig
        storageReference = ConfigFirebase.getStorageReference();
        currentUserId = UserFirebase.getCurrentUserId();

        //Retrieve User data
        FirebaseUser user = UserFirebase.getCurrentUser();

        Uri uri = user.getPhotoUrl();
        if (uri != null){
            Glide.with(ConfigUserActivity.this)
                    .load(uri)
                    .into(imageView);
        }else{
            imageView.setImageResource(R.drawable.user);
        }

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("nameUser");

        textNameUser.setText(nameUser);


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
                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("profile")
                            .child(currentUserId + ".jpeg");


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
                                Toast.makeText(ConfigUserActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                Uri uri = task.getResult();
                                updateProfileImage(uri);
                            }else {
                                Toast.makeText(ConfigUserActivity.this, "Failed to uploud Image", Toast.LENGTH_SHORT).show();
                            }
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
