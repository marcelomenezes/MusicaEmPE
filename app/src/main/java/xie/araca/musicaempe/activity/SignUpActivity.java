package xie.araca.musicaempe.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ActivitySignUpBinding;
import xie.araca.musicaempe.model.User;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private User user;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);


        binding.signupContent.noLoginId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.signupContent.botaoCadastrarId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User();
                user.setNameUser(binding.signupContent.cadastroEmailId.getText().toString());
                user.setEmail(binding.signupContent.cadastroEmailId.getText().toString());
                user.setUsername(binding.signupContent.cadastroUsuarioId.getText().toString());
                user.setPassword(binding.signupContent.cadastroSenhaId.getText().toString());
                signUpUser();
            }
        });
    }

    private void signUpUser(){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        firebaseAuth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Cadastrado com sucesso", Toast.LENGTH_LONG).show();

                    FirebaseUser firebaseUser = task.getResult().getUser();
                    user.setId(firebaseUser.getUid());
                    user.save();
                    finish();

                }else {

                    String error = "";

                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = getString(R.string.error_signup_weak_password);
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = getString(R.string.error_signup_invalid_email);
                    } catch (FirebaseAuthUserCollisionException e) {
                        error = getString(R.string.error_signup_user_collision);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(SignUpActivity.this,  error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
