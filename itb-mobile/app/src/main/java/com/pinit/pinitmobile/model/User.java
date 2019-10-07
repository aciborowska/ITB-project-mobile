package com.pinit.pinitmobile.model;


import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.util.PhotoUtil;

import java.util.Objects;

public class User {

    private String username;
    private Long userId;
    private String deviceId;
    private Byte[] photoSmall;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Long lastLoginDate;
    private long joinedDate;
    private int commentsAmount;
    private int positivesAmount;
    private int negativesAmount;
    @JsonIgnore
    private Photo smallPhoto;
    @JsonIgnore
    private Photo bigPhoto;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCommentsAmount() {
        return commentsAmount;
    }

    public void setCommentsAmount(int commentsAmount) {
        this.commentsAmount = commentsAmount;
    }

    public int getPositivesAmount() {
        return positivesAmount;
    }

    public void setPositivesAmount(int positivesAmount) {
        this.positivesAmount = positivesAmount;
    }

    public int getNegativesAmount() {
        return negativesAmount;
    }

    public void setNegativesAmount(int negativesAmount) {
        this.negativesAmount = negativesAmount;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Long getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(long joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Photo getBigPhoto() {
        return bigPhoto;
    }

    public void setBigPhoto(Photo bigPhoto, String directory, String fileName) {
        bigPhoto.scalePhoto(Photo.PREFERRED_WIDTH_BIG_PHOTO);
        bigPhoto.savePhoto(directory, fileName);
        this.bigPhoto = bigPhoto;
    }

    public Photo getSmallPhoto() {
        return smallPhoto;
    }

    public void setSmallPhoto(Photo smallPhoto, String directory, String fileName) {
        smallPhoto.scalePhoto(Photo.PREFERRED_WIDTH_SMALL_PHOTO);
        smallPhoto.savePhoto(directory, fileName);
        this.smallPhoto = smallPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return (int) (userId ^ (userId >>> 32));
    }

    public Byte[] getPhotoSmall() {
        return photoSmall;
    }

    public void setPhotoSmall(Byte[] photoSmall) {
        this.photoSmall = photoSmall;
    }

    @JsonIgnore
    public void setPhotoSmall(byte[] primitive) {
        Bitmap bitmap = PhotoUtil.byteArrayToBitmap(primitive);
        Photo p = new Photo(bitmap);
        setSmallPhoto(p, Globals.COMMENT_USER_DIR, String.valueOf(userId));
    }
}
