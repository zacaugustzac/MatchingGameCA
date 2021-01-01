package com.example.matchinggame;

public class Photo {

    private int PhotoId;
    private boolean photoChecked;
    public Photo(int PhotoId,boolean photoChecked) {
        this.PhotoId = PhotoId;
        this.photoChecked = photoChecked;
    }

    public Photo(int photoId) {
        PhotoId = photoId;
    }

    public Photo() {
    }

    public int getPhotoId() {
        return PhotoId;
    }
    public boolean isPhotoChecked() {
        return photoChecked;
    }

    public void setOppositeCheck(){
        this.photoChecked = !this.photoChecked;
    }
}