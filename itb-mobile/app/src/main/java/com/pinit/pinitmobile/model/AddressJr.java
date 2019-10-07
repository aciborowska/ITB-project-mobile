package com.pinit.pinitmobile.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.pinit.pinitmobile.R;

import java.util.Locale;


public class AddressJr implements Parcelable {

    android.location.Address address;

    public AddressJr() {
        address = new android.location.Address(Locale.getDefault());
    }

    public AddressJr(android.location.Address address) {
        this.address = address;
    }

    public LatLng getCoords() {
        if (address.hasLatitude() && address.hasLongitude()) return (new LatLng(address.getLatitude(), address.getLongitude()));
        return null;
    }

    public String getDisplayName() {
        return address.getFeatureName();
    }

    public void setDisplayName(String name) {
        address.setFeatureName(name);
    }

    public String getStreetName() {
        return address.getThoroughfare();
    }

    public void setStreetName(String streetName) {
        address.setThoroughfare(streetName);
    }

    public String getCity() {
        return address.getLocality();
    }

    public void setCity(String city) {
        address.setLocality(city);
    }

    public String getCountryCode() {
        return address.getCountryCode();
    }

    public void setCountryCode(String countryCode) {
        address.setCountryCode(countryCode);
    }

    public boolean hasCoords() {
        return address.hasLatitude() && address.hasLongitude();
    }

    public double getLatitude() {
        return address.getLatitude();
    }

    public double getLongitude() {
        return address.getLongitude();
    }

    public void setLatitude(double lat) {
        address.setLatitude(lat);
    }

    public void setLongitude(double lon) {
        address.setLongitude(lon);
    }

    public android.location.Address getAddress() {
        return address;
    }

    public void setAddress(android.location.Address address) {
        this.address = address;
    }

    /**
     * Mapuje obiekt adresu na widoki tekstu
     *
     * @param displayName FeatureName
     * @param streetName  ThroufareName
     * @param city        Locality
     * @param country     CountryCode
     */
    public void mapAddressToView(TextView displayName, TextView streetName, TextView city, TextView country) {
        if (getAddress() != null) {
            displayName.setText(getAddress().getFeatureName());
            if (getAddress().getThoroughfare() != null) streetName.setText(getAddress().getThoroughfare());
            else streetName.setText("");
            if (getAddress().getLocality() != null) city.setText(getAddress().getLocality());
            else city.setText("");
            if (getAddress().getCountryCode() != null) country.setText(getAddress().getCountryCode());
            else country.setText("");
        } else {
            displayName.setText("");
            streetName.setText("");
            city.setText("");
            country.setText("");
        }
    }

    public void mapAddressToView(View v) {
        TextView feature = (TextView) v.findViewById(R.id.tvFeature);
        TextView throufare = (TextView) v.findViewById(R.id.tvThorougfare);
        TextView locality = (TextView) v.findViewById(R.id.tvLocality);
        TextView country = (TextView) v.findViewById(R.id.tvCountry);
        mapAddressToView(feature, throufare, locality, country);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.address, 0);
    }

    private AddressJr(Parcel in) {
        this.address = in.readParcelable(android.location.Address.class.getClassLoader());
    }

    public static final Creator<AddressJr> CREATOR = new Creator<AddressJr>() {
        public AddressJr createFromParcel(Parcel source) {
            return new AddressJr(source);
        }

        public AddressJr[] newArray(int size) {
            return new AddressJr[size];
        }
    };

    public boolean isValid() {
        return hasCoords() && getDisplayName() != null && !getDisplayName().isEmpty();
    }

    @Override
    public String toString() {
        String name = getDisplayName() != null ? " " + getDisplayName() : "";
        String street = getStreetName() != null ? " " + getStreetName() : "";
        String city = getCity() != null ? " " + getCity() : "";
        String country = getCountryCode() != null ? " " + getCountryCode() : "";

        StringBuilder sb = new StringBuilder();
        sb.append(street);
        sb.append(name + ",");
        sb.append(city + ",");
        sb.append(country + ",");

        return sb.toString();
    }
}
