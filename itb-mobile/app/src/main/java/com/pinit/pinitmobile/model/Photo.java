package com.pinit.pinitmobile.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.pinit.pinitmobile.util.PhotoUtil;

public class Photo {

    public static final int PREFERRED_WIDTH_BIG_PHOTO = 200;
    public static final int PREFERRED_WIDTH_SMALL_PHOTO = 100;
    private static final String TAG = Photo.class.getName();
    private String photoPath;
    private Bitmap photo;
    private String systemPhotoPath;


    public Photo(Bitmap photo) {
        this.photo = photo;
        systemPhotoPath = null;
    }

    public Photo(Bitmap photo, String systemPhotoPath) {
        this.photo = photo;
        this.systemPhotoPath = systemPhotoPath;
    }

    public Photo(Byte[] photoInBytes) {
        photo = PhotoUtil.byteArrayToBitmap(photoInBytes);
    }

    public void savePhoto(String directory, String filename) {
        if (photo != null) {
            photoPath = PhotoUtil.savePhotoFromBitmap(photo, directory, filename);
            photo = null;
        }
    }

    public Bitmap getPhoto() {
        if (photo != null) {
            Log.e(TAG, "Photo still kept in memory!");
            return photo;
        }
        if (photoPath != null) {
            return PhotoUtil.getPhotoFromPath(photoPath);
        }
        return null;
    }

    public Byte[] getPhotoAsByteArray() {
        if (photo == null) {
            photo = PhotoUtil.getPhotoFromPath(photoPath);
        } else {
            Log.e(TAG, "Photo still kept in memory!");
        }
        if (photo != null) return PhotoUtil.bitmapToByteArray(photo);
        return null;
    }

    public void scalePhoto(int preferredWidth) {
        Bitmap scaledPhoto;
        if (photo != null) {
            if (photo.getWidth() > preferredWidth) {
                scaledPhoto = PhotoUtil.bitmapScale(photo, preferredWidth);
                photo = scaledPhoto;
            }
        } else {
            Log.e(TAG, "Photo need to be read into memory first by getPhoto() function!");
        }
    }

    public void rotatatePhototoPortrait() {
        if (systemPhotoPath != null) {
            if (photo != null) {
                PhotoUtil.rotateBitmapToPortraitWithExif(photo, systemPhotoPath);
            } else {
                Log.e(TAG, "Photo need to be read into memory first by getPhoto() function!");
            }
        } else {
            Log.e(TAG, "You must provide system path to photo file Photo object to access exif file.");
        }
    }


}
