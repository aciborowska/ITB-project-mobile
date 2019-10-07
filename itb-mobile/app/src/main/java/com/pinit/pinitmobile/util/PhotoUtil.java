package com.pinit.pinitmobile.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.pinit.pinitmobile.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtil {
    private static final String TAG = PhotoUtil.class.getName();

    public static Bitmap getPhotoFromPath(String photoPath) {
        Bitmap bitmap = null;
        if (photoPath != null) {
            try {
                File f = new File(photoPath);
                FileInputStream inputStream = new FileInputStream(f);
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return bitmap;
    }

    public static Byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return byteToObject(bytes);
    }

    private static Byte[] byteToObject(byte[] primitive) {
        Byte[] bytes = new Byte[primitive.length];
        for (int i = 0; i < primitive.length; i++) {
            bytes[i] = primitive[i];
        }
        return bytes;
    }

    public static String savePhotoFromBitmap(Bitmap photo, String dir, String filename) {
        Log.d(TAG, "save photo to file, photo dim: width" + photo.getWidth() + ", height " + photo.getHeight());
        ContextWrapper cw = new ContextWrapper(App.getCtx());
        File directory = cw.getDir(dir, Context.MODE_PRIVATE);
        File file = new File(directory, filename + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    public static Bitmap byteArrayToBitmap(Byte[] photo) {
        byte[] photoPrimitive = byteToPrimitive(photo);
        return BitmapFactory.decodeByteArray(photoPrimitive, 0, photoPrimitive.length);
    }

    public static Bitmap byteArrayToBitmap(byte[] photo) {
        return BitmapFactory.decodeByteArray(photo, 0, photo.length);
    }

    public static byte[] byteToPrimitive(Byte[] bytes) {
        byte[] primitive = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            primitive[i] = bytes[i];
        }
        return primitive;
    }

    public static Bitmap bitmapScale(Bitmap bitmap, int preferredWidth) {
        Log.d(TAG, "scale photo");
        Matrix matrix = new Matrix();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = (float) preferredWidth / (float) width;
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap rotateBitmapToPortraitWithExif(Bitmap bitmap, String path) {
        Log.d(TAG, "rotate photo");
        if (path != null) {
            Bitmap rotated;
            try {
                ExifInterface exif = new ExifInterface(path);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Matrix matrix = new Matrix();
                matrix.setRotate(exifToDegrees(rotation));
                rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
                return bitmap;
            }
            return rotated;
        }
        return bitmap;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }


}
