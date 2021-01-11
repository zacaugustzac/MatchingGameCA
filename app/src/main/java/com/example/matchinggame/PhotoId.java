package com.example.matchinggame;

import java.util.Objects;

public class PhotoId {
    public int value;

    public PhotoId(int v){
        this.value = v;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoId photoId = (PhotoId) o;
        return value == photoId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return ""+value;
    }
}
