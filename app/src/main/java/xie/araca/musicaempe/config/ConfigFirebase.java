package xie.araca.musicaempe.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {
    private static DatabaseReference referenceFirebase;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getReferenceFirebase(){

        if (referenceFirebase == null) {
            referenceFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenceFirebase;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }
}
