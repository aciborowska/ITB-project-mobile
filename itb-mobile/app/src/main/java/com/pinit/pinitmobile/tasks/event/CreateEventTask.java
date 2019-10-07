package com.pinit.pinitmobile.tasks.event;

import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class CreateEventTask extends AbstractAsyncTask<Event, Long> {

    public static final int TASK_ID = 2;
    public CreateEventTask(ProgressBar progressBar, FragmentActivity activity, Event event, AsyncTaskCallback requester,
                           HttpMethod method, Class aClass) {
        super(progressBar, activity, event, requester, method, aClass);
        url = Globals.SERVER_URL + Globals.CREATE_EVENT_URL + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Long id = (Long) responseEntity.getBody();
        objectToSend.setEventId(id);
        App.getEventsDao().saveWithLocation(objectToSend);
        Toast.makeText(activity, activity.getString(R.string.event_added), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void notifyRequester(int code) {
        requester.afterExecute(TASK_ID,code );
    }
}