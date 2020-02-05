package xie.araca.musicaempe.model;

import java.io.Serializable;

public class Follower implements Serializable {

    String followerId;
    String followedId;
    String cityFollowedId;
    String nameFollowedId;
    String photoFollowedId;

    public String getCityFollowedId() {
        return cityFollowedId;
    }

    public void setCityFollowedId(String cityFollowedId) {
        this.cityFollowedId = cityFollowedId;
    }

    public String getNameFollowedId() {
        return nameFollowedId;
    }

    public void setNameFollowedId(String nameFollowedId) {
        this.nameFollowedId = nameFollowedId;
    }

    public String getPhotoFollowedId() {
        return photoFollowedId;
    }

    public void setPhotoFollowedId(String photoFollowedId) {
        this.photoFollowedId = photoFollowedId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowedId() {
        return followedId;
    }

    public void setFollowedId(String followedId) {
        this.followedId = followedId;
    }
}
