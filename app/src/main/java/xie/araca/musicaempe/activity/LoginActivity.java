package xie.araca.musicaempe.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ActivityLoginBinding;
import xie.araca.musicaempe.model.User;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private User user;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        ValidateUserLoggedIn();

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
                user = new User();
                user.setEmail(binding.loginContent.editLoginUsuarioId.getText().toString());
                user.setPassword(binding.loginContent.editLoginSenhaId.getText().toString());
                ValidateLogin();
            }
        });

    }

    public void ValidateUserLoggedIn(){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        if (firebaseAuth.getCurrentUser() != null){
            openMainActivity();
        }
    }

    public void ValidateLogin(){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    openMainActivity();
                    Toast.makeText(LoginActivity.this, "Sucesso ao logar", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Erro ao logar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void openMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
