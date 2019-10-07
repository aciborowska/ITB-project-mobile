package com.pinit.pinitmobile.tasks.event;

import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.EventType;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class GetEventTypesTask extends AbstractAsyncTask<Void, EventType[]> {

    public static final int TASK_ID = 4;

    public GetEventTypesTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester) {
        super(progressBar, activity, null, requester, HttpMethod.GET, EventType[].class);
        url = Globals.SERVER_URL + Globals.GET_TYPES_URL + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        EventType[] eventTypes = (EventType[]) responseEntity.getBody();
        App.getEventTypesDao().saveAll(eventTypes);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            requester.afterExecute(TASK_ID, code);
        }
    }
}

