package xie.araca.musicaempe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ActivityLoginBinding;
import xie.araca.musicaempe.helper.UserFirebase;
import xie.araca.musicaempe.model.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private User user = new User();
    private FirebaseAuth firebaseAuth;

    private String login;
    private String senha;

    private String tipo;
    private String nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        SharedPreferences preferences = getSharedPreferences("type", MODE_PRIVATE);


        ValidateUserLoggedIn(preferences.getString("tipo", null));


        binding.loginContent.noSignupId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        binding.loginContent.botaoLogarId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Fazendo login", Toast.LENGTH_LONG).show();
                validateFields();
            }
        });



    }

    public void ValidateUserLoggedIn(String tipoaut){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        if (firebaseAuth.getCurrentUser() != null){
           openMainActivity(tipoaut);
        }
    }

    public void ValidateLogin(User user){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    DatabaseReference databaseReference = ConfigFirebase.getReferenceFirebase();
                    DatabaseReference data = databaseReference.child("users").child(UserFirebase.getCurrentUserId());

                    data.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            tipo = dataSnapshot.child("type").getValue().toString();
                            openMainActivity(tipo);
                            SharedPreferences.Editor editor = getSharedPreferences("type", MODE_PRIVATE).edit();
                            editor.putString("tipo", tipo);
                            editor.commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(LoginActivity.this, "Sucesso ao logar", Toast.LENGTH_LONG).show();
                }else {
                    String error = "";

                    try {
                    throw task.getException();
                    }
                 catch (FirebaseAuthInvalidUserException e) {
                    error = getString(R.string.error_login_invalid_user);
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    error = getString(R.string.error_login_invalid_credetials);
                } catch (Exception e) {
                        error = "Erro ao fazer o login" + e.getMessage();
                    e.printStackTrace();
                }
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void openMainActivity(String tipo){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("tipo", tipo);
            startActivity(intent);
            finish();

    }


    public void validateFields(){
        login = binding.loginContent.editLoginUsuarioId.getText().toString();
        senha = binding.loginContent.editLoginSenhaId.getText().toString();

        if (!login.isEmpty()){
            if (!senha.isEmpty()){
                user.setEmail(login);
                user.setPassword(senha);
                ValidateLogin(user);
            }else{
                Toast.makeText(LoginActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this, "Preencha o email/login", Toast.LENGTH_SHORT).show();
        }
    }



}
