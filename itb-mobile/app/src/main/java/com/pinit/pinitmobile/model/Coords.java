package com.pinit.pinitmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Coords implements Parcelable {

    LatLng latLng;

    public Coords(double latitude, double longitude) {
        latLng = new LatLng(latitude, longitude);
    }

    public Coords(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getDistance(LatLng other) {
        int earthRadius = 6371000;
        double f1 = Math.toRadians(getLatLng().latitude);
        double f2 = Math.toRadians(other.latitude);
        double l1 = Math.toRadians(getLatLng().longitude);
        double l2 = Math.toRadians(other.longitude);
        double a = Math.pow(Math.sin((f2 - f1)/2), 2) + Math.cos(f1) * Math.cos(f2) * Math.pow(Math.sin((l2 - l1)/2), 2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = earthRadius * c;
        return (int)Math.round(d);
    }

    public int getDistance(Coords other) {
        return getDistance(other.getLatLng());
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.latLng, flags);
    }

    private Coords(Parcel in) {
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Coords> CREATOR = new Creator<Coords>() {
        public Coords createFromParcel(Parcel source) {
            return new Coords(source);
        }

        public Coords[] newArray(int size) {
            return new Coords[size];
        }
    };
}
