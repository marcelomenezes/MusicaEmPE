package xie.araca.musicaempe.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ActivityConfigBinding;
import xie.araca.musicaempe.helper.Permission;
import xie.araca.musicaempe.helper.RecyclerItemClickListener;
import xie.araca.musicaempe.helper.UserFirebase;
import xie.araca.musicaempe.model.User;


public class ConfigArtistActivity extends AppCompatActivity {

    ActivityConfigBinding binding;
    User user = new User();
    User auxUser;

    private String[] permissionsNeed = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    private static final int SELECT_CAMERA = 100;
    private static final int SELECT_GALLERY = 200;

    private ImageView imageView;
    private EditText city;
    private Button buttonSave;

    private StorageReference storageReference;
    private String currentUserId;
    private FirebaseUser firebaseUser;

    private String nameUser;
    private String textCity;
    private String textNeighborhood;
    private String textIntro;
    private String textRythm;

    private String name;
    private String stringCity;
    private String neighborhood;
    private String intro;
    private String rythm;
    private String type;

    private ArrayAdapter<String> rtAdapter;

    private AutoCompleteTextView autoCompleteTextView;
    private EditText edtRythm;

    String[] arrayRitmo = {"Baião", "Brega-romântico", "Brega-pop", "Brega-funk", "Ciranda",
            "Coco", "Cavalo-marinho", "Forró", "Frevo", "Maracatu", "Caboclinho",
            "Xaxado", "Manguebeat", "Rap", "Sertanejo", "Rock", "Samba",
            "Pop", "Metal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rtAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                arrayRitmo);


        binding.contentConfigArtist.edittextConfigRythmArtist.setAdapter(rtAdapter);
        binding.contentConfigArtist.edittextConfigRythmArtist.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
// Minimum number of characters the user has to type before the drop-down list is shown
        binding.contentConfigArtist.edittextConfigRythmArtist.setThreshold(1);
        final ChipGroup chipGroup = findViewById(R.id.chip_group_artist);
        //Chip entryChip = addChipToGroup(chipGroup, "Hello World");

        binding.contentConfigArtist.edittextConfigRythmArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                addChipToGroup(selected);


            }
        });
        /*for(String ritmo : arrayRitmo){
            Chip chip = new Chip(this);
            chip.setText(ritmo);
            entryChipGroup.addView(chip);
        }*/

                toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);
        imageView = findViewById(R.id.image_profile_artist_config);
        city = findViewById(R.id.edittext_config_intro_artist);
        //buttonSave = findViewById(R.id.button_save_config_artist);


        //Initial Congig
        storageReference = ConfigFirebase.getStorageReference();
        currentUserId = UserFirebase.getCurrentUserId();
        auxUser = UserFirebase.getDataUserLoggedIn();
        firebaseUser = UserFirebase.getCurrentUser();

        //Retrieve User data
        final FirebaseUser userFirebase = UserFirebase.getCurrentUser();

        Uri uri = userFirebase.getPhotoUrl();
        if (uri != null){
            Glide.with(ConfigArtistActivity.this)
                    .load(uri)
                    .into(imageView);
        }else{
            imageView.setImageResource(R.drawable.user);
        }


        Intent intent = getIntent();
        nameUser = intent.getStringExtra("nameUser");
        textCity = intent.getStringExtra("city");
        textNeighborhood = intent.getStringExtra("neighborhood");
        textIntro = intent.getStringExtra("intro");
        textRythm = intent.getStringExtra("rythm");
        type = intent.getStringExtra("type");

        binding.contentConfigArtist.edittextConfigNameProfileUser.setText(nameUser);
        binding.contentConfigArtist.edittextConfigCityProfileArtist.setText(textCity);
        binding.contentConfigArtist.edittextConfigNeighbourhoodProfileArtist.setText(textNeighborhood);
        binding.contentConfigArtist.edittextConfigIntroArtist.setText(textIntro);
        binding.contentConfigArtist.edittextConfigRythmArtist.setText(textRythm);

        /*if(textRythm.contains(", "))
            splitComma(textRythm);
        else
            addChipToGroup(textRythm);*/

        binding.contentConfigArtist.imgbtGalleryArtist.setOnClickListener(new View.OnClickListener() {
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

        binding.contentConfigArtist.imgbtCameraArtist.setOnClickListener(new View.OnClickListener() {
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

        binding.contentConfigArtist.buttonSaveConfigArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = binding.contentConfigArtist.edittextConfigNameProfileUser.getText().toString();
                stringCity = binding.contentConfigArtist.edittextConfigCityProfileArtist.getText().toString();
                neighborhood = binding.contentConfigArtist.edittextConfigNeighbourhoodProfileArtist.getText().toString();
                intro = binding.contentConfigArtist.edittextConfigIntroArtist.getText().toString();
                rythm = binding.contentConfigArtist.edittextConfigRythmArtist.getText().toString();

                if (!name.isEmpty()){
                    if (!stringCity.isEmpty()){
                        if (!neighborhood.isEmpty()){
                            if(!intro.isEmpty()){
                                if (!rythm.isEmpty()){
                                    user.setNameUser(name);
                                    user.setCity(stringCity);
                                    user.setNeighborhood(neighborhood);
                                    user.setIntro(intro);
                                    user.setRythm(rythm);
                                    user.setEmail(firebaseUser.getEmail());
                                    user.setId(firebaseUser.getUid());
                                    user.setType(type);
                                    user.setRythm(rythm);
                                    if(firebaseUser.getPhotoUrl() != null){
                                        SharedPreferences preferences = getSharedPreferences("photo", MODE_PRIVATE);
                                        String foto = preferences.getString("foto", null);
                                        user.setPhoto(foto);
                                    }else{
                                        user.setPhoto(" ");
                                    }
                                        user.update();
                                        UserFirebase.updateNameUser(user.getNameUser());
                                        Toast.makeText(ConfigArtistActivity.this, "Dados Salvos", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(ConfigArtistActivity.this, "Preencha com ao menos um ritmo", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(ConfigArtistActivity.this, "Preencha uma pequena introdução sobre a banda/artista", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(ConfigArtistActivity.this, "Preencha o bairro", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ConfigArtistActivity.this, "Preencha a cidade", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ConfigArtistActivity.this, "Preencha o nome do Artista/Banda", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ConfigArtistActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                Uri uri = task.getResult();
                                updateProfileImage(uri);
                                SharedPreferences.Editor editor = getSharedPreferences("photo", MODE_PRIVATE).edit();
                                editor.putString("foto", uri.toString());
                                editor.commit();

                            }else {
                                Toast.makeText(ConfigArtistActivity.this, "Failed to uploud Image", Toast.LENGTH_SHORT).show();
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
        savePhotoUrl(uri);
    }

    public void savePhotoUrl(Uri uri){

        auxUser.setPhoto(uri.toString());
        auxUser.setRythm(textRythm);
        auxUser.setCity(textCity);
        auxUser.setIntro(textIntro);
        auxUser.setNeighborhood(textNeighborhood);
        auxUser.setType(type);
        auxUser.update();
    }

    private void addChipToGroup(String text) {
        ChipDrawable chip = ChipDrawable.createFromResource(this, R.xml.my_chip);
        ImageSpan span = new ImageSpan(chip);

        int cursorPosition = binding.contentConfigArtist.edittextConfigRythmArtist.getSelectionStart();
        int spanLength = text.length() + 2;
        Editable editable = binding.contentConfigArtist.edittextConfigRythmArtist.getText();
        chip.setText(text);
        chip.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
        editable.setSpan(span, cursorPosition - spanLength, cursorPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        /*int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);*/
    }

    /*private void addChipToGroup(String text) {
        String str = text;
        List<String> list = Arrays.asList(str.split(", "));
        ChipDrawable chip = ChipDrawable.createFromResource(this, R.xml.my_chip);
        ImageSpan span = new ImageSpan(chip);
        int soma= 0;
        int cursorPosition = binding.contentConfigArtist.edittextConfigRythmArtist.getSelectionStart();

        if (list.size() > 0) {
            int spanLengthTotal = text.length();
            int spanLength = list.get(0).length();
            List<String> lista = new ArrayList<>();
            for (int i = 0; i < list.size();i++) {
                //if (cursorPosition < spanLength) {
                    chip.setText(list.get(i));
                    chip.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                    cursorPosition = spanLength;

                    lista.add(binding.contentConfigArtist.edittextConfigRythmArtist.getText().toString());
                    if (i > 0) {

                        addChip(list.get(i-1), list.get(i), text );
                        /*
                        binding.contentConfigArtist.edittextConfigRythmArtist.setText(list.get(i-1) + ", " + list.get(i) + ", ");
                        Editable editable = binding.contentConfigArtist.edittextConfigRythmArtist.getText();

                        chip.setText(list.get(i-1));
                        chip.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());

                        editable.setSpan(span, cursorPosition - spanLength, spanLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }else {
                        addChip1("", list.get(i), text);
                        /*ChipDrawable chip1 = ChipDrawable.createFromResource(this, R.xml.my_chip);
                        ImageSpan span1 = new ImageSpan(chip);
                        chip1.setText(list.get(i));
                        chip1.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());

                        binding.contentConfigArtist.edittextConfigRythmArtist.setText(list.get(i) + ", ");
                        Editable editable = binding.contentConfigArtist.edittextConfigRythmArtist.getText();


                        editable.setSpan(span, cursorPosition - spanLength, spanLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                  //  }
                    //Editable editable = binding.contentConfigArtist.edittextConfigRythmArtist.getText();


                    //editable.setSpan(span, cursorPosition - spanLength, spanLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                /*} else {
                    chip.setText(text);
                    chip.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                    int spanlengthNow = list.get(i).length();
                    if ()
                    int spanLengthOld = list.get(i-1).length();
                    int somou = spanlengthNow + spanLengthOld;

                    binding.contentConfigArtist.edittextConfigRythmArtist.setText( lista.get(i-1) + list.get(i) + ", ");
                    Editable editable1 = binding.contentConfigArtist.edittextConfigRythmArtist.getText();
                    editable1.setSpan(span, spanLengthTotal - spanlengthNow , --somou, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }*/
          /*  }
        }else {
            int spanLength = text.length() + 2;
                    chip.setText(text);
                    chip.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                    int spanlength1 = text.length() + 2;
                    Editable editable = binding.contentConfigArtist.edittextConfigRythmArtist.getText();
                    editable.setSpan(span, cursorPosition - spanlength1, cursorPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            */

            private void addChip(String sizeBefore, String sizeNow, int sizeTotal){
                ChipDrawable chip = ChipDrawable.createFromResource(this, R.xml.my_chip);
                ImageSpan span = new ImageSpan(chip);
                int cursorPosition = binding.contentConfigArtist.edittextConfigRythmArtist.getSelectionStart();
                int spanLength = sizeNow.length() + 2;

                chip.setText(sizeNow);
                chip.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                binding.contentConfigArtist.edittextConfigRythmArtist.setText(sizeBefore + ", " + sizeNow + ", ");
                Editable editable = binding.contentConfigArtist.edittextConfigRythmArtist.getText();
                editable.setSpan(span, sizeTotal - spanLength, spanLength + sizeBefore.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }

    private void addChip1(String sizeNow){
        ChipDrawable chip = ChipDrawable.createFromResource(this, R.xml.my_chip);
        ImageSpan span = new ImageSpan(chip);
        int cursorPosition = binding.contentConfigArtist.edittextConfigRythmArtist.getSelectionStart();
        int spanLength = sizeNow.length();

        chip.setText(sizeNow);
        chip.setBounds(0, 20, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
        binding.contentConfigArtist.edittextConfigRythmArtist.setText(sizeNow + ", ");
        Editable editable = binding.contentConfigArtist.edittextConfigRythmArtist.getText();
        editable.setSpan(span, 0, spanLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

    }

    private void splitComma(String text){
        String str = text;
        List<String> list = Arrays.asList(str.split(", "));
        if (list.size() > 1){
            for(int i = 1; i < list.size(); i++){
                addChip(list.get(i-1), list.get(i), text.length() );
            }
        }else{
            addChip1(text);

        }
    }

        /*int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);*/
    }

   /* private void splitString(String text){
        String str = text;
        List<String> list = Arrays.asList(str.split("," ));

       // addChipToGroup(list.get(0));
        //addChipToGroup(list.get(1));

       /* for (String ritmo: list){
            addChipToGroup(ritmo);
        }



    }


}*/
