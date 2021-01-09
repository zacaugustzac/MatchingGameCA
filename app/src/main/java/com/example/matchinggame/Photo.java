package com.example.matchinggame;

import java.util.function.Consumer;

public class Photo {

    private int PhotoId;
    private boolean photoChecked;
    private Consumer<Boolean> onToggleListener;

    public Photo(int PhotoId,boolean photoChecked) {
        this.PhotoId = PhotoId;
        this.photoChecked = photoChecked;
        this.onToggleListener = ignored -> {};
    }

    public Photo(int photoId) {
        this(photoId, false);
    }

    public Photo() {
        this(-1);
    }

    public int getPhotoId() {
        return PhotoId;
    }
    public boolean isPhotoChecked() {
        return photoChecked;
    }

    public void setOppositeCheck(){
        this.photoChecked = !this.photoChecked;
        onToggleListener.accept(this.photoChecked);
    }

    public void setOnToggleListener(Consumer<Boolean> onToggleListener) {
        this.onToggleListener = onToggleListener;
    }
}