package com.pinit.pinitmobile.tasks.event;


import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class GetPublicEventsTask extends AbstractAsyncTask<Void, Event[]> {

    public static final int TASK_ID = 7;

    public GetPublicEventsTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester, String
            countryCode, String city) {
        super(progressBar, activity, null, requester, HttpMethod.GET, Event[].class);
        url = Globals.SERVER_URL + Globals.GET_PUBLIC_EVENTS_URL + countryCode + "/" + city + "/" + UserData.getToken()
                .getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Event[] events = (Event[]) responseEntity.getBody();
        App.getEventsDao().saveEventsWithLocations(events);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }
}
