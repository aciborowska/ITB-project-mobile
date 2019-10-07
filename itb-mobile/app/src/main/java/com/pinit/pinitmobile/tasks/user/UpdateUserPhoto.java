package com.pinit.pinitmobile.tasks.user;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Photo;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.PhotoUtil;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserPhoto extends AbstractAsyncTask<Map<String, Byte[]>, Void> {

    public static final int TASK_ID = 11;
    private Bitmap photo;

    public UpdateUserPhoto(ProgressBar progressBar, FragmentActivity activity, Bitmap photo, AsyncTaskCallback requester) {
        super(progressBar, activity, null, requester, HttpMethod.PUT, Void.class);
        url = Globals.SERVER_URL + Globals.UPDATE_PHOTO_URL + UserData.getToken().getToken();
        this.photo = photo;
        Map<String, Byte[]> newUserPhoto = new HashMap<>();
        newUserPhoto.put("photoBig", PhotoUtil.bitmapToByteArray(photo));
        newUserPhoto.put("photoSmall", PhotoUtil.bitmapToByteArray(PhotoUtil.bitmapScale(photo, 100)));
        objectToSend = newUserPhoto;
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        User user = UserData.getUser();
        user.setBigPhoto(new Photo(photo), Globals.USERS_PHOTO_DIR, Globals.USER_PHOTO_BIG);
        user.setSmallPhoto(new Photo(PhotoUtil.bitmapScale(photo, 100)), Globals.USERS_PHOTO_DIR, Globals.User_PHOTO_SMALL);
        UserData.saveUser(user);
        Toast.makeText(activity, R.string.user_data_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }
}