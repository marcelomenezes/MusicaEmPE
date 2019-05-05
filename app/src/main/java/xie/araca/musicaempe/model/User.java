package xie.araca.musicaempe.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import xie.araca.musicaempe.config.ConfigFirebase;
import xie.araca.musicaempe.helper.UserFirebase;

public class User implements Serializable {

    String nameUser;
    String flaguser;
    String username;
    String rythm;
    String password;
    String email;
    String id;
    String type;
    String city;
    String neighborhood;
    String intro;
    String photo;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User(){

    }

    public void save(){
        DatabaseReference databaseReference = ConfigFirebase.getReferenceFirebase();
        databaseReference.child("users").child(getId()).setValue(this);
    }

    public void update(){

        String UserId = UserFirebase.getCurrentUserId();
        DatabaseReference database = ConfigFirebase.getReferenceFirebase();

        DatabaseReference userRef = database.child("users")
                .child( UserId);

        Map<String, Object> valueUser = convertToMap();

        userRef.updateChildren( valueUser );
    }

    public Map<String, Object> convertToMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail() );
        usuarioMap.put("nameUser", getNameUser() );
        usuarioMap.put("photo", getPhoto() );
        usuarioMap.put("city", getCity());
        usuarioMap.put("rythm", getRythm());
        usuarioMap.put("neighborhood", getNeighborhood());
        usuarioMap.put("intro", getIntro());

        return usuarioMap;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getFlaguser() {
        return flaguser;
    }

    public void setFlaguser(String flaguser) {
        this.flaguser = flaguser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRythm() {
        return rythm;
    }

    public void setRythm(String rythm) {
        this.rythm = rythm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
