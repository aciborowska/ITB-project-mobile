package com.pinit.pinitmobile.model;


import android.graphics.Bitmap;
import android.util.Base64;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.util.PhotoUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {

    public static final int PREFERRED_PHOTO_WIDTH = 300;
    private static final String TAG = Comment.class.getName();
    public static long lastId = 0;
    private Long id;
    private String comment;
    private long date;
    private User author;
    private Photo photo = null;

    public Comment() {
        lastId++;
        id = lastId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap commentPhoto) {
        Bitmap scaledPhoto = PhotoUtil.bitmapScale(commentPhoto, PREFERRED_PHOTO_WIDTH);
        photo = new Photo(scaledPhoto);
        photo.savePhoto(Globals.COMMENT_PHOTO_DIR, String.valueOf(id));
    }

    public static String commentToJSONObject(Comment c) throws JSONException {
        JSONObject json = new JSONObject();
        if (!c.getAuthor().getUsername().isEmpty())
            json.put("username", c.getAuthor().getUsername());
        else json.put("username", c.getAuthor().getEmail());
        json.put("message", c.getComment());
        String photo;
        Photo p = c.getPhoto();
        if (p!= null) {
            photo = Base64.encodeToString(PhotoUtil.byteToPrimitive(p.getPhotoAsByteArray()), Base64.NO_WRAP);
        } else {
            photo = "";
        }
        json.put("photo", photo);
        return json.toString();
    }

    public synchronized static Comment jsonObjectToComment(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        User u = new User();
        u.setUserId(json.getLong("user_id"));
        u.setUsername(json.getString("username"));
        String userPhoto = json.getString("user_photo");
        if(!userPhoto.isEmpty()) {
            u.setPhotoSmall(Base64.decode(userPhoto, Base64.DEFAULT));
        }
        Comment c = new Comment();
        c.setAuthor(u);
        c.setComment(json.getString("message"));
        c.setDate(json.getLong("send_date"));
        String photo = json.getString("photo");
        if(!photo.isEmpty()) {
            c.setPhoto(Base64.decode(photo, Base64.DEFAULT));
        }
        return c;
    }

    private void setPhoto(byte[] primitive) {
        Bitmap commentPhoto = PhotoUtil.byteArrayToBitmap(primitive);
        setPhoto(commentPhoto);
    }
}
