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

public class GetPlusMinusMarkTask extends AbstractAsyncTask<Void, Integer> {

    public static final int TASK_ID = 17;
    private Event event;

    public GetPlusMinusMarkTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester, Event event) {
        super(progressBar, activity, null, requester, HttpMethod.GET, Integer.class);
        url = Globals.SERVER_URL + Globals.GET_PLUS_MINUS_URL_1 + event.getEventId() + "/" + Globals.GET_PLUS_MINUS_URL_2 +
                UserData.getToken().getToken();
        this.event = event;
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Integer result = (Integer) responseEntity.getBody();
        event.setMark(result);
        App.getEventsDao().update(event);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }
}
