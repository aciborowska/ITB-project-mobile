package com.pinit.pinitmobile.tasks.user;


import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.Photo;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class GetUserPhotoBigTask extends AbstractAsyncTask<Void, Byte[]> {

    public static final int TASK_ID = 8;

    public GetUserPhotoBigTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester) {
        super(progressBar, activity, null, requester, HttpMethod.GET, Byte[].class);
        url = Globals.SERVER_URL + Globals.GET_USER_PHOTO_BIG_URL + +UserData.getToken().getUserId() + "/" + UserData.getToken
                ().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Byte[] photo = (Byte[]) responseEntity.getBody();
        if (photo != null && photo.length > 0) {
            User user = UserData.getUser();
            Photo p = new Photo(photo);
            user.setBigPhoto(p, Globals.USERS_PHOTO_DIR, Globals.USER_PHOTO_BIG);
            p = new Photo(photo);
            user.setSmallPhoto(p, Globals.USERS_PHOTO_DIR, Globals.User_PHOTO_SMALL);
            UserData.saveUser(user);
        }
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            requester.afterExecute(TASK_ID, code);
        }
    }
}
