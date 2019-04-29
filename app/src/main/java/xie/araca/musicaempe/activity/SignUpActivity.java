package xie.araca.musicaempe.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import xie.araca.musicaempe.R;
import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.databinding.ActivitySignUpBinding;
import xie.araca.musicaempe.helper.UserFirebase;
import xie.araca.musicaempe.model.User;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private User user = new User();

    private FirebaseAuth firebaseAuth;
    private Spinner spinner;

    private String nameUser;
    private String email;
    private String senha;
    private String userName;
    private String city = "";
    private String neighbourhood = "";
    private String intro = "";
    private String rythm = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        spinner = findViewById(R.id.item_check);


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
               // binding.signupContent.botaoCadastrarId.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                validateFields();
            }
        });
        configSpinner();
        //binding.signupContent.textCheck.setSelected(false);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.cinza), PorterDuff.Mode.SRC_ATOP);
        spinner.setSelection(2);
    }

    private void signUpUser(final User user){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        firebaseAuth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Cadastrado com sucesso", Toast.LENGTH_LONG).show();
                    UserFirebase.setDisplayNameUser(user.getNameUser());
                    firebaseAuth.signInWithEmailAndPassword(
                            user.getEmail(),
                            user.getPassword()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                openMainActivity();
                                Toast.makeText(SignUpActivity.this, "Sucesso ao logar", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(SignUpActivity.this, "Erro ao logar", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    user.setId(firebaseUser.getUid());
                    user.save();

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

    private void validateFields(){
        nameUser = binding.signupContent.cadastroNomeId.getText().toString();
        email = binding.signupContent.cadastroEmailId.getText().toString();
        senha = binding.signupContent.cadastroSenhaId.getText().toString();
        userName = binding.signupContent.cadastroUsuarioId.getText().toString();

        if (!nameUser.isEmpty()){

            if (!email.isEmpty()){

                if (!userName.isEmpty()){

                    if (!senha.isEmpty()){
                        user.setNameUser(binding.signupContent.cadastroNomeId.getText().toString());
                        user.setEmail(binding.signupContent.cadastroEmailId.getText().toString());
                        user.setUsername(binding.signupContent.cadastroUsuarioId.getText().toString());
                        user.setPassword(binding.signupContent.cadastroSenhaId.getText().toString());
                        user.setType(spinner.getSelectedItem().toString());
                        user.setCity(city);
                        user.setNeighborhood(neighbourhood);
                        user.setIntro(intro);
                        user.setRythm(rythm);
                        signUpUser(user);

                    }else {
                        Toast.makeText(SignUpActivity.this, "Preencha a Senha", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(SignUpActivity.this, "Preencha o Nome de Login", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(SignUpActivity.this, "Preencha o Nome do Artista/Usuário(a)", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(SignUpActivity.this, "Preencha o Nome do Artista/Usuário(a)", Toast.LENGTH_SHORT).show();
        }

    }

    private void configSpinner(){
        String[] types = new String[]{
                "Artista", "Produtor(a)", "Usuário(a)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.simple_spinner_item_text_white, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    public void openMainActivity(){
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        String tipo = spinner.getSelectedItem().toString();
        String nameUser = binding.signupContent.cadastroNomeId.getText().toString();

        intent.putExtra("tipo", tipo);
        intent.putExtra("nameUser", nameUser);
        startActivity(intent);
        finish();
    }

}
