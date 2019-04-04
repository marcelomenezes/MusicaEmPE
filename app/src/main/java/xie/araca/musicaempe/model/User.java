package xie.araca.musicaempe.model;

import com.google.firebase.database.DatabaseReference;

import xie.araca.musicaempe.config.ConfigFirebase;

public class User {

    String nameUser;
    String flaguser;
    String username;
    String rythm;
    String password;
    String email;
    String id;

    public User(){

    }

    public void save(){
        DatabaseReference databaseReference = ConfigFirebase.getReferenceFirebase();
        databaseReference.child("users").child(getId()).setValue(this);
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
}
