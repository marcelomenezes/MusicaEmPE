package xie.araca.musicaempe.model;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import xie.araca.musicaempe.config.ConfigFirebase;

public class Event {

    String photoEvent;
    String startDate;
    String endDate;
    String nameEvent;
    String id;
    String detailsEvent;
    String addressEvent;


    public Event(){
        DatabaseReference databaseReference = ConfigFirebase.getReferenceFirebase();
        DatabaseReference eventsRef = databaseReference.child("events");
        String idEventRef = eventsRef.push().getKey();
        setId(idEventRef);

    }

    public boolean save(){
        DatabaseReference databaseReference = ConfigFirebase.getReferenceFirebase();
        databaseReference.child("events").child(getId()).setValue(this);
        return true;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoEvent() {
        return photoEvent;
    }

    public void setPhotoEvent(String photoEvent) {
        this.photoEvent = photoEvent;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public void setNameEvent(String nameEvent) {
        this.nameEvent = nameEvent;
    }

    public String getDetailsEvent() {
        return detailsEvent;
    }

    public void setDetailsEvent(String detailsEvent) {
        this.detailsEvent = detailsEvent;
    }

    public String getAddressEvent() {
        return addressEvent;
    }

    public void setAddressEvent(String addressEvent) {
        this.addressEvent = addressEvent;
    }



}
