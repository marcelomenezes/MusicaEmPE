package xie.araca.musicaempe.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.model.User;

public class UserFirebase {

            public static String getCurrentUserId(){
                FirebaseAuth user = ConfigFirebase.getFirebaseAuth();
                String currentUserId = user.getCurrentUser().getUid();
                return currentUserId;
            }

            public static FirebaseUser getCurrentUser(){
                FirebaseAuth user = ConfigFirebase.getFirebaseAuth();
                return user.getCurrentUser();
            }

            public static boolean updateProfilePicture(Uri uri){

                try {
                    FirebaseUser user = getCurrentUser();
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();

                    user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                Log.d("Profile", "Erro ao atualizar imagem" );
                            }
                        }
                    });
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }

    public static boolean updateNameUser(String name){

        try {

            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( name )
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Perfil", "Erro ao atualizar nome");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

            public static String  setDisplayNameUser(String displayName ){
                FirebaseUser user = getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName).build();
                user.updateProfile(profileUpdates);

                return displayName;
            }

            public static String getCurrentUserDisplayName(){
                FirebaseUser user = getCurrentUser();
                String name = user.getDisplayName();
                return name;
            }

    public static User getDataUserLoggedIn(){

        FirebaseUser firebaseUser = getCurrentUser();

        User usuario = new User();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNameUser( firebaseUser.getDisplayName() );

        if ( firebaseUser.getPhotoUrl() == null ){
            usuario.setPhoto("");
        }else {
            usuario.setPhoto( firebaseUser.getPhotoUrl().toString() );
        }

        return usuario;

    }
}
